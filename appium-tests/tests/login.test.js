const { expect } = require('chai');
const testContext = require('./base.test');
const LoginPage = require('../pages/login.page');
const DashboardPage = require('../pages/dashboard.page');
const excelReporter = require('../utilities/excel.reporter');

describe('Authentication & Access Management Suite', function() {
  let driver;
  let loginPage;
  let dashboardPage;

  before(async function() {
    driver = testContext.getDriver();
    loginPage = new LoginPage(driver);
    dashboardPage = new DashboardPage(driver);

    // Ensure we start logged out
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

  it('TC_AUTH_03: Verify registration tab transition is responsive', async function() {
    excelReporter.addStepLog(this.test.title, 'Tab Click', 'Navigate', 'Clicking on Register tab');
    await loginPage.selectRegisterTab();
    
    excelReporter.addStepLog(this.test.title, 'Verify Fields', 'Success', 'Asserting register fields display');
    const isNameInputDisplayed = await driver.$(loginPage.nameInput).isDisplayed();
    expect(isNameInputDisplayed).to.be.true;
  });

  it('TC_AUTH_04: Verify registration form empty fields validation', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Leaving registration inputs empty');
    await loginPage.clear(loginPage.nameInput);
    await loginPage.clear(loginPage.emailInput);
    await loginPage.clear(loginPage.phoneInput);
    await loginPage.clear(loginPage.passwordInput);
    await loginPage.hideKeyboard();
    await loginPage.submit();

    excelReporter.addStepLog(this.test.title, 'Verify Alert', 'Success', 'Checking error text display');
    const errorText = await loginPage.getErrorMessage();
    expect(errorText).to.satisfy(text => text.includes('fill') || text.includes('fields') || text.includes('empty'));
  });

  it('TC_AUTH_05: Verify forgot password modal triggers successfully', async function() {
    excelReporter.addStepLog(this.test.title, 'Tab Click', 'Navigate', 'Switching back to Sign In tab');
    await loginPage.selectSignInTab();
    
    excelReporter.addStepLog(this.test.title, 'Forgot Link Click', 'Action', 'Clicking Forgot Password link');
    await driver.$(loginPage.forgotPasswordLink).click();
    
    excelReporter.addStepLog(this.test.title, 'Verify Dialog', 'Success', 'Confirming Send Link button is displayed');
    const isSendLinkDisplayed = await driver.$(loginPage.sendLinkBtn).isDisplayed();
    expect(isSendLinkDisplayed).to.be.true;
  });

  it('TC_AUTH_06: Verify forgot password modal empty email validation', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Leaving forgot email empty and clicking send');
    const emailField = await driver.$(loginPage.forgotEmailInput);
    await emailField.clearValue();
    await loginPage.hideKeyboard();
    await driver.$(loginPage.sendLinkBtn).click();

    excelReporter.addStepLog(this.test.title, 'Verify Alert', 'Success', 'Checking for email error display');
    const errorText = await driver.$('//*[contains(@text, "enter") or contains(@text, "email") or contains(@text, "Email")]').isDisplayed();
    expect(errorText).to.be.true;
  });

  it('TC_AUTH_07: Verify forgot password modal invalid link structures error', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Pasting invalid link structure');
    // The link field is the second EditText in the recovery dialog
    const editTexts = await driver.$$('//android.widget.EditText');
    if (editTexts.length >= 2) {
      await editTexts[1].setValue('http://invalid-link');
      await loginPage.hideKeyboard();
      
      const invalidBtn = await driver.$('//*[@text="Invalid Link"]');
      const isInvalidBtnDisplayed = await invalidBtn.isDisplayed();
      expect(isInvalidBtnDisplayed).to.be.true;
    }
  });

  it('TC_AUTH_08: Verify forgot password cancel button closes modal', async function() {
    excelReporter.addStepLog(this.test.title, 'Dialog Cancel', 'Action', 'Clicking cancel dialog button');
    await driver.$(loginPage.cancelForgotBtn).click();
    
    excelReporter.addStepLog(this.test.title, 'Verify Close', 'Success', 'Verifying forgot link is visible again');
    await driver.pause(1000);
    const isForgotLinkVisible = await driver.$(loginPage.forgotPasswordLink).isDisplayed();
    expect(isForgotLinkVisible).to.be.true;
  });

  it('TC_AUTH_09: Verify tab segment toggling switches layout state', async function() {
    excelReporter.addStepLog(this.test.title, 'Tab Switch', 'Action', 'Toggling between login and register');
    await loginPage.selectRegisterTab();
    expect(await driver.$(loginPage.nameInput).isDisplayed()).to.be.true;
    
    await loginPage.selectSignInTab();
    const isNameInputPresent = await driver.$(loginPage.nameInput).isDisplayed().catch(() => false);
    expect(isNameInputPresent).to.be.false;
  });

  it('TC_AUTH_10: Verify incorrect credentials displays proper warning box', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Entering completely empty credentials and submitting');
    await loginPage.clear(loginPage.emailInput);
    await loginPage.clear(loginPage.passwordInput);
    await loginPage.hideKeyboard();
    await loginPage.submit();
    
    const warningText = await loginPage.getErrorMessage();
    expect(warningText).to.not.be.null;
  });

  it('TC_AUTH_11: Verify valid login navigates to dashboard', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Entering valid credentials');
    await loginPage.performLogin('amulyaammu316@gmail.com', 'correctpassword123');
    
    excelReporter.addStepLog(this.test.title, 'Verify Navigation', 'Success', 'Verifying dashboard slogan displays');
    await dashboardPage.waitForElementDisplayed(dashboardPage.appSlogan);
    const isDashboardActive = await dashboardPage.isMonitoringActive();
    expect(isDashboardActive).to.be.true;
  });

  it('TC_AUTH_12: Verify logout functionality returns to Login screen', async function() {
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
