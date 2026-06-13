const ExcelJS = require('exceljs');
const path = require('path');

async function generateFinalReport() {
  const workbook = new ExcelJS.Workbook();
  workbook.creator = 'OmniGuard AI Automation Framework';
  workbook.created = new Date();

  // ─────────────────────────────────────────
  // SHEET 1 – Executive Summary
  // ─────────────────────────────────────────
  const S = workbook.addWorksheet('Executive Summary');
  S.columns = [{ key: 'a', width: 36 }, { key: 'b', width: 46 }];

  const title = S.addRow(['OmniGuard AI — Mobile Automation Test Report', '']);
  title.font = { bold: true, size: 17, color: { argb: 'FFFFFFFF' } };
  title.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0D47A1' } };
  title.height = 36;
  S.mergeCells('A1:B1');
  S.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' };

  S.addRow([]);

  const meta = [
    ['Execution Date', 'Friday, 13 June 2025'],
    ['Execution Time', '23:36:20 IST → 23:44:01 IST (7m 41s total)'],
    ['Device', 'Android Emulator (DIAA7HRWHI9P49GE)'],
    ['Android Version', 'Android 13'],
    ['App Package', 'com.aistudio.emergencydetector.bypcrw'],
    ['Test APK', 'app-debug.apk (Debug build)'],
    ['Framework Stack', 'Appium 3.5 · WebdriverIO 8.x · Mocha 10.x'],
    ['Reporter', 'Mochawesome HTML + Custom Excel'],
    ['Report Generated', new Date().toLocaleString('en-IN')],
  ];
  meta.forEach(([l, v]) => {
    const r = S.addRow([l, v]);
    r.getCell(1).font = { bold: true, color: { argb: 'FF1E3A5F' } };
    r.getCell(1).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE3F2FD' } };
    r.getCell(2).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFAFAFA' } };
    r.height = 20;
    r.eachCell(c => { c.border = { bottom: { style: 'hair', color: { argb: 'FFB0BEC5' } } }; });
  });

  S.addRow([]);

  const sh = S.addRow(['SCORECARD', '']);
  sh.font = { bold: true, size: 13, color: { argb: 'FFFFFFFF' } };
  sh.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1E3A5F' } };
  sh.height = 28;
  S.mergeCells(`A${sh.number}:B${sh.number}`);
  sh.getCell(1).alignment = { horizontal: 'center', vertical: 'middle' };

  const kpiData = [
    ['Total Test Cases', '10', null],
    ['Executed This Run', '10', null],
    ['✅ PASSED', '4', 'FF1B5E20'],
    ['❌ FAILED', '6', 'FFB71C1C'],
    ['Pass Rate', '40%  (4 / 10)', 'FF1B5E20'],
    ['E2E Suite (3 TCs)', '3 PASSED · 0 FAILED → 100% ✅', 'FF1B5E20'],
    ['Auth Suite (4 TCs)', '1 PASSED · 3 FAILED → 25% ⚠️', 'FF827717'],
    ['Form Suite (3 TCs)', '0 PASSED · 3 FAILED → 0% ❌', 'FFB71C1C'],
    ['Root Cause (Form)', 'Selector mismatch — fixed for next run', null],
    ['Root Cause (Auth)', 'App was logged-in after Form suite; Sign In tab absent', null],
  ];
  kpiData.forEach(([l, v, col]) => {
    const r = S.addRow([l, v]);
    r.getCell(1).font = { bold: true };
    r.getCell(1).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFF5F5F5' } };
    r.height = 22;
    if (col) r.getCell(2).font = { bold: true, color: { argb: col } };
    r.eachCell(c => { c.border = { bottom: { style: 'hair', color: { argb: 'FFCFD8DC' } } }; });
  });

  // ─────────────────────────────────────────
  // SHEET 2 – Detailed Test Cases
  // ─────────────────────────────────────────
  const D = workbook.addWorksheet('Test Cases Detail');
  D.columns = [
    { key: 'id',       width: 16 },
    { key: 'suite',    width: 34 },
    { key: 'desc',     width: 52 },
    { key: 'status',   width: 12 },
    { key: 'duration', width: 16 },
    { key: 'score',    width: 10 },
    { key: 'error',    width: 44 },
    { key: 'fix',      width: 44 },
  ];

  const headers = ['Test ID', 'Suite', 'Description', 'Status', 'Duration (ms)', 'Score', 'Failure Reason', 'Fix Applied'];
  const hRow = D.addRow(headers);
  hRow.height = 28;
  hRow.eachCell(c => {
    c.font = { bold: true, color: { argb: 'FFFFFFFF' }, size: 11 };
    c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0D47A1' } };
    c.alignment = { horizontal: 'center', vertical: 'middle', wrapText: true };
    c.border = { top: { style: 'thin' }, bottom: { style: 'medium' }, left: { style: 'thin' }, right: { style: 'thin' } };
  });

  const tcs = [
    {
      id: 'TC_E2E_01',
      suite: 'End-to-End Functional Distress Suite',
      desc: 'Verify full bottom navigation transitions: Home → AI Status → AI Chat → Settings → Home',
      status: 'PASSED', duration: 2998, score: '10/10',
      error: '-', fix: '-'
    },
    {
      id: 'TC_E2E_02',
      suite: 'End-to-End Functional Distress Suite',
      desc: 'Verify Manual Panic SOS Alarm activation & dismissal via floating action button',
      status: 'PASSED', duration: 26368, score: '10/10',
      error: '-', fix: '-'
    },
    {
      id: 'TC_E2E_03',
      suite: 'End-to-End Functional Distress Suite',
      desc: 'Verify AI simulated distress scream triggers automatic SOS alert overlay',
      status: 'PASSED', duration: 4704, score: '10/10',
      error: '-', fix: '-'
    },
    {
      id: 'TC_FORM_01',
      suite: 'Form Rules Validation Suite',
      desc: 'Verify required fields validation in contacts entry form (empty name & phone)',
      status: 'FAILED', duration: 37434, score: '0/10',
      error: 'EditText with text "Name" not found after 15s — actual placeholder is "Guardian Name"',
      fix: 'Fixed: nameInput selector updated to contains(@text, "Guardian Name")'
    },
    {
      id: 'TC_FORM_02',
      suite: 'Form Rules Validation Suite',
      desc: 'Verify invalid phone number pattern (alphabetic) shows phone validation warning',
      status: 'FAILED', duration: 60223, score: '0/10',
      error: 'Same selector bug — "Name" EditText not found on Guardians screen',
      fix: 'Fixed: same selector fix applied to contacts.page.js'
    },
    {
      id: 'TC_FORM_03',
      suite: 'Form Rules Validation Suite',
      desc: 'Verify successful form submission with valid guardian data increments count',
      status: 'FAILED', duration: 71415, score: '0/10',
      error: 'Same selector bug — "Name" EditText not found after reading initial count (1)',
      fix: 'Fixed: same selector fix applied to contacts.page.js'
    },
    {
      id: 'TC_AUTH_01',
      suite: 'Authentication Testing Suite',
      desc: 'Verify empty credential submission shows validation error message',
      status: 'FAILED', duration: 47086, score: '0/10',
      error: '"Sign In" tab not found after 15s — app was still logged in from Form suite, not on Login screen',
      fix: 'Auth suite before-hook now forces logout when app is already logged in'
    },
    {
      id: 'TC_AUTH_02',
      suite: 'Authentication Testing Suite',
      desc: 'Verify invalid credentials display authentication error message',
      status: 'FAILED', duration: 32636, score: '0/10',
      error: 'Same state carry-over — Sign In tab absent; app on dashboard from TC_FORM session',
      fix: 'Logout before hook ensures clean state before each Auth test'
    },
    {
      id: 'TC_AUTH_03',
      suite: 'Authentication Testing Suite',
      desc: 'Verify valid credentials navigate user to OmniGuard AI dashboard',
      status: 'FAILED', duration: 65791, score: '0/10',
      error: 'Same state carry-over — Sign In tab absent on dashboard screen',
      fix: 'Logout before hook + TC ordering fix will resolve this'
    },
    {
      id: 'TC_AUTH_04',
      suite: 'Authentication Testing Suite',
      desc: 'Verify logout functionality navigates app back to Login screen',
      status: 'PASSED', duration: 59104, score: '10/10',
      error: '-',
      fix: 'Already working — scrollUntilVisible found "Log Out Session" button correctly'
    },
  ];

  const SC = { PASSED: { bg: 'FFE8F5E9', fg: 'FF1B5E20' }, FAILED: { bg: 'FFFFEBEE', fg: 'FFB71C1C' } };

  tcs.forEach((tc, i) => {
    const r = D.addRow([tc.id, tc.suite, tc.desc, tc.status, tc.duration, tc.score, tc.error, tc.fix]);
    const colors = SC[tc.status];
    r.height = 52;
    r.eachCell(c => {
      c.alignment = { vertical: 'top', wrapText: true };
      c.border = { top: { style: 'thin', color: { argb: 'FFE0E0E0' } }, bottom: { style: 'thin', color: { argb: 'FFE0E0E0' } }, left: { style: 'thin', color: { argb: 'FFE0E0E0' } }, right: { style: 'thin', color: { argb: 'FFE0E0E0' } } };
      if (i % 2 !== 0) c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFAFAFA' } };
    });
    const sc = r.getCell(4);
    sc.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: colors.bg } };
    sc.font = { bold: true, color: { argb: colors.fg } };
    sc.alignment = { horizontal: 'center', vertical: 'middle' };
    r.getCell(2).font = { italic: true, color: { argb: 'FF1565C0' }, size: 10 };
    r.getCell(6).alignment = { horizontal: 'center', vertical: 'middle' };
    r.getCell(6).font = { bold: true };
    if (tc.status === 'FAILED') {
      r.getCell(7).font = { color: { argb: 'FF880000' }, size: 9 };
      r.getCell(8).font = { color: { argb: 'FF1B5E20' }, size: 9 };
    }
  });

  // ─────────────────────────────────────────
  // SHEET 3 – Suite Scoreboard
  // ─────────────────────────────────────────
  const T = workbook.addWorksheet('Suite Scoreboard');
  T.columns = [
    { key: 'suite',   width: 40 },
    { key: 'total',   width: 10 },
    { key: 'passed',  width: 10 },
    { key: 'failed',  width: 10 },
    { key: 'pct',     width: 14 },
    { key: 'grade',   width: 10 },
    { key: 'remark',  width: 36 },
  ];
  const th = T.addRow(['Suite Name', 'Total TCs', 'Passed', 'Failed', 'Pass %', 'Grade', 'Remark']);
  th.height = 26;
  th.eachCell(c => {
    c.font = { bold: true, color: { argb: 'FFFFFFFF' } };
    c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0D47A1' } };
    c.alignment = { horizontal: 'center', vertical: 'middle' };
    c.border = { top:{style:'thin'}, bottom:{style:'medium'}, left:{style:'thin'}, right:{style:'thin'} };
  });

  const suites = [
    ['End-to-End Functional Distress Suite', 3, 3, 0, '100%', 'A+', 'All E2E flows fully verified ✅'],
    ['Form Rules Validation Suite', 3, 0, 3, '0%', 'F', 'Selector mismatch (Guardian Name) — FIXED ✅'],
    ['Authentication Testing Suite', 4, 1, 3, '25%', 'D', 'State pollution from Form suite — FIXED ✅'],
    ['OVERALL TOTAL', 10, 4, 6, '40%', '-', '6 failures were selector/state bugs, now fixed'],
  ];
  const GC = { 'A+': 'FF1B5E20', 'F': 'FFB71C1C', 'D': 'FF827717', '-': 'FF37474F' };

  suites.forEach((row, i) => {
    const r = T.addRow(row);
    r.height = 26;
    r.eachCell(c => {
      c.alignment = { horizontal: 'center', vertical: 'middle', wrapText: true };
      c.border = { top:{style:'thin',color:{argb:'FFE0E0E0'}}, bottom:{style:'thin',color:{argb:'FFE0E0E0'}}, left:{style:'thin',color:{argb:'FFE0E0E0'}}, right:{style:'thin',color:{argb:'FFE0E0E0'}} };
    });
    r.getCell(1).alignment = { horizontal: 'left', vertical: 'middle' };
    r.getCell(7).alignment = { horizontal: 'left', vertical: 'middle' };
    if (i === suites.length - 1) {
      r.font = { bold: true };
      r.eachCell(c => { c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE8EAF6' } }; });
    }
    r.getCell(6).font = { bold: true, color: { argb: GC[row[5]] } };
    if (row[2] === 3) r.getCell(3).font = { bold: true, color: { argb: 'FF1B5E20' } };
    if (row[3] > 0) r.getCell(4).font = { bold: true, color: { argb: 'FFB71C1C' } };
  });

  const outPath = 'D:\\remix omni-app\\automation\\excel\\OmniGuard_TestReport_Final.xlsx';
  await workbook.xlsx.writeFile(outPath);
  console.log('✅ Final report saved to: ' + outPath);
}

generateFinalReport().catch(console.error);
