const DriverFactory = require('./drivers/driver.factory');
const fs = require('fs');
const path = require('path');
const Gestures = require('./utilities/gestures');

async function run() {
  console.log('Connecting to Appium...');
  let driver;
  try {
    process.env.ANDROID_HOME = 'C:\\Users\\Rajiv\\AppData\\Local\\Android\\Sdk';
    
    driver = await DriverFactory.createDriver();
    console.log('Driver session created. Bypassing onboarding...');
    
    await driver.setTimeouts(0);
    
    const skipBtnSelector = '//*[@text="Skip"]';
    const signInTabSelector = '//*[@text="Sign In"]';
    const loggedInSelector = '//*[@text="Home" or @text="Settings" or @text="OmniGuard AI"]';
    
    await driver.waitUntil(
      async () => {
        const skipBtn = await driver.$(skipBtnSelector);
        const signInTab = await driver.$(signInTabSelector);
        const loggedIn = await driver.$(loggedInSelector);
        return (await skipBtn.isDisplayed().catch(() => false)) || 
               (await signInTab.isDisplayed().catch(() => false)) || 
               (await loggedIn.isDisplayed().catch(() => false));
      },
      {
        timeout: 45000,
        timeoutMsg: 'Splash screen wait timed out'
      }
    );
    
    await driver.setTimeouts(15000);
    
    const skipBtn = await driver.$(skipBtnSelector);
    const loggedIn = await driver.$(loggedInSelector);
    
    if (await skipBtn.isDisplayed().catch(() => false)) {
      console.log('Onboarding screen detected. Clicking "Skip"...');
      await skipBtn.click();
      await driver.pause(2000);
    } else if (await loggedIn.isDisplayed().catch(() => false)) {
      console.log('App is already logged in. Logging out to start fresh...');
      const settingsTab = await driver.$('//*[@text="Settings" or contains(@content-desc, "Settings")]');
      await settingsTab.click();
      await driver.pause(2000);
      
      const signOutBtn = await driver.$('//*[contains(@text, "Log Out") or contains(@text, "Sign Out") or contains(@text, "Logout")]');
      await signOutBtn.click();
      await driver.pause(2000);
    }
    
    console.log('Waiting for login screen...');
    const signInTab = await driver.$(signInTabSelector);
    await signInTab.waitForDisplayed({ timeout: 15000 });
    
    console.log('Performing Login...');
    await signInTab.click();
    
    const emailInput = await driver.$('//*[@resource-id="email_input"]');
    await emailInput.setValue('amulyaammu316@gmail.com');
    
    const passwordInput = await driver.$('//*[@resource-id="password_input"]');
    await passwordInput.setValue('correctpassword123');
    
    const submitBtn = await driver.$('//*[contains(@text, "Sign In & Unlock") or contains(@text, "Register Emergency")]');
    await submitBtn.click();
    
    console.log('Waiting for dashboard Slogan to display...');
    const slogan = await driver.$('//*[@text="OmniGuard AI"]');
    await slogan.waitForDisplayed({ timeout: 15000 });
    
    console.log('Navigating to Chatbot screen...');
    const chatbotSelector = '//*[contains(@text, "AI Safety Chatbot") or contains(@text, "Chatbot Advisor")]';
    const chatbotCard = await Gestures.scrollUntilVisible(driver, chatbotSelector);
    await chatbotCard.click();
    await driver.pause(5000);
    
    console.log('Getting chatbot page source...');
    const source = await driver.getPageSource();
    
    const outputPath = path.resolve(__dirname, 'chatbot_layout_source.xml');
    fs.writeFileSync(outputPath, source);
    console.log(`Chatbot page source dumped successfully to: ${outputPath}`);
  } catch (err) {
    console.error('Error during layout dump:', err);
  } finally {
    if (driver) {
      await driver.deleteSession();
    }
  }
}

run();
