const BasePage = require('./base.page');
const logger = require('../utilities/logger');

class DashboardPage extends BasePage {
  constructor(driver) {
    super(driver);

    // Bottom Navigation Bar Selectors
    this.navHome = '//*[contains(@content-desc, "Home") or @text="Home"]';
    this.navAiStatus = '//*[contains(@content-desc, "AI Status") or @text="AI Status"]';
    this.navGuardians = '//*[contains(@content-desc, "Guardians") or @text="Guardians"]';
    this.navAiChat = '//*[contains(@content-desc, "AI Chat") or @text="AI Chat"]';
    this.navSettings = '//*[contains(@content-desc, "Settings") or @text="Settings"]';

    // UI Dashboard Components
    this.appSlogan = '//*[@text="OmniGuard AI"]';
    this.safetyScore = '//android.widget.TextView[contains(@text, "%")]';
    this.safetyVerdict = '//*[@text="Optimal" or @text="Distressed"]';
    this.locationDisplay = '//android.widget.TextView[contains(@text, "Hyderabad") or contains(@text, "N,") or contains(@text, "E")]';

    // SOS triggers
    this.extendedFabSos = '//*[contains(@content-desc, "SOS Alert Signal") or @text="Panic SOS" or contains(@content-desc, "Panic SOS")]';
    this.centralSosBtn = '//*[contains(@content-desc, "Distress Trigger") or @text="SOS" or contains(@content-desc, "SOS")]';
    this.sosCountdown = '//android.widget.TextView[parent::android.view.View and string-length(@text) <= 2]'; // represents countdown digits like '10', '9'
    this.cancelSosBtn = '//*[contains(@text, "Deactivate") or contains(@text, "False Alert") or contains(@text, "TAP TO CANCEL") or contains(@content-desc, "Cancel Alert")]';
    
    // Performance assessment selectors
    this.sensorVitalsTitle = '//*[@text="Vitals Assessment Metrics"]';
    this.statScreamSimBtn = '//*[@text="Simulate Scream"]';
    this.statFallSimBtn = '//*[@text="Simulate Fall"]';
  }

  async navigateToHome() {
    logger.info('Navigating to Home section');
    await this.click(this.navHome);
  }

  async navigateToAiStatus() {
    logger.info('Navigating to AI Status section');
    await this.click(this.navAiStatus);
  }

  async navigateToGuardians() {
    logger.info('Navigating to Guardians/Rescue section');
    await this.click(this.navGuardians);
  }

  async navigateToAiChat() {
    logger.info('Navigating to AI Chatbot section');
    await this.click(this.navAiChat);
  }

  async navigateToSettings() {
    logger.info('Navigating to Settings/Profile section');
    await this.click(this.navSettings);
  }

  async triggerPanicSosViaFab() {
    logger.info('Clicking Extended Floating Action Button Panic SOS');
    await this.click(this.extendedFabSos);
  }

  async triggerCentralSosButton() {
    logger.info('Clicking Big Central touch-panic SOS button');
    await this.click(this.centralSosBtn);
  }

  async cancelSosAlert() {
    logger.info('Deactivating Panic SOS Alarm');
    await this.click(this.cancelSosBtn);
  }

  async getSafetyScoreText() {
    return await this.getText(this.safetyScore);
  }

  async isMonitoringActive() {
    try {
      const homeTab = await this.getElement(this.navHome);
      const active = await homeTab.isDisplayed();
      if (active) {
        // Ensure we are back on the Home dashboard screen
        logger.info('isMonitoringActive: App is logged in. Navigating to Home tab...');
        await this.click(this.navHome);
      }
      return active;
    } catch (err) {
      logger.info(`isMonitoringActive: Not active because: ${err.message}`);
      return false;
    }
  }

  async simulateScreamDistress() {
    logger.info('Simulating sound scream acoustic trigger');
    await this.click(this.statScreamSimBtn);
  }

  async simulateFallDistress() {
    logger.info('Simulating kinetic fall impact gesture trigger');
    await this.click(this.statFallSimBtn);
  }
}

module.exports = DashboardPage;
