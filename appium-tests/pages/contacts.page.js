const BasePage = require('./base.page');
const logger = require('../utilities/logger');

class ContactsPage extends BasePage {
  constructor(driver) {
    super(driver);

    this.nameInput = '//*[@resource-id="guardian_name_input"]';
    this.phoneInput = '//*[@resource-id="guardian_phone_input"]';
    
    // Relationship buttons
    this.relationFamily = '//*[@text="Family"]';
    this.relationPolice = '//*[@text="Police"]';
    this.relationFriend = '//*[@text="Friend"]';
    this.relationDoctor = '//*[@text="Doctor"]';
    
    this.primaryCheckbox = '//android.widget.CheckBox';
    this.submitBtn = '//*[@text="Register Trusted Contact"]';

    // List and Delete buttons
    this.contactsTitle = '//android.widget.TextView[contains(@text, "Registered Guardians")]';
    this.deleteBtn = '//*[contains(@content-desc, "Remove contact") or contains(@content-desc, "Delete")]';
  }

  async addContact(name, phone, relationship = 'Family', isPrimary = true) {
    logger.info(`Adding emergency contact: ${name} (${phone})`);
    
    await this.type(this.nameInput, name);
    await this.type(this.phoneInput, phone);
    await this.hideKeyboard();

    // Select relationship
    switch (relationship) {
      case 'Family':
        await this.click(this.relationFamily);
        break;
      case 'Police':
        await this.click(this.relationPolice);
        break;
      case 'Friend':
        await this.click(this.relationFriend);
        break;
      case 'Doctor':
        await this.click(this.relationDoctor);
        break;
    }

    // Toggle primary checkbox (default is checked in app, if we want to change we click it)
    const checkbox = await this.waitForElementDisplayed(this.primaryCheckbox);
    const checked = await checkbox.getAttribute('checked');
    if ((isPrimary && checked === 'false') || (!isPrimary && checked === 'true')) {
      logger.info('Toggling primary checkpoint');
      await checkbox.click();
    }

    await this.click(this.submitBtn);
    logger.info('Successfully submitted contact form.');
  }

  async removeFirstContact() {
    logger.info('Removing first emergency contact from list');
    const Gestures = require('../utilities/gestures');
    await Gestures.scrollUntilVisible(this.driver, this.deleteBtn);
    await this.click(this.deleteBtn);
  }

  async getRegisteredCount() {
    const titleText = await this.getText(this.contactsTitle);
    logger.info(`Registered count title text: "${titleText}"`);
    // Expected output format: "Registered Guardians & Authorities (3)"
    const match = titleText.match(/\((\d+)\)/);
    return match ? parseInt(match[1], 10) : 0;
  }
}

module.exports = ContactsPage;
