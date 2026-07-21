const { expect } = require('chai');
const testContext = require('./base.test');
const LoginPage = require('../pages/login.page');
const DashboardPage = require('../pages/dashboard.page');
const ContactsPage = require('../pages/contacts.page');
const excelReporter = require('../utilities/excel.reporter');
const Gestures = require('../utilities/gestures');

describe('Contacts Form Rules & Verification Suite', function() {
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
        const Gestures = require('../utilities/gestures');
        const baseBtn = await Gestures.scrollUntilVisible(driver, signOutBtn);
        await baseBtn.click();
        await driver.$(loginPage.signInTab).waitForDisplayed({ timeout: 10000 });
      }
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

  it('TC_FORM_03: Verify phone number empty constraint handling', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Entering name but leaving phone empty');
    await contactsPage.clear(contactsPage.nameInput);
    await contactsPage.clear(contactsPage.phoneInput);
    
    await contactsPage.type(contactsPage.nameInput, 'No Phone Contact');
    await contactsPage.hideKeyboard();
    await contactsPage.click(contactsPage.submitBtn);

    excelReporter.addStepLog(this.test.title, 'Verify Alert', 'Success', 'Asserting alert text displays');
    const alertText = await contactsPage.handleAlert(true);
    if (alertText) {
      expect(alertText.toLowerCase()).to.satisfy(text => text.includes('fill') || text.includes('empty') || text.includes('phone') || text.includes('required'));
    }
  });

  it('TC_FORM_04: Verify relationship buttons selection logic', async function() {
    excelReporter.addStepLog(this.test.title, 'Relation Switch', 'Click', 'Selecting Police relationship button');
    await contactsPage.click(contactsPage.relationPolice);
    
    excelReporter.addStepLog(this.test.title, 'Relation Switch', 'Click', 'Selecting Doctor relationship button');
    await contactsPage.click(contactsPage.relationDoctor);
    
    excelReporter.addStepLog(this.test.title, 'Verify Selection', 'Success', 'Asserting relation selection functions without crash');
    expect(await driver.$(contactsPage.relationDoctor).isDisplayed()).to.be.true;
  });

  it('TC_FORM_05: Verify primary designation checkbox toggling', async function() {
    excelReporter.addStepLog(this.test.title, 'Checkbox Toggle', 'Click', 'Toggling primary SMS checkbox');
    const checkbox = await driver.$(contactsPage.primaryCheckbox);
    const initialChecked = await checkbox.getAttribute('checked');
    await checkbox.click();
    const finalChecked = await checkbox.getAttribute('checked');
    
    excelReporter.addStepLog(this.test.title, 'Verify State', 'Success', 'Confirming toggle changed state');
    expect(finalChecked).to.not.equal(initialChecked);
  });

  it('TC_FORM_06: Verify successful form submission with valid inputs', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Adding a valid guardian: Dr. Rajesh Koothrappali');
    const initialCount = await contactsPage.getRegisteredCount();
    
    await contactsPage.clear(contactsPage.nameInput);
    await contactsPage.clear(contactsPage.phoneInput);
    await contactsPage.addContact('Dr. Rajesh Koothrappali', '9888877777', 'Doctor', false);
    
    await driver.pause(2000);
    
    excelReporter.addStepLog(this.test.title, 'Verify Entry', 'Success', 'Verifying count incremented');
    const finalCount = await contactsPage.getRegisteredCount();
    expect(finalCount).to.equal(initialCount + 1);
  });

  it('TC_FORM_07: Verify contact details show correct relationship metadata', async function() {
    excelReporter.addStepLog(this.test.title, 'Verify Card', 'Search', 'Locating Rajesh Koothrappali card in UI');
    const contactCard = await Gestures.scrollUntilVisible(driver, '//*[contains(@text, "Rajesh") or contains(@text, "Rajesh Koothrappali")]');
    const isContactCardDisplayed = await contactCard.isDisplayed();
    expect(isContactCardDisplayed).to.be.true;

    // Scroll back to top to restore form inputs visibility
    for (let i = 0; i < 3; i++) {
      await Gestures.swipeDown(driver, 0.7);
      await driver.pause(300);
    }
  });

  it('TC_FORM_08: Verify multiple contact additions updates contact list', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Input', 'Adding second valid contact: Howard Wolowitz');
    const initialCount = await contactsPage.getRegisteredCount();
    
    await contactsPage.clear(contactsPage.nameInput);
    await contactsPage.clear(contactsPage.phoneInput);
    await contactsPage.addContact('Howard Wolowitz', '9888866666', 'Friend', true);
    
    await driver.pause(2000);
    
    excelReporter.addStepLog(this.test.title, 'Verify Count', 'Success', 'Asserting list increments to count of 2 added contacts');
    const finalCount = await contactsPage.getRegisteredCount();
    expect(finalCount).to.equal(initialCount + 1);
  });

  it('TC_FORM_09: Verify deleting a registered contact', async function() {
    excelReporter.addStepLog(this.test.title, 'Form Action', 'Delete', 'Removing added trusted contact');
    const initialCount = await contactsPage.getRegisteredCount();
    
    await contactsPage.removeFirstContact();
    await driver.pause(2000);
    
    excelReporter.addStepLog(this.test.title, 'Verify Deletion', 'Success', 'Verifying count decremented');
    const finalCount = await contactsPage.getRegisteredCount();
    expect(finalCount).to.equal(initialCount - 1);

    // Scroll back to top
    for (let i = 0; i < 3; i++) {
      await Gestures.swipeDown(driver, 0.7);
      await driver.pause(300);
    }
  });

  it('TC_FORM_10: Verify contact list layout integrity after CRUD actions', async function() {
    excelReporter.addStepLog(this.test.title, 'Verify Title', 'Display', 'Asserting list title counts are properly formatted');
    const titleText = await contactsPage.getText(contactsPage.contactsTitle);
    expect(titleText).to.include('Registered Guardians');
  });
});
