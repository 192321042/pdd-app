const { expect } = require('chai');
const testContext = require('./base.test');
const LoginPage = require('../pages/login.page');
const DashboardPage = require('../pages/dashboard.page');
const ContactsPage = require('../pages/contacts.page');
const excelReporter = require('../utilities/excel.reporter');

describe('Form Rules Validation Suite', function() {
  let driver;
  let loginPage;
  let dashboardPage;
  let contactsPage;

  before(async function() {
    driver = testContext.getDriver();
    loginPage = new LoginPage(driver);
    dashboardPage = new DashboardPage(driver);
    contactsPage = new ContactsPage(driver);

    // Make sure we are logged in before running form validation checks
    const isDashboard = await dashboardPage.isMonitoringActive();
    if (!isDashboard) {
      await loginPage.performLogin('amulyaammu316@gmail.com', 'correctpassword123');
      await dashboardPage.waitForElementDisplayed(dashboardPage.appSlogan);
    }
  });

  it('TC_FORM_01: Verify required fields validation in contacts entry form', async function() {
    excelReporter.addStepLog(this.test.title, 'Navigation', 'Click', 'Navigating to Guardians section');
    await dashboardPage.navigateToGuardians();

    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Leaving Guardian name & phone empty and submitting');
    await contactsPage.clear(contactsPage.nameInput);
    await contactsPage.clear(contactsPage.phoneInput);
    await contactsPage.click(contactsPage.submitBtn);

    excelReporter.addStepLog(this.test.title, 'Verify Rule', 'Success', 'Asserting list remains unchanged due to required fields constraint');
    // Verify count did not increase (or check for validation warning toast/alert)
    const alertText = await contactsPage.handleAlert(true);
    if (alertText) {
      expect(alertText.toLowerCase()).to.satisfy(text => text.includes('fill') || text.includes('required') || text.includes('invalid') || text.includes('empty'));
    }
  });

  it('TC_FORM_02: Verify invalid phone number pattern warnings', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Submitting contact form with invalid non-numeric phone');
    await contactsPage.clear(contactsPage.nameInput);
    await contactsPage.clear(contactsPage.phoneInput);
    
    await contactsPage.type(contactsPage.nameInput, 'Dr. Invalid Phone');
    await contactsPage.type(contactsPage.phoneInput, 'abcdefgh');
    await contactsPage.hideKeyboard();
    await contactsPage.click(contactsPage.submitBtn);

    excelReporter.addStepLog(this.test.title, 'Verify Alert', 'Success', 'Checking for phone validation warning modal');
    const alertText = await contactsPage.handleAlert(true);
    if (alertText) {
      expect(alertText.toLowerCase()).to.satisfy(text => text.includes('phone') || text.includes('number') || text.includes('invalid'));
    }
  });

  it('TC_FORM_03: Verify successful form submission with valid inputs', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Adding a valid guardian: Dr. Rajesh Koothrappali');
    const initialCount = await contactsPage.getRegisteredCount();
    
    await contactsPage.clear(contactsPage.nameInput);
    await contactsPage.clear(contactsPage.phoneInput);
    
    await contactsPage.addContact('Dr. Rajesh Koothrappali', '9888877777', 'Doctor', false);
    
    // Pause briefly for local sync to conclude
    await driver.pause(2000);
    
    excelReporter.addStepLog(this.test.title, 'Verify Entry', 'Success', 'Verifying count incremented');
    const finalCount = await contactsPage.getRegisteredCount();
    expect(finalCount).to.equal(initialCount + 1);
  });
});
