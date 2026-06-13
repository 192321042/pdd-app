const BasePage = require('./base.page');
const logger = require('../utilities/logger');

class LoginPage extends BasePage {
  constructor(driver) {
    super(driver);
    
    // Selectors optimized for Jetpack Compose (using texts, index, and class hierarchy)
    this.signInTab = '//*[@text="Sign In"]';
    this.registerTab = '//*[@text="Register"]';
    
    this.nameInput = '//*[@resource-id="name_input"]';
    this.emailInput = '//*[@resource-id="email_input"]';
    this.phoneInput = '//*[@resource-id="phone_input"]';
    this.passwordInput = '//*[@resource-id="password_input"]';
    
    this.submitBtn = '//*[contains(@text, "Sign In & Unlock") or contains(@text, "Register Emergency")]';
    this.forgotPasswordLink = '//*[@text="Forgot Password?"]';
    
    this.errorBox = '//android.widget.TextView[contains(@text, "failed") or contains(@text, "fill") or contains(@text, "credentials") or contains(@text, "invalid") or contains(@text, "required")]';
    this.successBox = '//android.widget.TextView[contains(@text, "successful")]';
    
    // Forgot Password modal controls
    this.forgotEmailInput = '//android.widget.EditText[contains(@text, "Email") or android.widget.TextView[contains(@text, "Email")]]';
    this.sendLinkBtn = '//*[@text="Send Link"]';
    this.cancelForgotBtn = '//*[@text="Cancel"]';
  }

  async selectSignInTab() {
    logger.info('Selecting Sign In Tab');
    await this.click(this.signInTab);
  }

  async selectRegisterTab() {
    logger.info('Selecting Register Tab');
    await this.click(this.registerTab);
  }

  async fillName(name) {
    await this.type(this.nameInput, name);
  }

  async fillEmail(email) {
    await this.type(this.emailInput, email);
  }

  async fillPhone(phone) {
    await this.type(this.phoneInput, phone);
  }

  async fillPassword(password) {
    await this.type(this.passwordInput, password);
  }

  async submit() {
    await this.click(this.submitBtn);
  }

  async performLogin(email, password) {
    logger.info(`Performing login with email: ${email}`);
    await this.selectSignInTab();
    await this.fillEmail(email);
    await this.fillPassword(password);
    await this.hideKeyboard();
    await this.submit();
  }

  async performRegistration(name, email, phone, password) {
    logger.info(`Performing registration for: ${name} (${email})`);
    await this.selectRegisterTab();
    await this.fillName(name);
    await this.fillEmail(email);
    await this.fillPhone(phone);
    await this.fillPassword(password);
    await this.hideKeyboard();
    await this.submit();
  }

  async getErrorMessage() {
    try {
      const text = await this.getText(this.errorBox);
      return text;
    } catch (err) {
      logger.info('No error message displayed.');
      return null;
    }
  }

  async getSuccessMessage() {
    try {
      const text = await this.getText(this.successBox);
      return text;
    } catch (err) {
      logger.info('No success message displayed.');
      return null;
    }
  }

  async triggerForgotPassword(email) {
    logger.info(`Triggering forgot password for: ${email}`);
    await this.click(this.forgotPasswordLink);
    await this.type(this.forgotEmailInput, email);
    await this.click(this.sendLinkBtn);
  }
}

module.exports = LoginPage;
