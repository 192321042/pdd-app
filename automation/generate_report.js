const ExcelJS = require('exceljs');
const path = require('path');

async function generateReport() {
  const workbook = new ExcelJS.Workbook();

  // ===== Sheet 1: Executive Summary =====
  const summarySheet = workbook.addWorksheet('Executive Summary');
  summarySheet.columns = [
    { header: '', key: 'label', width: 32 },
    { header: '', key: 'value', width: 44 }
  ];

  const titleRow = summarySheet.addRow(['OmniGuard AI - Mobile E2E Test Report', '']);
  titleRow.font = { bold: true, size: 16, color: { argb: 'FFFFFFFF' } };
  titleRow.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0288D1' } };
  titleRow.height = 32;
  summarySheet.mergeCells('A1:B1');
  summarySheet.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' };

  summarySheet.addRow(['']);

  const metaData = [
    ['Execution Date', new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })],
    ['Device', 'Android Emulator (DIAA7HRWHI9P49GE)'],
    ['OS Version', 'Android 13'],
    ['App Package', 'com.aistudio.emergencydetector.bypcrw'],
    ['Framework', 'Appium 3.5 + WebdriverIO 8.x + Mocha 10.x'],
    ['Test Environment', 'Local Emulator - Debug APK'],
    ['Report Generated', new Date().toLocaleString('en-IN')],
  ];

  metaData.forEach(([label, value]) => {
    const row = summarySheet.addRow([label, value]);
    row.getCell(1).font = { bold: true, color: { argb: 'FF374151' } };
    row.getCell(1).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF0F9FF' } };
    row.getCell(2).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFFFFFF' } };
    row.height = 22;
  });

  summarySheet.addRow(['']);

  const scoreHeader = summarySheet.addRow(['TEST RESULTS SCORECARD', '']);
  scoreHeader.font = { bold: true, size: 13, color: { argb: 'FFFFFFFF' } };
  scoreHeader.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1E3A5F' } };
  summarySheet.mergeCells('A' + scoreHeader.number + ':B' + scoreHeader.number);
  scoreHeader.getCell(1).alignment = { horizontal: 'center' };
  scoreHeader.height = 26;

  const scores = [
    ['Total Test Cases Defined', '10'],
    ['Total Executed (Confirmed)', '5'],
    ['Passed', '5'],
    ['Failed', '0'],
    ['Running / Pending Results', '5'],
    ['Pass Rate (Executed)', '100%'],
    ['Overall Completion', '50% (5/10 fully verified)'],
    ['E2E Suite Score', '3/3  (100%)'],
    ['Authentication Suite Score', '2/4  (50% - remaining in progress)'],
    ['Form Validation Suite Score', '0/3  (Queued - running now)'],
  ];

  const highlightLabels = ['Passed', 'Pass Rate (Executed)', 'E2E Suite Score'];
  scores.forEach(([label, value]) => {
    const row = summarySheet.addRow([label, value]);
    row.getCell(1).font = { bold: true };
    row.getCell(1).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF8FAFC' } };
    row.height = 22;
    if (highlightLabels.includes(label)) {
      row.getCell(2).font = { bold: true, color: { argb: 'FF16A34A' } };
    } else if (label === 'Failed') {
      row.getCell(2).font = { bold: true, color: { argb: 'FFDC2626' } };
    }
  });

  // ===== Sheet 2: Detailed Test Cases =====
  const detailSheet = workbook.addWorksheet('Test Cases Detail');
  detailSheet.columns = [
    { header: 'Test ID', key: 'id', width: 16 },
    { header: 'Suite', key: 'suite', width: 32 },
    { header: 'Test Case Description', key: 'desc', width: 50 },
    { header: 'Status', key: 'status', width: 14 },
    { header: 'Duration (ms)', key: 'duration', width: 16 },
    { header: 'Score', key: 'score', width: 12 },
    { header: 'Notes', key: 'notes', width: 52 },
  ];

  const headerRow = detailSheet.getRow(1);
  headerRow.height = 26;
  headerRow.eachCell(cell => {
    cell.font = { bold: true, color: { argb: 'FFFFFFFF' }, size: 11 };
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0288D1' } };
    cell.alignment = { horizontal: 'center', vertical: 'middle', wrapText: true };
    cell.border = {
      top: { style: 'thin' }, bottom: { style: 'thin' },
      left: { style: 'thin' }, right: { style: 'thin' }
    };
  });

  const testCases = [
    {
      id: 'TC_E2E_01',
      suite: 'End-to-End Functional Distress Suite',
      desc: 'Verify full bottom navigation transitions: Home -> AI Status -> AI Chat -> Settings -> Home',
      status: 'PASSED', duration: 16428, score: '10/10',
      notes: 'All 5 nav tabs clicked successfully. Home tab confirmed active at end.'
    },
    {
      id: 'TC_E2E_02',
      suite: 'End-to-End Functional Distress Suite',
      desc: 'Verify Manual Panic SOS Alarm activation and dismissal via floating action button',
      status: 'PASSED', duration: 41739, score: '10/10',
      notes: 'FAB triggered. Countdown (9) detected. Alarm dismissed via False Alert button.'
    },
    {
      id: 'TC_E2E_03',
      suite: 'End-to-End Functional Distress Suite',
      desc: 'Verify AI simulated distress scream triggers automatic SOS alert overlay',
      status: 'PASSED', duration: 5079, score: '10/10',
      notes: 'Simulate Scream clicked on AI Status screen. SOS overlay appeared. Dismissed successfully.'
    },
    {
      id: 'TC_AUTH_01',
      suite: 'Authentication Testing Suite',
      desc: 'Verify empty credential submission shows validation error message',
      status: 'PASSED', duration: 8751, score: '10/10',
      notes: 'Empty fields submitted. Error shown: "Please fill out credentials." Assertion passed.'
    },
    {
      id: 'TC_AUTH_02',
      suite: 'Authentication Testing Suite',
      desc: 'Verify invalid credentials (wrong email/password) displays authentication error',
      status: 'PASSED', duration: 26628, score: '10/10',
      notes: 'Invalid login attempted. Backend returned credentials error. Assertion passed.'
    },
    {
      id: 'TC_AUTH_03',
      suite: 'Authentication Testing Suite',
      desc: 'Verify valid login with correct credentials navigates user to dashboard',
      status: 'RUNNING', duration: null, score: 'TBD',
      notes: 'Currently executing. Login submitted. Waiting for OmniGuard AI dashboard to load.'
    },
    {
      id: 'TC_AUTH_04',
      suite: 'Authentication Testing Suite',
      desc: 'Verify logout functionality navigates back to the Login screen',
      status: 'RUNNING', duration: null, score: 'TBD',
      notes: 'Queued. Navigates to Settings, scrolls to find Log Out Session button, clicks and verifies Sign In tab.'
    },
    {
      id: 'TC_FORM_01',
      suite: 'Form Rules Validation Suite',
      desc: 'Verify required fields validation - empty name and phone shows constraint warning',
      status: 'RUNNING', duration: null, score: 'TBD',
      notes: 'Queued. Navigate to Guardians, clear inputs, submit, check for validation alert.'
    },
    {
      id: 'TC_FORM_02',
      suite: 'Form Rules Validation Suite',
      desc: 'Verify invalid phone number pattern (alphabetic input) shows phone validation warning',
      status: 'RUNNING', duration: null, score: 'TBD',
      notes: 'Queued. Type non-numeric phone (abcdefgh), submit, assert error modal contains phone/invalid.'
    },
    {
      id: 'TC_FORM_03',
      suite: 'Form Rules Validation Suite',
      desc: 'Verify successful form submission with valid guardian data increments registered count',
      status: 'RUNNING', duration: null, score: 'TBD',
      notes: 'Queued. Add Dr. Rajesh Koothrappali with valid phone. Assert contact count incremented by 1.'
    },
  ];

  const statusColors = {
    'PASSED': { bg: 'FFD1FAE5', fg: 'FF065F46' },
    'FAILED': { bg: 'FFFEE2E2', fg: 'FF991B1B' },
    'RUNNING': { bg: 'FFFEF3C7', fg: 'FF92400E' },
  };

  testCases.forEach((tc, idx) => {
    const row = detailSheet.addRow([
      tc.id, tc.suite, tc.desc, tc.status,
      tc.duration !== null ? tc.duration : 'In Progress',
      tc.score, tc.notes
    ]);

    const colors = statusColors[tc.status] || { bg: 'FFF3F4F6', fg: 'FF4B5563' };

    row.eachCell((cell) => {
      cell.border = {
        top: { style: 'thin', color: { argb: 'FFE5E7EB' } },
        bottom: { style: 'thin', color: { argb: 'FFE5E7EB' } },
        left: { style: 'thin', color: { argb: 'FFE5E7EB' } },
        right: { style: 'thin', color: { argb: 'FFE5E7EB' } }
      };
      cell.alignment = { vertical: 'top', wrapText: true };
      if (idx % 2 === 0) {
        cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FAFAFAFA' } };
      }
    });

    const statusCell = row.getCell(4);
    statusCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: colors.bg } };
    statusCell.font = { bold: true, color: { argb: colors.fg } };
    statusCell.alignment = { horizontal: 'center', vertical: 'middle' };

    row.getCell(2).font = { italic: true, color: { argb: 'FF0369A1' } };
    row.getCell(6).alignment = { horizontal: 'center', vertical: 'middle' };
    row.getCell(6).font = { bold: true };
    row.height = 50;
  });

  // ===== Sheet 3: Suite Scores =====
  const scoreSheet = workbook.addWorksheet('Suite Scores');
  scoreSheet.columns = [
    { header: 'Suite Name', key: 'suite', width: 38 },
    { header: 'Total TCs', key: 'total', width: 12 },
    { header: 'Passed', key: 'passed', width: 12 },
    { header: 'Failed', key: 'failed', width: 12 },
    { header: 'Running/TBD', key: 'running', width: 14 },
    { header: 'Pass % (Executed)', key: 'pct', width: 20 },
    { header: 'Grade', key: 'grade', width: 10 },
  ];

  const shHeader = scoreSheet.getRow(1);
  shHeader.height = 26;
  shHeader.eachCell(cell => {
    cell.font = { bold: true, color: { argb: 'FFFFFFFF' } };
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1E3A5F' } };
    cell.alignment = { horizontal: 'center', vertical: 'middle' };
    cell.border = { top: { style: 'thin' }, bottom: { style: 'thin' }, left: { style: 'thin' }, right: { style: 'thin' } };
  });

  const suiteData = [
    ['End-to-End Functional Distress Suite', 3, 3, 0, 0, '100%', 'A+'],
    ['Authentication Testing Suite', 4, 2, 0, 2, '100% (of 2 executed)', 'A'],
    ['Form Rules Validation Suite', 3, 0, 0, 3, 'Pending', 'TBD'],
    ['OVERALL TOTAL', 10, 5, 0, 5, '100% (5/5 executed)', 'A'],
  ];

  const gradeColors = { 'A+': 'FF16A34A', 'A': 'FF2563EB', 'TBD': 'FF92400E' };

  suiteData.forEach((rowData, idx) => {
    const r = scoreSheet.addRow(rowData);
    r.height = 26;
    r.eachCell((cell, col) => {
      cell.border = {
        top: { style: 'thin', color: { argb: 'FFE5E7EB' } },
        bottom: { style: 'thin', color: { argb: 'FFE5E7EB' } },
        left: { style: 'thin', color: { argb: 'FFE5E7EB' } },
        right: { style: 'thin', color: { argb: 'FFE5E7EB' } }
      };
      cell.alignment = { horizontal: 'center', vertical: 'middle' };
    });
    r.getCell(1).alignment = { horizontal: 'left', vertical: 'middle' };
    if (idx === suiteData.length - 1) {
      r.font = { bold: true };
      r.eachCell(cell => { cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFEFF6FF' } }; });
    }
    const grade = rowData[6];
    const gradeCell = r.getCell(7);
    gradeCell.font = { bold: true, color: { argb: gradeColors[grade] || 'FF374151' } };
    if (rowData[2] === 3) {
      r.getCell(3).font = { bold: true, color: { argb: 'FF16A34A' } };
    }
  });

  const filePath = 'D:\\remix omni-app\\automation\\excel\\Mobile_E2E_Report_Comprehensive.xlsx';
  await workbook.xlsx.writeFile(filePath);
  console.log('Report saved to: ' + filePath);
}

generateReport().catch(console.error);
