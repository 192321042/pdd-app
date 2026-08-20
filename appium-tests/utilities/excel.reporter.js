const ExcelJS = require('exceljs');
const path = require('path');
const fs = require('fs');
const logger = require('./logger');

class ExcelReporter {
  constructor() {
    this.workbook = new ExcelJS.Workbook();
    this.summaryData = null;
    this.testCases = [];
    this.failedTests = [];
    this.executionLogs = [];
  }

  /**
   * Set execution summary data
   */
  setSummary(data) {
    // Expected fields: executionDate, deviceName, osVersion, totalTests, passed, failed, skipped, duration
    const total = data.totalTests || 0;
    const passed = data.passed || 0;
    const passPercent = total > 0 ? ((passed / total) * 100).toFixed(2) + '%' : '0.00%';
    
    this.summaryData = {
      ...data,
      passPercent
    };
  }

  /**
   * Add test case detail row
   */
  addTestCase(testId, module, scenario, device, status, startTime, endTime, duration) {
    this.testCases.push({
      testId,
      module,
      scenario,
      device,
      status,
      startTime,
      endTime,
      duration
    });
  }

  /**
   * Add failure detail row
   */
  addFailedTest(testName, failureReason, screenshotPath, device, osVersion, activityName) {
    this.failedTests.push({
      testName,
      failureReason,
      screenshotPath,
      device,
      osVersion,
      activityName
    });
  }

  /**
   * Add a execution step log
   */
  addStepLog(testName, step, result, remarks = '') {
    this.executionLogs.push({
      timestamp: new Date().toISOString(),
      testName,
      step,
      result,
      remarks
    });
  }

  /**
   * Setup styles for sheets headers
   */
  styleHeader(worksheet, numColumns) {
    const row = worksheet.getRow(1);
    row.height = 25;
    for (let i = 1; i <= numColumns; i++) {
      const cell = row.getCell(i);
      cell.font = { name: 'Segoe UI', size: 11, bold: true, color: { argb: 'FFFFFF' } };
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: '1F4E79' } // Dark blue theme color
      };
      cell.alignment = { vertical: 'middle', horizontal: 'center' };
      cell.border = {
        top: { style: 'thin' },
        left: { style: 'thin' },
        bottom: { style: 'medium' },
        right: { style: 'thin' }
      };
    }
    worksheet.views = [{ state: 'frozen', xSplit: 0, ySplit: 1 }];
  }

  /**
   * Set borders for all data rows
   */
  styleDataRows(worksheet) {
    worksheet.eachRow((row, rowNumber) => {
      if (rowNumber > 1) {
        row.eachCell(cell => {
          cell.font = { name: 'Segoe UI', size: 10 };
          cell.border = {
            top: { style: 'thin', color: { argb: 'E0E0E0' } },
            left: { style: 'thin', color: { argb: 'E0E0E0' } },
            bottom: { style: 'thin', color: { argb: 'E0E0E0' } },
            right: { style: 'thin', color: { argb: 'E0E0E0' } }
          };
        });
      }
    });
  }

  /**
   * Save workbook to file
   */
  async generateReport() {
    const outputDir = path.resolve(__dirname, '../excel');
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true });
    }
    const outputPath = path.join(outputDir, 'Mobile_E2E_Report.xlsx');
    logger.info(`Generating Excel E2E Report at: ${outputPath}`);

    // --- SHEET 1: SUMMARY ---
    const summarySheet = this.workbook.addWorksheet('Summary');
    summarySheet.columns = [
      { header: 'Execution Date', key: 'executionDate', width: 22 },
      { header: 'Device Name', key: 'deviceName', width: 22 },
      { header: 'Android Version', key: 'osVersion', width: 18 },
      { header: 'Total Tests', key: 'totalTests', width: 15 },
      { header: 'Passed', key: 'passed', width: 12 },
      { header: 'Failed', key: 'failed', width: 12 },
      { header: 'Skipped', key: 'skipped', width: 12 },
      { header: 'Pass Percentage', key: 'passPercent', width: 18 },
      { header: 'Execution Duration', key: 'duration', width: 20 }
    ];
    this.styleHeader(summarySheet, 9);
    if (this.summaryData) {
      summarySheet.addRow(this.summaryData);
    }
    this.styleDataRows(summarySheet);

    // --- SHEET 2: TEST CASES ---
    const tcSheet = this.workbook.addWorksheet('Test Cases');
    tcSheet.columns = [
      { header: 'Test ID', key: 'testId', width: 15 },
      { header: 'Module', key: 'module', width: 20 },
      { header: 'Scenario', key: 'scenario', width: 35 },
      { header: 'Device', key: 'device', width: 22 },
      { header: 'Status', key: 'status', width: 15 },
      { header: 'Start Time', key: 'startTime', width: 22 },
      { header: 'End Time', key: 'endTime', width: 22 },
      { header: 'Duration (ms)', key: 'duration', width: 15 }
    ];
    this.styleHeader(tcSheet, 8);
    this.testCases.forEach(tc => {
      const row = tcSheet.addRow(tc);
      const cell = row.getCell(5); // Status cell
      if (tc.status === 'Passed') {
        cell.font = { color: { argb: '006100' }, bold: true };
        cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'C6EFCE' } };
      } else if (tc.status === 'Failed') {
        cell.font = { color: { argb: '9C0006' }, bold: true };
        cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFC7CE' } };
      }
    });
    this.styleDataRows(tcSheet);

    // --- SHEET 3: FAILED TESTS ---
    const failSheet = this.workbook.addWorksheet('Failed Tests');
    failSheet.columns = [
      { header: 'Test Name', key: 'testName', width: 30 },
      { header: 'Failure Reason', key: 'failureReason', width: 45 },
      { header: 'Screenshot Path', key: 'screenshotPath', width: 40 },
      { header: 'Device', key: 'device', width: 20 },
      { header: 'Android Version', key: 'osVersion', width: 18 },
      { header: 'Activity Name', key: 'activityName', width: 30 }
    ];
    this.styleHeader(failSheet, 6);
    this.failedTests.forEach(ft => {
      failSheet.addRow(ft);
    });
    this.styleDataRows(failSheet);

    // --- SHEET 4: EXECUTION LOGS ---
    const logsSheet = this.workbook.addWorksheet('Execution Logs');
    logsSheet.columns = [
      { header: 'Timestamp', key: 'timestamp', width: 25 },
      { header: 'Test Name', key: 'testName', width: 25 },
      { header: 'Step', key: 'step', width: 30 },
      { header: 'Result', key: 'result', width: 15 },
      { header: 'Remarks', key: 'remarks', width: 35 }
    ];
    this.styleHeader(logsSheet, 5);
    this.executionLogs.forEach(log => {
      logsSheet.addRow(log);
    });
    this.styleDataRows(logsSheet);

    await this.workbook.xlsx.writeFile(outputPath);
    logger.info('Excel E2E Report saved successfully.');
    return outputPath;
  }
}

// Singleton instances mapping for runtime accessibility
const excelReporter = new ExcelReporter();

module.exports = excelReporter;
