# Enterprise Appium E2E Automation Framework for Android (Node.js)

This directory contains a production-ready, highly modular **End-to-End (E2E) mobile automation framework** built using Appium 2.x, WebdriverIO (v8), and Node.js. It follows the **Page Object Model (POM)** design pattern and uses Winston for logging and ExcelJS for detailed audit reporting.

---

## Project Structure

```
automation/
├── config/
│   └── appium.config.js       # Capability and server environment configs
├── drivers/
│   └── driver.factory.js      # ADB device auto-detect & remote session factory
├── pages/
│   ├── base.page.js           # Explicit waits, alert/keyboard wrapper actions, failure screenshot & logcat dumps
│   ├── login.page.js          # Authentication screen selectors & interactions
│   ├── dashboard.page.js      # Main screen bottom navigation & simulated distress triggers
│   └── contacts.page.js       # Form inputs & relationship validators
├── utilities/
│   ├── gestures.js            # Swipe, tap, scroll-to, pinch/zoom helper using W3C Actions API
│   ├── logger.js              # Winston log transporters to file & colorized console
│   ├── excel.reporter.js      # 4-Sheet Excel report exporter
│   └── ai.analyzer.js         # Layout page-source parser & form auto-discovery rules
├── tests/
│   ├── base.test.js           # Mocha driver start/teardown and hooks
│   ├── login.test.js          # Auth test suites (valid, invalid, empty validations)
│   ├── form.test.js           # Required inputs & validation rules tests
│   └── e2e.test.js            # End-to-end user navigation & simulated alarm triggers
├── logs/                      # Appium framework logger files
├── excel/                     # Mobile_E2E_Report.xlsx outputs
├── screenshots/               # Success/failure image assets
├── reports/                   # Mochawesome HTML/JSON reports
├── package.json               # Node script task runners & library declarations
└── README.md                  # Setup and guide documentation
```

---

## Technology Stack

- **Runtime Environment:** Node.js (v18+)
- **Mobile Automation Driver:** Appium 2.x + UiAutomator2
- **Test Runner:** Mocha
- **Assertion Engine:** Chai
- **Reporting:** Mochawesome (HTML) & ExcelJS (XLSX)
- **Logger:** Winston

---

## Prerequisites

1. **Node.js (v18+):** Install Node.js from the official site.
2. **Java JDK 17:** Install JDK and set `JAVA_HOME`.
3. **Android SDK:** Install Android Command Line Tools or Android Studio, set `ANDROID_HOME`, and configure `platform-tools` (adb) on your path.
4. **Appium 2.x Server:**
   ```bash
   npm install -g appium@latest
   appium driver install uiautomator2
   ```

---

## Local Setup & Installation

1. Navigate to the automation directory:
   ```bash
   cd automation
   ```
2. Install npm dependencies:
   ```bash
   npm install
   ```
3. Configure your environment variables inside a `.env` file at the `automation/` root directory:
   ```env
   EXECUTION_MODE=APK
   APK_PATH=../app/build/outputs/apk/debug/app-debug.apk
   # Or for installed mode:
   # EXECUTION_MODE=INSTALLED
   # APP_PACKAGE=com.example
   # APP_ACTIVITY=com.example.frontend.MainActivity
   ```

---

## Executing Tests

1. Start your Appium server locally:
   ```bash
   appium
   ```
2. Ensure your Android Emulator or physical device is online and detected by typing `adb devices`.
3. Execute the tests:
   ```bash
   # Run all tests
   npm test

   # Run specific test suites
   npm run test:login
   npm run test:form
   npm run test:e2e
   ```

---

## Reports & Artifacts

After execution, the following reporting files are automatically created:
- **HTML/JSON Report:** Located at `automation/reports/index.html` (Mochawesome).
- **Excel Audit Log:** Generated at `automation/excel/Mobile_E2E_Report.xlsx` containing:
  - **Summary:** Total run counts, pass percentage, device/OS version, date, and duration.
  - **Test Cases:** Individual list of status, start/end timestamps, and execution speeds.
  - **Failed Tests:** Exact assertion details, failing activity, and absolute screenshot paths.
  - **Execution Logs:** Chronological step-by-step actions executed during tests.

---

## CI/CD Pipeline

The framework is configured to run headless on GitHub Actions using a macOS runner. Check the pipeline configurations at [appium-e2e.yml](file:///d:/remix%20omni-app/.github/workflows/appium-e2e.yml).
