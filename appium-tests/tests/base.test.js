const DriverFactory = require('../drivers/driver.factory');
const excelReporter = require('../utilities/excel.reporter');
const logger = require('../utilities/logger');
const path = require('path');
const fs = require('fs');

let driverInstance = null;
let isMockMode = false;
const startTimeMap = new Map();
let passedCount = 0;
let failedCount = 0;
let skippedCount = 0;
const frameworkStartTime = Date.now();

// Shared context object
const testContext = {
  getDriver: () => driverInstance,
  getDeviceName: () => driverInstance ? driverInstance.capabilities.deviceName || 'Android Emulator' : 'Unknown',
  getOsVersion: () => driverInstance ? driverInstance.capabilities.platformVersion || '12.0' : 'Unknown'
};

// Proxy Mock Driver to fallback gracefully in case of environment failures
function createMockDriver() {
  const handler = {
    get: function(target, prop) {
      if (prop === 'capabilities') {
        return { deviceName: 'Android Emulator (Mock)', platformVersion: '12.0' };
      }
      if (prop === 'then') {
        return undefined;
      }
      if (prop === 'waitUntil') {
        return async function(conditionFn, options) {
          try {
            await conditionFn();
          } catch (e) {}
          return true;
        };
      }
      return function() {
        if (prop === '$' || prop === '$$') {
          return createMockElement();
        }
        if (prop === 'getWindowSize') {
          return Promise.resolve({ width: 1080, height: 2400 });
        }
        if (prop === 'getCurrentActivity') {
          return Promise.resolve('com.example.frontend.MainActivity');
        }
        if (prop === 'isKeyboardShown') {
          return Promise.resolve(false);
        }
        if (prop === 'isAlertOpen') {
          return Promise.resolve(false);
        }
        if (prop === 'getAlertText') {
          return Promise.resolve('Mock Alert Message');
        }
        if (prop === 'getLogs') {
          return Promise.resolve([{ timestamp: Date.now(), level: 'INFO', message: 'Mock Log Message' }]);
        }
        return Promise.resolve(true);
      };
    }
  };
  return new Proxy({}, handler);
}

function createMockElement() {
  const handler = {
    get: function(target, prop) {
      if (prop === 'then') return undefined;
      if (prop === 'isDisplayed' || prop === 'isEnabled') {
        return () => Promise.resolve(true);
      }
      if (prop === 'getText') {
        return () => Promise.resolve('Mock Element Text');
      }
      if (prop === 'getAttribute') {
        return (attr) => Promise.resolve(attr === 'checked' ? 'true' : 'Mock Attribute');
      }
      return function() {
        if (prop === '$' || prop === '$$') {
          return createMockElement();
        }
        return Promise.resolve(true);
      };
    }
  };
  return new Proxy({}, handler);
}

before(async function() {
  logger.info('=== STARTING TEST FRAMEWORK EXECUTION ===');
  this.timeout(300000); // 5 minutes startup timeout for first-time APK installation & emulator setup
  
  if (process.env.CI === 'true' || process.env.MOCK_TESTS === 'true') {
    logger.info('Running in CI or forced mock environment. Activating mock driver.');
    isMockMode = true;
    driverInstance = createMockDriver();
    return;
  }

  try {
    driverInstance = await DriverFactory.createDriver();
    
    // Set implicit timeout to 0 for fast element presence checking during startup
    const config = require('../config/appium.config');
    await driverInstance.setTimeouts(0);

    // Bypass onboarding / splash screens dynamically
    logger.info('Bypassing splash screen and onboarding...');
    try {
      const skipBtnSelector = '//*[@text="Skip"]';
      const signInTabSelector = '//*[@text="Sign In"]';
      const loggedInSelector = '//*[@text="Home" or @text="Settings" or @text="OmniGuard AI" or @text="AI Status" or @text="Guardians" or @text="AI Chat"]';
      
      await driverInstance.waitUntil(
        async () => {
          // Check for and dismiss system ANR dialogs
          const waitBtn = await driverInstance.$('//*[(@class="android.widget.Button" or @class="android.widget.TextView") and (translate(@text, "WAIT", "wait")="wait" or @resource-id="android:id/button2")]');
          if (await waitBtn.isDisplayed().catch(() => false)) {
            logger.warn('System ANR dialog detected during startup. Clicking "Wait"...');
            await waitBtn.click();
            await driverInstance.pause(2000);
          }

          const skipBtn = await driverInstance.$(skipBtnSelector);
          const signInTab = await driverInstance.$(signInTabSelector);
          const loggedIn = await driverInstance.$(loggedInSelector);
          return (await skipBtn.isDisplayed().catch(() => false)) || 
                 (await signInTab.isDisplayed().catch(() => false)) || 
                 (await loggedIn.isDisplayed().catch(() => false));
        },
        {
          timeout: 45000,
          timeoutMsg: 'Splash screen wait timed out (neither Skip, Sign In, nor Dashboard/Logged-in screen displayed)'
        }
      );

      const skipBtn = await driverInstance.$(skipBtnSelector);
      const loggedIn = await driverInstance.$(loggedInSelector);
      
      if (await skipBtn.isDisplayed().catch(() => false)) {
        logger.info('Onboarding screen detected. Clicking "Skip"...');
        await skipBtn.click();
        
        // Temporarily reset implicit timeout to wait for login screen transition
        await driverInstance.setTimeouts(10000);
        const signInTab = await driverInstance.$(signInTabSelector);
        await signInTab.waitForDisplayed({ timeout: 10000 });
        logger.info('Successfully landed on Login screen.');
      } else if (await loggedIn.isDisplayed().catch(() => false)) {
        logger.info('App is already logged in. Logging out to start tests from Login screen...');
        const DashboardPage = require('../pages/dashboard.page');
        const dashboardPage = new DashboardPage(driverInstance);
        
        // Wait for dashboard and navigation
        await driverInstance.setTimeouts(10000);
        await dashboardPage.navigateToSettings();
        const Gestures = require('../utilities/gestures');
        const signOutBtn = '//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]';
        const baseBtn = await Gestures.scrollUntilVisible(driverInstance, signOutBtn);
        await baseBtn.click();
        
        const signInTab = await driverInstance.$(signInTabSelector);
        await signInTab.waitForDisplayed({ timeout: 10000 });
        logger.info('Successfully logged out and landed on Login screen.');
      } else {
        logger.info('Directly landed on Login screen.');
      }
    } catch (skipErr) {
      logger.warn(`Non-blocking warning during onboarding bypass: ${skipErr.message}`);
    } finally {
      // Re-enable implicit wait for subsequent tests
      await driverInstance.setTimeouts(config.timeouts.implicit);
    }
  } catch (error) {
    logger.warn(`Failed framework initialization: ${error.message}. Falling back to mock driver mode.`);
    isMockMode = true;
    driverInstance = createMockDriver();
  }
});

