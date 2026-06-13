const { expect } = require('chai');
const testContext = require('./base.test');
const LoginPage = require('../pages/login.page');
const DashboardPage = require('../pages/dashboard.page');
const excelReporter = require('../utilities/excel.reporter');

describe('Authentication Testing Suite', function() {
  let driver;
  let loginPage;
  let dashboardPage;

  before(async function() {
    driver = testContext.getDriver();
    loginPage = new LoginPage(driver);
    dashboardPage = new DashboardPage(driver);

    // If already logged in, log out first so we can run authentication tests from login screen
    const isDashboard = await dashboardPage.isMonitoringActive();
    if (isDashboard) {
      await dashboardPage.navigateToSettings();
      const Gestures = require('../utilities/gestures');
      const signOutBtn = '//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]';
      const baseBtn = await Gestures.scrollUntilVisible(driver, signOutBtn);
      await baseBtn.click();
      await driver.$(loginPage.signInTab).waitForDisplayed({ timeout: 10000 });
    }
  });

  it('TC_AUTH_01: Verify empty credential validations', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Leaving email & password empty');
    await loginPage.selectSignInTab();
    
    // Clear fields if populated
    await loginPage.clear(loginPage.emailInput);
    await loginPage.clear(loginPage.passwordInput);
    await loginPage.hideKeyboard();
    
    await loginPage.submit();
    
    excelReporter.addStepLog(this.test.title, 'Submit Click', 'Verify', 'Checking error validation display');
    const errorText = await loginPage.getErrorMessage();
    expect(errorText).to.include('credentials');
  });

  it('TC_AUTH_02: Verify invalid credentials display error message', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Entering invalid username and password');
    await loginPage.performLogin('invalid.user@example.com', 'wrongpassword');
    
    excelReporter.addStepLog(this.test.title, 'Submit Click', 'Verify', 'Checking login failed display');
    const errorText = await loginPage.getErrorMessage();
    expect(errorText).to.not.be.null;
    expect(errorText).to.satisfy(text => text.includes('failed') || text.includes('invalid') || text.includes('credentials'));
  });

  it('TC_AUTH_03: Verify valid login navigates to dashboard', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Entering valid credentials');
    
    // Using sample seed credentials
    await loginPage.performLogin('amulyaammu316@gmail.com', 'correctpassword123');
    
    excelReporter.addStepLog(this.test.title, 'Verify Navigation', 'Success', 'Verifying dashboard container displays');
    await dashboardPage.waitForElementDisplayed(dashboardPage.appSlogan);
    const isDashboardActive = await dashboardPage.isMonitoringActive();
    expect(isDashboardActive).to.be.true;
  });

  it('TC_AUTH_04: Verify logout functionality returns to Login screen', async function() {
    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Going to profile setting section');
    await dashboardPage.navigateToSettings();
    
    excelReporter.addStepLog(this.test.title, 'Logout Action', 'Click', 'Clicking sign out button');
    const signOutBtn = '//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]';
    const Gestures = require('../utilities/gestures');
    const baseBtn = await Gestures.scrollUntilVisible(driver, signOutBtn);
    await baseBtn.click();
    
    excelReporter.addStepLog(this.test.title, 'Verify Redirect', 'Success', 'Verifying redirected back to Login tab');
    const isSignInTabDisplayed = await driver.$(loginPage.signInTab).isDisplayed();
    expect(isSignInTabDisplayed).to.be.true;
  });
});
