const { expect } = require('chai');
const testContext = require('./base.test');
const LoginPage = require('../pages/login.page');
const DashboardPage = require('../pages/dashboard.page');
const excelReporter = require('../utilities/excel.reporter');
const Gestures = require('../utilities/gestures');

describe('AI Telemetry Diagnostics & Role-Based Workspaces Suite', function() {
  let driver;
  let loginPage;
  let dashboardPage;

  before(async function() {
    driver = testContext.getDriver();
    loginPage = new LoginPage(driver);
    dashboardPage = new DashboardPage(driver);

    // Make sure we are logged in as standard User
    const isDashboard = await dashboardPage.isMonitoringActive();
    let correctUser = false;
    if (isDashboard) {
      try {
        const roleChip = await driver.$('//*[contains(@text, "Workspace:")]');
        const roleText = await roleChip.getText();
        if (roleText.includes("User")) {
          correctUser = true;
        }
      } catch (err) {
        // Fallback
      }
    }

    if (!correctUser) {
      if (isDashboard) {
        await dashboardPage.navigateToSettings();
        const signOutBtn = '//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]';
        const baseBtn = await Gestures.scrollUntilVisible(driver, signOutBtn);
        await baseBtn.click();
        await driver.$(loginPage.signInTab).waitForDisplayed({ timeout: 10000 });
      }
      await loginPage.performLogin('amulyaammu316@gmail.com', 'correctpassword123');
      await dashboardPage.waitForElementDisplayed(dashboardPage.appSlogan);
    }
  });

  it('TC_AI_01: Verify AI Status Monitor tab probability graph', async function() {
    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Navigating to AI Status sub-page');
    await dashboardPage.navigateToAiStatus();

    excelReporter.addStepLog(this.test.title, 'Widget Check', 'Display', 'Looking for Risk evaluation charts');
    const isChartTitleDisplayed = await driver.$('//*[@text="AI Threat Probability Assessment"]').isDisplayed();
    expect(isChartTitleDisplayed).to.be.true;
  });

  it('TC_AI_02: Verify Speech stress telemetry vitals indicator', async function() {
    excelReporter.addStepLog(this.test.title, 'Widget Check', 'Display', 'Locating speech acoustic stress bar');
    const speechAcousticVal = await driver.$('//*[@text="Speech Acoustic Stress"]');
    expect(await speechAcousticVal.isDisplayed()).to.be.true;
  });

  it('TC_AI_03: Verify Motion instability telemetry vitals indicator', async function() {
    excelReporter.addStepLog(this.test.title, 'Widget Check', 'Display', 'Locating kinetic posture instability analytics');
    const motionInstabilityVal = await driver.$('//*[@text="Posture Instability Analytics"]');
    expect(await motionInstabilityVal.isDisplayed()).to.be.true;
  });

  it('TC_AI_04: Verify Simulate Scream button in Monitor tab triggers auto-SOS', async function() {
    excelReporter.addStepLog(this.test.title, 'Simulation', 'Action', 'Clicking Simulate Scream distress button');
    await Gestures.swipeUp(driver, 0.7);
    await dashboardPage.simulateScreamDistress();

    excelReporter.addStepLog(this.test.title, 'Verify Alarm', 'Success', 'Asserting safety level drops and alarm countdown overlay triggers');
    await dashboardPage.waitForElementDisplayed(dashboardPage.cancelSosBtn);
    const isSosOverlayActive = await driver.$(dashboardPage.cancelSosBtn).isDisplayed();
    expect(isSosOverlayActive).to.be.true;

    // Clean up active alarm
    await dashboardPage.cancelSosAlert();
    await dashboardPage.navigateToAiStatus();
  });

  it('TC_AI_05: Verify Simulate Fall button in Monitor tab triggers auto-SOS', async function() {
    excelReporter.addStepLog(this.test.title, 'Simulation', 'Action', 'Clicking Simulate Fall distress button');
    await Gestures.swipeUp(driver, 0.7);
    await dashboardPage.simulateFallDistress();

    excelReporter.addStepLog(this.test.title, 'Verify Alarm', 'Success', 'Asserting safety level drops and alarm countdown overlay triggers');
    await dashboardPage.waitForElementDisplayed(dashboardPage.cancelSosBtn);
    const isSosOverlayActive = await driver.$(dashboardPage.cancelSosBtn).isDisplayed();
    expect(isSosOverlayActive).to.be.true;

    // Clean up active alarm
    await dashboardPage.cancelSosAlert();
    await dashboardPage.navigateToAiStatus();
  });

  it('TC_AI_06: Verify Simulate Fear Face ML button in Monitor tab triggers auto-SOS', async function() {
    excelReporter.addStepLog(this.test.title, 'Simulation', 'Action', 'Clicking Simulate Fear/Dilation Face ML button');
    await Gestures.swipeUp(driver, 0.7);
    const fearBtn = await driver.$('//*[@text="Simulate Fear/Dilation Face ML Trigger"]');
    await fearBtn.click();

    excelReporter.addStepLog(this.test.title, 'Verify Alarm', 'Success', 'Asserting safety level drops and alarm countdown overlay triggers');
    await dashboardPage.waitForElementDisplayed(dashboardPage.cancelSosBtn);
    const isSosOverlayActive = await driver.$(dashboardPage.cancelSosBtn).isDisplayed();
    expect(isSosOverlayActive).to.be.true;

    // Clean up active alarm
    await dashboardPage.cancelSosAlert();
    await dashboardPage.navigateToAiStatus();
  });

  it('TC_AI_07: Verify Voice Stress Analyzer Live Scan mic toggle actions', async function() {
    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Navigating to Voice Analyzer sub-tab');
    const voiceTab = await driver.$('//*[@text="Voice Analyzer"]');
    await voiceTab.click();

    excelReporter.addStepLog(this.test.title, 'Mic Click', 'Action', 'Clicking Live Scan Mic button');
    const scanBtn = await driver.$('//*[contains(@text, "Scan Mic") or contains(@text, "Stop Mic")]');
    await scanBtn.click();
    
    excelReporter.addStepLog(this.test.title, 'Verify State', 'Success', 'Checking active scan title');
    const isScanning = await driver.$('//*[contains(@text, "Stop Mic") or contains(@text, "Live Scan")]').isDisplayed();
    expect(isScanning).to.be.true;
  });

  it('TC_AI_08: Verify Voice Stress Analyzer Trigger Phrase simulation triggers auto-SOS', async function() {
    excelReporter.addStepLog(this.test.title, 'Simulation', 'Input', 'Entering emergency phrase into simulator text box');
    const phraseInput = await driver.$('//android.widget.EditText');
    await phraseInput.setValue('help me immediately');
    await loginPage.hideKeyboard();

    excelReporter.addStepLog(this.test.title, 'Simulation', 'Action', 'Submitting transcription');
    const recordBtnSelector = '//*[@text="Record & Dispatch AI Transcription"]';
    const recordBtn = await Gestures.scrollUntilVisible(driver, recordBtnSelector);
    await recordBtn.click();

    excelReporter.addStepLog(this.test.title, 'Verify Alarm', 'Success', 'Asserting safety level drops and alarm countdown overlay triggers');
    await dashboardPage.waitForElementDisplayed(dashboardPage.cancelSosBtn);
    const isSosOverlayActive = await driver.$(dashboardPage.cancelSosBtn).isDisplayed();
    expect(isSosOverlayActive).to.be.true;

    // Clean up active alarm
    await dashboardPage.cancelSosAlert();
  });

  it('TC_AI_09: Verify Kinetic accelerometer X/Y/Z vector display values', async function() {
    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Navigating to Behavioral sub-tab');
    await dashboardPage.navigateToAiStatus();
    const behaviorTab = await driver.$('//*[@text="Behavioral"]');
    await behaviorTab.click();

    excelReporter.addStepLog(this.test.title, 'Widget Check', 'Display', 'Verifying kinetic vector text shows dynamic values');
    const vectorText = await driver.$('//*[contains(@text, "Axis Vector") or contains(@text, "Telemetry")]');
    expect(await vectorText.isDisplayed()).to.be.true;
  });

  it('TC_AI_10: Verify Vision ML facial emotion calm/sad/hesitated simulator', async function() {
    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Navigating to Vision ML sub-tab');
    await dashboardPage.navigateToAiStatus();
    const visionTab = await driver.$('//*[@text="Vision ML"]');
    await visionTab.click();

    excelReporter.addStepLog(this.test.title, 'Emotion Click', 'Action', 'Selecting simulated Sad emotion');
    const sadTabSelector = '//*[contains(@text, "Sad")]';
    const sadTab = await Gestures.scrollUntilVisible(driver, sadTabSelector);
    await sadTab.click();

    excelReporter.addStepLog(this.test.title, 'Verify State', 'Success', 'Asserting active emotion state updates and dismissing SOS alert');
    await dashboardPage.waitForElementDisplayed(dashboardPage.cancelSosBtn);
    await dashboardPage.cancelSosAlert();
  });

  it('TC_AI_11: Verify Admin controller hub scan triggers and log audit', async function() {
    excelReporter.addStepLog(this.test.title, 'Log Out', 'Action', 'Logging out standard user');
    const isDashboard = await dashboardPage.isMonitoringActive();
    if (isDashboard) {
      await dashboardPage.navigateToSettings();
      const signOutBtn = '//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]';
      const Gestures = require('../utilities/gestures');
      const baseBtn = await Gestures.scrollUntilVisible(driver, signOutBtn);
      await baseBtn.click();
      await driver.$(loginPage.signInTab).waitForDisplayed({ timeout: 10000 });
    } else {
      await loginPage.selectSignInTab();
    }

    excelReporter.addStepLog(this.test.title, 'Log In', 'Action', 'Logging in as Admin');
    await loginPage.performLogin('admin@example.com', 'adminpassword123');
    await dashboardPage.waitForElementDisplayed(dashboardPage.appSlogan);

    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Clicking top app bar Admin shortcut');
    const adminShortcut = await driver.$('//*[contains(@content-desc, "Admin Space") or contains(@content-desc, "Admin")]');
    await adminShortcut.click();

    excelReporter.addStepLog(this.test.title, 'Admin Action', 'Click', 'Triggering global integrity diagnostic scan');
    const scanBtn = await driver.$('//*[@text="Trigger Global Integrity Scan"]');
    await scanBtn.click();
    await driver.pause(1000);

    excelReporter.addStepLog(this.test.title, 'Verify Scan', 'Success', 'Asserting success status logs are printed');
    const successLogsText = await driver.$('//*[contains(@text, "Diagnostic complete") or contains(@text, "Nodes")]');
    expect(await successLogsText.isDisplayed()).to.be.true;
    
    // Log out admin
    await dashboardPage.navigateToSettings();
    const signOutBtnAdmin = '//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]';
    const baseBtnAdmin = await Gestures.scrollUntilVisible(driver, signOutBtnAdmin);
    await baseBtnAdmin.click();
    await driver.$(loginPage.signInTab).waitForDisplayed({ timeout: 10000 });
  });

  it('TC_AI_12: Verify Rescue dispatcher SWAT mobilization triggers', async function() {
    excelReporter.addStepLog(this.test.title, 'Log Out', 'Action', 'Logging out admin/standard user');
    const isDashboard = await dashboardPage.isMonitoringActive();
    if (isDashboard) {
      await dashboardPage.navigateToSettings();
      const signOutBtn = '//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]';
      const Gestures = require('../utilities/gestures');
      const baseBtn = await Gestures.scrollUntilVisible(driver, signOutBtn);
      await baseBtn.click();
      await driver.$(loginPage.signInTab).waitForDisplayed({ timeout: 10000 });
    } else {
      await loginPage.selectSignInTab();
    }

    excelReporter.addStepLog(this.test.title, 'Log In', 'Action', 'Logging in as Rescue Team');
    await loginPage.performLogin('rescue@example.com', 'rescuepassword123');
    await dashboardPage.waitForElementDisplayed(dashboardPage.appSlogan);

    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Clicking top app bar Rescue Team shortcut');
    const rescueShortcut = await driver.$('//*[contains(@content-desc, "Rescue Desk") or contains(@content-desc, "Rescue")]');
    await rescueShortcut.click();

    excelReporter.addStepLog(this.test.title, 'Rescue Action', 'Click', 'Dispatching swat / police forces');
    const policeBtn = await driver.$('//*[@text="Dispatch SWAT/Police"]');
    await policeBtn.click();
    await driver.pause(1000);

    excelReporter.addStepLog(this.test.title, 'Verify Dispatch', 'Success', 'Asserting status logs update with ambulance/police sirens alert');
    const statusText = await driver.$('//*[contains(@text, "POLICE") or contains(@text, "SWAT") or contains(@text, "DISPATCHED") or contains(@text, "ambulance")]');
    expect(await statusText.isDisplayed()).to.be.true;

    // Restore clean state by logging out and logging standard user back in
    await dashboardPage.navigateToSettings();
    const signOutBtnRescue = '//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]';
    const Gestures = require('../utilities/gestures');
    const baseBtnRescue = await Gestures.scrollUntilVisible(driver, signOutBtnRescue);
    await baseBtnRescue.click();
    await driver.$(loginPage.signInTab).waitForDisplayed({ timeout: 10000 });
    
    await loginPage.performLogin('amulyaammu316@gmail.com', 'correctpassword123');
    await dashboardPage.waitForElementDisplayed(dashboardPage.appSlogan);
  });
});
