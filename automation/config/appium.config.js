require('dotenv').config();
const path = require('path');

const config = {
  // Appium Server Configuration
  server: {
    host: process.env.APPIUM_HOST || '127.0.0.1',
    port: parseInt(process.env.APPIUM_PORT, 10) || 4723,
    path: process.env.APPIUM_PATH || '/'
  },

  // Base Capabilities
  capabilities: {
    platformName: 'Android',
    'appium:automationName': 'UiAutomator2',
    'appium:deviceName': process.env.DEVICE_NAME || 'Android Emulator',
    'appium:platformVersion': process.env.PLATFORM_VERSION || '12.0',
    'appium:newCommandTimeout': 240,
    'appium:noReset': true,
    'appium:fullReset': false,
    'appium:autoGrantPermissions': true,
    'appium:ignoreHiddenApiPolicyError': true
  },

  // Execution Modes (APK or Installed Package)
  executionMode: process.env.EXECUTION_MODE || 'APK', // 'APK' or 'INSTALLED'

  apkConfig: {
    app: process.env.APK_PATH ? path.resolve(process.env.APK_PATH) : path.resolve(__dirname, '../../app/build/outputs/apk/debug/app-debug.apk')
  },

  installedConfig: {
    appPackage: process.env.APP_PACKAGE || 'com.example',
    appActivity: process.env.APP_ACTIVITY || 'com.example.frontend.MainActivity'
  },

  timeouts: {
    implicit: 10000,
    explicit: 20000,
    waitInterval: 1000
  }
};

module.exports = config;
