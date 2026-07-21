const { remote } = require('webdriverio');
const { execSync } = require('child_process');
const config = require('../config/appium.config');
const logger = require('../utilities/logger');

class DriverFactory {
  /**
   * Automatically detect connected adb devices.
   * If devices are present, pick the first active one.
   */
  static detectDevice() {
    try {
      logger.info('Scanning for connected Android devices/emulators via ADB...');
      const stdout = execSync('adb devices').toString();
      const lines = stdout.split('\n').map(line => line.trim()).filter(line => line.length > 0);
      
      const devices = [];
      // Line 0 is usually "List of devices attached"
      for (let i = 1; i < lines.length; i++) {
        const parts = lines[i].split('\t');
        if (parts.length >= 2 && parts[1] === 'device') {
          const serial = parts[0];
          // Only select emulators to avoid using physical devices
          if (serial.startsWith('emulator-') || serial.startsWith('127.0.0.1') || serial.includes('localhost')) {
            devices.push(serial);
          } else {
            logger.info(`Ignoring physical/non-emulator device: ${serial}`);
          }
        }
      }

      if (devices.length > 0) {
        const chosen = devices[0];
        logger.info(`Detected device: ${chosen}. Retrieving Android version...`);
        let version = '12.0';
        try {
          version = execSync(`adb -s ${chosen} shell getprop ro.build.version.release`).toString().trim();
          logger.info(`Device ${chosen} runs Android ${version}`);
        } catch (verErr) {
          logger.warn(`Could not fetch OS version for device ${chosen}: ${verErr.message}`);
        }
        return { udid: chosen, version };
      }
      logger.warn('No active ADB devices detected. Defaulting to config capabilities.');
      return null;
    } catch (err) {
      logger.warn(`Failed to execute ADB command: ${err.message}. Defaulting config capabilities.`);
      return null;
    }
  }

  /**
   * Instantiates a new WebdriverIO Appium session.
   */
  static async createDriver() {
    logger.info('Initializing Appium session...');
    
    // Create cloned base capabilities
    const caps = { ...config.capabilities };

    // Dynamically detect connected device/emulator
    const device = this.detectDevice();
    if (device) {
      caps['appium:udid'] = device.udid;
      caps['appium:platformVersion'] = device.version;
      caps['appium:deviceName'] = device.udid;
    } else {
      throw new Error('No active Android emulator (emulator-*, 127.0.0.1, or localhost) detected. Running on physical devices is strictly disabled.');
    }

    // Configure Execution Mode (APK vs INSTALLED)
    if (config.executionMode === 'APK') {
      logger.info(`Configured for APK execution. Target path: ${config.apkConfig.app}`);
      caps['appium:app'] = config.apkConfig.app;
    } else {
      logger.info(`Configured for Installed App execution. Target: ${config.installedConfig.appPackage}`);
      caps['appium:appPackage'] = config.installedConfig.appPackage;
      caps['appium:appActivity'] = config.installedConfig.appActivity;
    }

    const options = {
      hostname: config.server.host,
      port: config.server.port,
      path: config.server.path,
      capabilities: caps,
      logLevel: 'error'
    };

    try {
      const driver = await remote(options);
      logger.info('Successfully created WebdriverIO Appium driver session.');
      return driver;
    } catch (error) {
      logger.error(`Failed to start Appium session: ${error.message}`);
      throw error;
    }
  }
}

module.exports = DriverFactory;
