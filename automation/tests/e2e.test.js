const { expect } = require('chai');
const testContext = require('./base.test');
const LoginPage = require('../pages/login.page');
const DashboardPage = require('../pages/dashboard.page');
const excelReporter = require('../utilities/excel.reporter');
const Gestures = require('../utilities/gestures');

describe('End-to-End Functional Distress Suite', function() {
  let driver;
  let loginPage;
  let dashboardPage;

  before(async function() {
    driver = testContext.getDriver();
    loginPage = new LoginPage(driver);
    dashboardPage = new DashboardPage(driver);

    // Ensure we are logged in and on the Dashboard page
    const isDashboard = await dashboardPage.isMonitoringActive();
    if (!isDashboard) {
      await loginPage.performLogin('amulyaammu316@gmail.com', 'correctpassword123');
      await dashboardPage.waitForElementDisplayed(dashboardPage.appSlogan);
    }
  });

  it('TC_E2E_01: Verify full bottom navigation transitions', async function() {
    excelReporter.addStepLog(this.test.title, 'Navigate Tab', 'Click', 'Clicking AI Status tab');
    await dashboardPage.navigateToAiStatus();
    
    excelReporter.addStepLog(this.test.title, 'Navigate Tab', 'Click', 'Clicking AI Chat tab');
    await dashboardPage.navigateToAiChat();
    
    excelReporter.addStepLog(this.test.title, 'Navigate Tab', 'Click', 'Clicking Settings tab');
    await dashboardPage.navigateToSettings();
    
    excelReporter.addStepLog(this.test.title, 'Navigate Tab', 'Click', 'Returning to Home dashboard');
    await dashboardPage.navigateToHome();
    
    const isDashboard = await dashboardPage.isMonitoringActive();
    expect(isDashboard).to.be.true;
  });

  it('TC_E2E_02: Verify Manual Panic SOS Alarm activation & dismissal', async function() {
    excelReporter.addStepLog(this.test.title, 'Trigger SOS', 'Action', 'Clicking floating Panic SOS button');
    await dashboardPage.triggerPanicSosViaFab();
    
    excelReporter.addStepLog(this.test.title, 'Verify Alarm', 'Display', 'Checking for active alarm warning');
    const isSosActive = await driver.$(dashboardPage.cancelSosBtn).isDisplayed();
    expect(isSosActive).to.be.true;

    // Check for countdown display
    const countdownVal = await dashboardPage.getText(dashboardPage.sosCountdown);
    expect(parseInt(countdownVal, 10)).to.be.within(0, 10);

    excelReporter.addStepLog(this.test.title, 'Cancel SOS', 'Action', 'Dismissing active panic alarm as false trigger');
    await dashboardPage.cancelSosAlert();

    excelReporter.addStepLog(this.test.title, 'Verify Reset', 'Success', 'Verifying Central SOS button is reset');
    const isSosStillActive = await driver.$(dashboardPage.cancelSosBtn).isDisplayed().catch(() => false);
    expect(isSosStillActive).to.be.false;
  });

  it('TC_E2E_03: Verify AI simulated distress scream triggers auto-SOS', async function() {
    excelReporter.addStepLog(this.test.title, 'Simulation', 'Action', 'Clicking Simulate Scream distress button');
    
    // Navigate to AI Status screen first
    await dashboardPage.navigateToAiStatus();
    
    // Perform swipe up gesture to ensure simulated buttons are visible
    await Gestures.swipeUp(driver, 0.7);
    
    await dashboardPage.simulateScreamDistress();

    excelReporter.addStepLog(this.test.title, 'Verify Auto-SOS', 'Success', 'Asserting safety level drops and alarm is triggered');
    
    // Wait for the SOS screen overlay to appear, confirming alarm is triggered
    await dashboardPage.waitForElementDisplayed(dashboardPage.cancelSosBtn);
    const isCancelBtnDisplayed = await driver.$(dashboardPage.cancelSosBtn).isDisplayed();
    expect(isCancelBtnDisplayed).to.be.true;
    
    // Clean up active alarm
    await dashboardPage.cancelSosAlert();
  });
});
