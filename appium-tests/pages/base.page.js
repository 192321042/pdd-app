const fs = require('fs');
const path = require('path');
const logger = require('../utilities/logger');

class BasePage {
  constructor(driver) {
    this.driver = driver;
  }

  /**
   * Wrapper for finding a single element
   */
  async getElement(selector) {
    return this.driver.$(selector);
  }

  /**
   * Explicit Wait: Wait until element is displayed
   */
  async waitForElementDisplayed(selector, timeout = 15000) {
    logger.info(`Waiting for element: [${selector}] to be displayed...`);
    const el = await this.getElement(selector);
    try {
      await el.waitForDisplayed({ timeout });
      return el;
    } catch (err) {
      // Check if there is a system ANR dialog or permission/SMS dialog blocking
      try {
        await this.driver.setTimeouts(0);

        const allowBtn = await this.driver.$('//*[@text="Allow" or @text="ALLOW" or @resource-id="android:id/button1"]');
        if (await allowBtn.isDisplayed().catch(() => false)) {
          logger.warn('System Allow/Permission dialog detected. Clicking "Allow" and retrying...');
          await allowBtn.click();
          await this.driver.pause(2000);

          // Restore implicit wait to wait for element
          const config = require('../config/appium.config');
          await this.driver.setTimeouts(config.timeouts.implicit);
          await el.waitForDisplayed({ timeout: 5000 });
          return el;
        }

        const waitBtn = await this.driver.$('//*[(@class="android.widget.Button" or @class="android.widget.TextView") and (translate(@text, "WAIT", "wait")="wait" or @resource-id="android:id/button2")]');
        if (await waitBtn.isDisplayed().catch(() => false)) {
          logger.warn('System ANR dialog detected. Clicking "Wait" and retrying...');
          await waitBtn.click();
          await this.driver.pause(2000);
          
          // Restore implicit wait to wait for element
          const config = require('../config/appium.config');
          await this.driver.setTimeouts(config.timeouts.implicit);
          await el.waitForDisplayed({ timeout: 5000 });
          return el;
        }
      } catch (anrErr) {
        logger.error(`Error handling system/ANR dialog: ${anrErr.message}`);
      } finally {
        try {
          const config = require('../config/appium.config');
          await this.driver.setTimeouts(config.timeouts.implicit);
        } catch (restoreErr) {
          // Ignore
        }
      }
      throw err;
    }
  }

  /**
   * Wrapper for clicking an element after verifying visibility
   */
  async click(selector) {
    const el = await this.waitForElementDisplayed(selector);
    logger.info(`Clicking element: [${selector}]`);
    await el.click();
  }

  /**
   * Wrapper for typing value into element
   */
  async type(selector, value) {
    const el = await this.waitForElementDisplayed(selector);
    logger.info(`Typing "${value}" into element: [${selector}]`);
    try {
      await el.clearValue();
    } catch (err) {
      logger.warn(`Could not clear element value: ${err.message}`);
    }
    await el.setValue(value);
  }

  /**
   * Clear text value from input field
   */
  async clear(selector) {
    const el = await this.waitForElementDisplayed(selector);
    logger.info(`Clearing element: [${selector}]`);
    await el.clearValue();
  }

  /**
   * Retrieve text contents of element
   */
  async getText(selector) {
    const el = await this.waitForElementDisplayed(selector);
    const text = await el.getText();
    logger.info(`Element [${selector}] text: "${text}"`);
    return text;
  }

  /**
   * Hides the software keyboard safely if displayed.
   */
  async hideKeyboard() {
    try {
      if (await this.driver.isKeyboardShown()) {
        logger.info('Hiding active keyboard');
        await this.driver.hideKeyboard();
      }
    } catch (err) {
      logger.warn(`Could not hide keyboard: ${err.message}`);
    }
  }

  /**
   * Checks if an alert is displayed and accepts/dismisses it.
   */
  async handleAlert(accept = true) {
    try {
      await this.driver.waitUntil(async () => await this.driver.isAlertOpen(), {
        timeout: 5000,
        timeoutMsg: 'No system alert displayed'
      });
      const alertText = await this.driver.getAlertText();
      logger.info(`Alert found: "${alertText}"`);
      if (accept) {
        await this.driver.acceptAlert();
        logger.info('Alert accepted');
      } else {
        await this.driver.dismissAlert();
        logger.info('Alert dismissed');
      }
      return alertText;
    } catch (err) {
      logger.info('No alert detected within wait window.');
      return null;
    }
  }

  /**
   * Captures screen snapshot on test failure
   */
  async captureScreenshot(testName) {
    const dir = path.resolve(__dirname, '../reports/failures');
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
    const sanitizedTestName = testName.replace(/[^a-z0-9]/gi, '_').toLowerCase();
    const filePath = path.join(dir, `${sanitizedTestName}_failure.png`);
    
    try {
      logger.info(`Capturing failure screenshot for "${testName}" at: ${filePath}`);
      await this.driver.saveScreenshot(filePath);
      return filePath;
    } catch (err) {
      logger.error(`Failed to capture screenshot: ${err.message}`);
      return null;
    }
  }

  /**
   * Dumps active android logcat logs to a file
   */
  async captureDeviceLogs(testName) {
    const dir = path.resolve(__dirname, '../reports/failures');
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
    const sanitizedTestName = testName.replace(/[^a-z0-9]/gi, '_').toLowerCase();
    const filePath = path.join(dir, `${sanitizedTestName}_logcat.txt`);

    try {
      logger.info(`Dumping device logcat logs for "${testName}" at: ${filePath}`);
      const logs = await this.driver.getLogs('logcat');
      const logString = logs.map(log => `[${log.timestamp}] [${log.level}]: ${log.message}`).join('\n');
      fs.writeFileSync(filePath, logString);
      return filePath;
    } catch (err) {
      logger.error(`Failed to capture logcat logs: ${err.message}`);
      return null;
    }
  }
}

module.exports = BasePage;