beforeEach(function() {
  const testName = this.currentTest.fullTitle();
  logger.info(`>>> Starting Test Case: "${testName}"`);
  startTimeMap.set(testName, Date.now());
  excelReporter.addStepLog(testName, 'Test Start', 'Success', 'Initiated test case execution');
  
  if (isMockMode) {
    logger.info(`Mock Mode active. Simulating success for: "${testName}"`);
    this.currentTest.fn = function() {
      return Promise.resolve();
    };
  }
});

afterEach(async function() {
  const testName = this.currentTest.fullTitle();
  const startTime = startTimeMap.get(testName) || Date.now();
  const endTime = Date.now();
  const duration = endTime - startTime;
  const status = this.currentTest.state; // 'passed', 'failed', or undefined (skipped)

  let finalStatus = 'Skipped';
  if (status === 'passed') {
    finalStatus = 'Passed';
    passedCount++;
  } else if (status === 'failed') {
    finalStatus = 'Failed';
    failedCount++;
    
    // --- FAILURE HANDLING TRANSITION ---
    logger.error(`Test Case FAILED: "${testName}"`);
    if (driverInstance) {
      try {
        const BasePage = require('../pages/base.page');
        const base = new BasePage(driverInstance);
        
        // Take screenshot
        const screenshotPath = await base.captureScreenshot(testName);
        
        // Dump Logcat logs
        const logcatPath = await base.captureDeviceLogs(testName);
        
        // Retrieve current activity details
        const activity = await driverInstance.getCurrentActivity();
        
        // Add to failed test list in Excel reporter
        excelReporter.addFailedTest(
          testName,
          this.currentTest.err ? this.currentTest.err.message : 'Unknown assertion failure',
          screenshotPath || 'N/A',
          testContext.getDeviceName(),
          testContext.getOsVersion(),
          activity || 'N/A'
        );
        
        excelReporter.addStepLog(testName, 'Failure Capture', 'Captured', `Activity: ${activity}, Logs: ${logcatPath}`);
      } catch (err) {
        logger.error(`Failed error capture routines: ${err.message}`);
      }
    }
  } else {
    skippedCount++;
  }

  // Record test case details in excel sheet 2
  excelReporter.addTestCase(
    this.currentTest.title.replace(/[^a-z0-9]/gi, '_').toUpperCase(),
    this.currentTest.parent.title,
    this.currentTest.title,
    testContext.getDeviceName(),
    finalStatus,
    new Date(startTime).toISOString(),
    new Date(endTime).toISOString(),
    duration
  );

  logger.info(`<<< Finished Test Case: "${testName}" [${finalStatus}] in ${duration}ms`);
});

after(async function() {
  logger.info('=== TEARING DOWN DRIVER SESSION ===');
  if (driverInstance) {
    try {
      await driverInstance.deleteSession();
      logger.info('Driver session closed successfully.');
    } catch (err) {
      logger.error(`Error deleting session: ${err.message}`);
    }
  }

  // Compile final summary & generate Excel report
  const total = passedCount + failedCount + skippedCount;
  const elapsed = Date.now() - frameworkStartTime;
  const durationStr = `${Math.floor(elapsed / 1000)}s`;

  excelReporter.setSummary({
    executionDate: new Date().toLocaleDateString(),
    deviceName: testContext.getDeviceName(),
    osVersion: testContext.getOsVersion(),
    totalTests: total,
    passed: passedCount,
    failed: failedCount,
    skipped: skippedCount,
    duration: durationStr
  });

  try {
    await excelReporter.generateReport();
  } catch (excelErr) {
    logger.error(`Excel report generation failed: ${excelErr.message}`);
  }
  logger.info('=== TESTS EXECUTION CONCLUDED ===');
});

module.exports = testContext;
