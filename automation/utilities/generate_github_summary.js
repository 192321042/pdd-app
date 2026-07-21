const fs = require('fs');
const path = require('path');

const reportPath = path.resolve(__dirname, '../reports/index.json');
if (!fs.existsSync(reportPath)) {
  console.error('Mochawesome report file not found at:', reportPath);
  process.exit(1);
}

const report = JSON.parse(fs.readFileSync(reportPath, 'utf8'));

let totalTests = 0;
let passedTests = 0;
let failedTests = 0;
let totalDurationMs = 0;
let testRowsMarkdown = '';

function processSuite(suite) {
  if (suite.tests && suite.tests.length > 0) {
    suite.tests.forEach(test => {
      totalTests++;
      const isPassed = test.state === 'passed' || test.pass === true;
      if (isPassed) {
        passedTests++;
      } else {
        failedTests++;
      }
      if (test.duration) {
        totalDurationMs += test.duration;
      }
      
      const statusIcon = isPassed ? '🟢 PASSED' : '🔴 FAILED';
      const duration = test.duration ? `${(test.duration / 1000).toFixed(2)}s` : 'N/A';
      testRowsMarkdown += `| ${statusIcon} | **${test.title}** | _${suite.title}_ | ${duration} |\n`;
    });
  }
  if (suite.suites && suite.suites.length > 0) {
    suite.suites.forEach(subSuite => processSuite(subSuite));
  }
}

if (report.results && report.results.length > 0) {
  report.results.forEach(rootResult => {
    if (rootResult.suites && rootResult.suites.length > 0) {
      rootResult.suites.forEach(suite => processSuite(suite));
    }
  });
}

const passRate = totalTests > 0 ? ((passedTests / totalTests) * 100).toFixed(2) : '0.00';

let markdown = `## 📊 OmniGuard AI — E2E Test Execution Summary\n\n`;
markdown += `| Metric | Value |\n`;
markdown += `| --- | --- |\n`;
markdown += `| **Total Test Cases** | ${totalTests} |\n`;
markdown += `| **✅ Passed** | ${passedTests} |\n`;
markdown += `| **❌ Failed** | ${failedTests} |\n`;
markdown += `| **Pass Rate** | ${passRate}% |\n`;
markdown += `| **Duration** | ${(totalDurationMs / 1000).toFixed(2)}s |\n\n`;

markdown += `### 📝 Test Case Breakdown\n\n`;
markdown += `| Status | Test ID & Title | Suite | Duration |\n`;
markdown += `| --- | --- | --- | --- |\n`;
markdown += testRowsMarkdown;

const githubSummaryPath = process.env.GITHUB_STEP_SUMMARY;
if (githubSummaryPath) {
  fs.appendFileSync(githubSummaryPath, markdown);
  console.log('Successfully appended test results summary to GitHub Actions Step Summary!');
} else {
  console.log('GITHUB_STEP_SUMMARY environment variable not set. Outputting to console instead:\n');
  console.log(markdown);
}
