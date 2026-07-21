const { expect } = require('chai');
const testContext = require('./base.test');
const LoginPage = require('../pages/login.page');
const DashboardPage = require('../pages/dashboard.page');
const excelReporter = require('../utilities/excel.reporter');
const Gestures = require('../utilities/gestures');

describe('Dashboard Core Widgets & Profile Management Suite', function() {
  let driver;
  let loginPage;
  let dashboardPage;

  before(async function() {
    driver = testContext.getDriver();
    loginPage = new LoginPage(driver);
    dashboardPage = new DashboardPage(driver);

    // Make sure we are logged in before running dashboard validation checks
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

  beforeEach(async function() {
    try {
      const isDashboard = await dashboardPage.isMonitoringActive();
      if (isDashboard) {
        await dashboardPage.navigateToHome();
      }
    } catch (err) {
      // Ignore
    }
  });

  it('TC_DASH_01: Verify App slogan "OmniGuard AI" displays', async function() {
    excelReporter.addStepLog(this.test.title, 'Widget Check', 'Display', 'Locating application main slogan');
    const isSloganDisplayed = await driver.$(dashboardPage.appSlogan).isDisplayed();
    expect(isSloganDisplayed).to.be.true;
  });

  it('TC_DASH_02: Verify ambient safety score percentage card displays', async function() {
    excelReporter.addStepLog(this.test.title, 'Widget Check', 'Display', 'Reading current safety score metric');
    const scoreVal = await dashboardPage.getSafetyScoreText();
    expect(scoreVal).to.include('%');
  });

  it('TC_DASH_03: Verify live location text display displays', async function() {
    excelReporter.addStepLog(this.test.title, 'Widget Check', 'Display', 'Confirming GPS location string displays');
    const locationVal = await dashboardPage.getText(dashboardPage.locationDisplay);
    expect(locationVal).to.satisfy(text => text.includes('Hyderabad') || text.includes('N,') || text.includes('E'));
  });

  it('TC_DASH_04: Verify AI Chatbot launcher card navigates to chatbot page', async function() {
    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Clicking Safety Chatbot advisor card');
    const chatbotSelector = '//*[contains(@text, "AI Safety Chatbot") or contains(@text, "Chatbot Advisor")]';
    const chatbotCard = await Gestures.scrollUntilVisible(driver, chatbotSelector);
    await chatbotCard.click();
    
    excelReporter.addStepLog(this.test.title, 'Verify Navigation', 'Success', 'Asserting chatbot language switcher is visible');
    const switcher = await driver.$('//*[@text="English"]');
    expect(await switcher.isDisplayed()).to.be.true;
    
    // Return to dashboard
    await dashboardPage.navigateToHome();
  });

  it('TC_DASH_05: Verify "Add Rescue" shortcut link redirects to Guardians page', async function() {
    excelReporter.addStepLog(this.test.title, 'Shortcut Click', 'Navigate', 'Clicking Add Rescue text link');
    const addRescueLink = await Gestures.scrollUntilVisible(driver, '//*[@text="Add Rescue"]');
    await addRescueLink.click();
    
    excelReporter.addStepLog(this.test.title, 'Verify Page', 'Success', 'Confirming Guardian form is active');
    const nameInput = await driver.$('//*[@resource-id="guardian_name_input"]');
    expect(await nameInput.isDisplayed()).to.be.true;
    
    // Return to dashboard
    await dashboardPage.navigateToHome();
  });

  it('TC_DASH_06: Verify bottom navigation transitions between all tabs', async function() {
    excelReporter.addStepLog(this.test.title, 'Navigation Carousel', 'Click', 'Stepping through bottom tab routes');
    
    await dashboardPage.navigateToAiStatus();
    const monitorTab = await driver.$('//*[@text="Monitor"]');
    await monitorTab.waitForDisplayed({ timeout: 10000 });
    expect(await monitorTab.isDisplayed()).to.be.true;

    await dashboardPage.navigateToGuardians();
    const registerContactText = await driver.$('//*[@text="Register Trusted Contact"]');
    await registerContactText.waitForDisplayed({ timeout: 10000 });
    expect(await registerContactText.isDisplayed()).to.be.true;

    await dashboardPage.navigateToAiChat();
    const chatEl = await driver.$('//*[contains(@text, "AI Safety Assistant") or contains(@text, "Gemini")]');
    await chatEl.waitForDisplayed({ timeout: 10000 });
    expect(await chatEl.isDisplayed()).to.be.true;

    await dashboardPage.navigateToSettings();
    const settingsEl = await driver.$('//*[contains(@text, "Security & Credentials") or contains(@text, "Log Out Session")]');
    await settingsEl.waitForDisplayed({ timeout: 10000 });
    expect(await settingsEl.isDisplayed()).to.be.true;

    await dashboardPage.navigateToHome();
    const sloganEl = await driver.$(dashboardPage.appSlogan);
    await sloganEl.waitForDisplayed({ timeout: 10000 });
    expect(await sloganEl.isDisplayed()).to.be.true;
  });

  it('TC_DASH_07: Verify floating action button triggers manual distress SOS overlay', async function() {
    excelReporter.addStepLog(this.test.title, 'Trigger SOS', 'Action', 'Clicking floating action button panic beacon');
    await dashboardPage.triggerPanicSosViaFab();
    
    excelReporter.addStepLog(this.test.title, 'Verify SOS Display', 'Success', 'Checking active distress cancel button is visible');
    const isSosScreenActive = await driver.$(dashboardPage.cancelSosBtn).isDisplayed();
    expect(isSosScreenActive).to.be.true;
    
    // Deactivate alert
    await dashboardPage.cancelSosAlert();
  });

  it('TC_DASH_08: Verify large central touch-panic button triggers SOS alarm', async function() {
    excelReporter.addStepLog(this.test.title, 'Trigger SOS', 'Action', 'Pressing big central panic button');
    await dashboardPage.triggerCentralSosButton();
    
    excelReporter.addStepLog(this.test.title, 'Verify Alarm Countdown', 'Success', 'Asserting countdown timers decrementing');
    const timerValue = await dashboardPage.getText(dashboardPage.sosCountdown);
    expect(parseInt(timerValue, 10)).to.be.within(0, 10);
  });

  it('TC_DASH_09: Verify countdown cancel/dismissal deactivates panic beacon', async function() {
    excelReporter.addStepLog(this.test.title, 'Cancel SOS', 'Action', 'Dismissing active alarm event');
    await dashboardPage.cancelSosAlert();
    
    excelReporter.addStepLog(this.test.title, 'Verify Idle', 'Success', 'Confirming cancel button is gone and dashboard restored');
    const isCancelBtnPresent = await driver.$(dashboardPage.cancelSosBtn).isDisplayed().catch(() => false);
    expect(isCancelBtnPresent).to.be.false;
  });

  it('TC_DASH_10: Verify profile information editing and saving', async function() {
    excelReporter.addStepLog(this.test.title, 'Menu Click', 'Navigate', 'Navigating to Profile settings');
    await dashboardPage.navigateToSettings();
    
    excelReporter.addStepLog(this.test.title, 'Profile Form', 'Click', 'Clicking edit button');
    const editBtn = await driver.$('//*[contains(@text, "Edit Contact Profile") or contains(@text, "Edit Contact")]');
    await editBtn.click();
    
    excelReporter.addStepLog(this.test.title, 'Profile Form', 'Input', 'Editing name and mobile number fields');
    const editInputs = await driver.$$('//android.widget.EditText');
    await editInputs[0].setValue('Amulya Edited');
    await editInputs[2].setValue('+91 9999922222');
    await loginPage.hideKeyboard();
    
    excelReporter.addStepLog(this.test.title, 'Profile Form', 'Action', 'Saving changes');
    const saveBtn = await driver.$('//*[@text="Save"]');
    await saveBtn.click();
    await driver.pause(1000);
    
    excelReporter.addStepLog(this.test.title, 'Verify Update', 'Success', 'Asserting new updated name displays');
    const updatedName = await driver.$('//*[@text="Amulya Edited"]').isDisplayed();
    expect(updatedName).to.be.true;
  });

  it('TC_DASH_11: Verify health summary edit modal cancels correctly', async function() {
    excelReporter.addStepLog(this.test.title, 'Health Form', 'Click', 'Clicking edit health summary pencil button');
    const healthPencilSelector = '//*[contains(@content-desc, "Edit Health Summary") or @content-desc="Edit Health Summary"]';
    const editHealthPencil = await Gestures.scrollUntilVisible(driver, healthPencilSelector);
    await editHealthPencil.click();
    
    excelReporter.addStepLog(this.test.title, 'Health Form', 'Action', 'Clicking Cancel inside editing layout');
    // Click Cancel on the health profile card edit layout (which is OutlinedButton inside profile container)
    const cancelBtn = await driver.$('//android.widget.Button/*[@text="Cancel"]|//android.widget.Button[@text="Cancel"]|//*[@text="Cancel"]');
    await cancelBtn.click();
    await driver.pause(1000);
    
    excelReporter.addStepLog(this.test.title, 'Verify Cancel', 'Success', 'Asserting edit pencil returns to view screen');
    const pencilDisplayed = await editHealthPencil.isDisplayed();
    expect(pencilDisplayed).to.be.true;
    
    // Navigate home for next suite
    await dashboardPage.navigateToHome();
  });
});
