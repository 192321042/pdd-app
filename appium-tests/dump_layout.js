const DriverFactory = require('./drivers/driver.factory');
const fs = require('fs');
const path = require('path');

async function run() {
  console.log('Connecting to Appium...');
  let driver;
  try {
    // Set environment variables programmatically just in case
    process.env.ANDROID_HOME = 'C:\\Users\\Rajiv\\AppData\\Local\\Android\\Sdk';
    
    driver = await DriverFactory.createDriver();
    console.log('Driver session created. Waiting 5s...');
    await driver.pause(5000);
    
    const skipBtn = await driver.$('//*[@text="Skip"]');
    if (await skipBtn.isDisplayed().catch(() => false)) {
      console.log('Clicking Skip button to navigate to Login screen...');
      await skipBtn.click();
      await driver.pause(5000);
    }
    
    console.log('Getting page source...');
    const source = await driver.getPageSource();
    
    const outputPath = path.resolve(__dirname, 'layout_source.xml');
    fs.writeFileSync(outputPath, source);
    console.log(`Page source dumped successfully to: ${outputPath}`);
  } catch (err) {
    console.error('Error during dump:', err);
  } finally {
    if (driver) {
      await driver.deleteSession();
    }
  }
}

run();
