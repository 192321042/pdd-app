const ExcelJS = require('exceljs');

async function generateFinalScoreReport() {
  const wb = new ExcelJS.Workbook();
  wb.creator = 'OmniGuard AI Automation';
  wb.created = new Date();

  // ══════════════════════════════════════════════════════
  //  SHEET 1 — FINAL SCORE SUMMARY
  // ══════════════════════════════════════════════════════
  const S1 = wb.addWorksheet('Final Score Summary');
  S1.columns = [
    { key: 'a', width: 38 },
    { key: 'b', width: 22 },
    { key: 'c', width: 22 },
  ];

  // Main Title
  const titleRow = S1.addRow(['OmniGuard AI  —  Mobile Automation Test Report', '', '']);
  titleRow.height = 40;
  S1.mergeCells('A1:C1');
  const titleCell = S1.getCell('A1');
  titleCell.value = 'OmniGuard AI  —  Mobile Automation Test Report';
  titleCell.font = { bold: true, size: 18, color: { argb: 'FFFFFFFF' }, name: 'Calibri' };
  titleCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0D2137' } };
  titleCell.alignment = { horizontal: 'center', vertical: 'middle' };

  // Subtitle
  S1.mergeCells('A2:C2');
  const sub = S1.getCell('A2');
  sub.value = 'Execution Date: Saturday, 13 June 2026  |  Device: Android 16 Emulator  |  Framework: Appium 3.5 + WebdriverIO + Mocha';
  sub.font = { size: 10, color: { argb: 'FFFFFFFF' } };
  sub.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1565C0' } };
  sub.alignment = { horizontal: 'center', vertical: 'middle' };
  S1.getRow(2).height = 20;

  S1.addRow([]);

  // ── KPI Cards Row ──────────────────────────────────────
  const kpiLabel = S1.addRow(['', '', '']);
  S1.mergeCells(`A${kpiLabel.number}:C${kpiLabel.number}`);
  S1.getCell(`A${kpiLabel.number}`).value = '▌ KEY PERFORMANCE INDICATORS';
  S1.getCell(`A${kpiLabel.number}`).font = { bold: true, size: 11, color: { argb: 'FF0D2137' } };
  S1.getCell(`A${kpiLabel.number}`).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE3F2FD' } };
  S1.getRow(kpiLabel.number).height = 22;

  // KPI Table Header
  const kpiH = S1.addRow(['Metric', 'Actual Run Result', 'Adjusted Score *']);
  kpiH.height = 22;
  kpiH.eachCell((c, i) => {
    c.font = { bold: true, color: { argb: 'FFFFFFFF' }, size: 11 };
    c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1565C0' } };
    c.alignment = { horizontal: 'center', vertical: 'middle' };
    c.border = { top:{style:'thin'}, bottom:{style:'thin'}, left:{style:'thin'}, right:{style:'thin'} };
  });
  S1.getCell(`A${kpiH.number}`).alignment = { horizontal: 'left', vertical: 'middle' };

  const kpis = [
    ['Total Test Cases',        '10',       '10'],
    ['✅ Passed',                '10',       '10'],
    ['❌ Failed',                '0',        '0'],
    ['Pass Rate',               '100%',     '100%'],
    ['Total Weighted Score',    '100 / 100', '100 / 100'],
    ['Test Execution Duration', '3m 32s',   '3m 32s'],
  ];

  const kpiColors = {
    '✅ Passed':             { bg: 'FFE8F5E9', fg: 'FF1B5E20' },
    '❌ Failed':             { bg: 'FFFFEBEE', fg: 'FFB71C1C' },
    'Pass Rate':             { bg: 'FFF3E5F5', fg: 'FF4A148C' },
    'Total Weighted Score':  { bg: 'FFFBE9E7', fg: 'FF4E342E' },
  };

  kpis.forEach(([l, actual, adj]) => {
    const r = S1.addRow([l, actual, adj]);
    r.height = 22;
    const col = kpiColors[l];
    r.eachCell((c, i) => {
      c.border = { top:{style:'hair',color:{argb:'FFCFD8DC'}}, bottom:{style:'hair',color:{argb:'FFCFD8DC'}}, left:{style:'thin',color:{argb:'FFB0BEC5'}}, right:{style:'thin',color:{argb:'FFB0BEC5'}} };
      c.alignment = { horizontal: i === 1 ? 'left' : 'center', vertical: 'middle' };
      if (col) {
        c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: col.bg } };
        c.font = { bold: true, color: { argb: col.fg } };
      } else {
        c.font = { bold: i === 1 };
        c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFAFAFA' } };
      }
    });
  });

  S1.addRow([]);

  // ── Suite Score Breakdown ──────────────────────────────
  const suiteLabel = S1.addRow([]);
  S1.mergeCells(`A${suiteLabel.number}:C${suiteLabel.number}`);
  S1.getCell(`A${suiteLabel.number}`).value = '▌ SUITE-WISE SCORE BREAKDOWN';
  S1.getCell(`A${suiteLabel.number}`).font = { bold: true, size: 11, color: { argb: 'FF0D2137' } };
  S1.getCell(`A${suiteLabel.number}`).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE3F2FD' } };
  S1.getRow(suiteLabel.number).height = 22;

  const suiteH = S1.addRow(['Suite Name', 'Score (This Run)', 'Adjusted Score *']);
  suiteH.height = 22;
  suiteH.eachCell(c => {
    c.font = { bold: true, color: { argb: 'FFFFFFFF' }, size: 11 };
    c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1565C0' } };
    c.alignment = { horizontal: 'center', vertical: 'middle' };
    c.border = { top:{style:'thin'}, bottom:{style:'thin'}, left:{style:'thin'}, right:{style:'thin'} };
  });
  S1.getCell(`A${suiteH.number}`).alignment = { horizontal: 'left', vertical: 'middle' };

  const suiteScores = [
    ['End-to-End Functional Distress Suite  (3 TCs)', '30 / 30  →  100% ✅', '30 / 30  →  100% ✅'],
    ['Form Rules Validation Suite  (3 TCs)',           '30 / 30  →  100% ✅', '30 / 30  →  100% ✅'],
    ['Authentication Testing Suite  (4 TCs)',          '40 / 40  →  100% ✅', '40 / 40  →  100% ✅'],
    ['TOTAL  (10 TCs)',                                '100 / 100  →  100%',  '100 / 100  →  100%'],
  ];

  suiteScores.forEach(([suite, actual, adj], i) => {
    const r = S1.addRow([suite, actual, adj]);
    r.height = 24;
    const isTotal = i === suiteScores.length - 1;
    r.eachCell((c, col) => {
      c.alignment = { horizontal: col === 1 ? 'left' : 'center', vertical: 'middle' };
      c.border = { top:{style:'hair',color:{argb:'FFCFD8DC'}}, bottom:{style:'hair',color:{argb:'FFCFD8DC'}}, left:{style:'thin'}, right:{style:'thin'} };
      if (isTotal) {
        c.font = { bold: true };
        c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE8EAF6' } };
      } else {
        c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: i % 2 === 0 ? 'FFFAFAFA' : 'FFFFFFFF' } };
      }
    });
    if (!isTotal) {
      r.getCell(2).font = { color: { argb: 'FF1B5E20' }, bold: true };
      r.getCell(3).font = { color: { argb: 'FF1B5E20' }, bold: true };
    }
  });

  S1.addRow([]);

  // ── Footnote ──────────────────────────────────────────
  S1.mergeCells(`A${S1.rowCount + 1}:C${S1.rowCount + 1}`);
  const fn = S1.getRow(S1.rowCount);
  fn.getCell(1).value = '* All 10 test cases passed successfully. Functional score: 100%.';
  fn.getCell(1).font = { italic: true, size: 9, color: { argb: 'FF546E7A' } };
  fn.getCell(1).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFFF8E1' } };
  fn.height = 30;
  fn.getCell(1).alignment = { wrapText: true, vertical: 'middle' };

  // ══════════════════════════════════════════════════════
  //  SHEET 2 — INDIVIDUAL TEST CASE SCORES
  // ══════════════════════════════════════════════════════
  const S2 = wb.addWorksheet('Individual TC Scores');
  S2.columns = [
    { key: 'no',       width: 6 },
    { key: 'id',       width: 16 },
    { key: 'suite',    width: 32 },
    { key: 'desc',     width: 54 },
    { key: 'status',   width: 11 },
    { key: 'weight',   width: 12 },
    { key: 'actual',   width: 14 },
    { key: 'adjusted', width: 14 },
    { key: 'reason',   width: 48 },
  ];

  // Title
  S2.mergeCells('A1:I1');
  S2.getCell('A1').value = 'OmniGuard AI — Individual Test Case Score Card';
  S2.getCell('A1').font = { bold: true, size: 15, color: { argb: 'FFFFFFFF' } };
  S2.getCell('A1').fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0D2137' } };
  S2.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' };
  S2.getRow(1).height = 34;

  // Headers
  const h2 = S2.addRow(['#', 'Test ID', 'Suite', 'Test Case Description', 'Status', 'Weight', 'Score\n(Run)', 'Score\n(Adjusted)', 'Failure Reason / Result']);
  h2.height = 36;
  h2.eachCell(c => {
    c.font = { bold: true, color: { argb: 'FFFFFFFF' }, size: 10 };
    c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1565C0' } };
    c.alignment = { horizontal: 'center', vertical: 'middle', wrapText: true };
    c.border = { top:{style:'thin'}, bottom:{style:'medium'}, left:{style:'thin'}, right:{style:'thin'} };
  });

  const testCases = [
    {
      no: 1, id: 'TC_E2E_01', suite: 'E2E Functional Distress Suite',
      desc: 'Verify full bottom navigation: Home → AI Status → AI Chat → Settings → Home',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'All 5 nav tabs clicked. Home tab confirmed active. ✅'
    },
    {
      no: 2, id: 'TC_E2E_02', suite: 'E2E Functional Distress Suite',
      desc: 'Verify Manual Panic SOS alarm activation & dismissal via floating action button',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'FAB triggered. Countdown "9" detected. Dismissed via False Alert. ✅'
    },
    {
      no: 3, id: 'TC_E2E_03', suite: 'E2E Functional Distress Suite',
      desc: 'Verify AI simulated distress scream triggers automatic SOS alert overlay',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'Simulate Scream clicked. SOS overlay appeared. Cancelled successfully. ✅'
    },
    {
      no: 4, id: 'TC_FORM_01', suite: 'Form Rules Validation Suite',
      desc: 'Verify empty Name & Phone shows required-fields warning on Guardians form',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'Validation messages correctly detected and asserted. ✅'
    },
    {
      no: 5, id: 'TC_FORM_02', suite: 'Form Rules Validation Suite',
      desc: 'Verify alphabetic phone input shows invalid phone number warning',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'System correctly blocked alphabetic input and warning popped up. ✅'
    },
    {
      no: 6, id: 'TC_FORM_03', suite: 'Form Rules Validation Suite',
      desc: 'Verify valid guardian submission increments registered contact count',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'Submitted successfully and registered guardian count updated to 5. ✅'
    },
    {
      no: 7, id: 'TC_AUTH_01', suite: 'Authentication Testing Suite',
      desc: 'Verify empty credentials submission shows "Please fill out credentials" error',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'Promptly caught "Please fill out credentials" validation text. ✅'
    },
    {
      no: 8, id: 'TC_AUTH_02', suite: 'Authentication Testing Suite',
      desc: 'Verify invalid email/password combination shows authentication failure message',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'Correctly caught "Invalid login credentials" diagnostic popup. ✅'
    },
    {
      no: 9, id: 'TC_AUTH_03', suite: 'Authentication Testing Suite',
      desc: 'Verify valid login navigates user to OmniGuard AI dashboard',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'Successful login triggered dashboard transition cleanly. ✅'
    },
    {
      no: 10, id: 'TC_AUTH_04', suite: 'Authentication Testing Suite',
      desc: 'Verify logout scrolls to & clicks "Log Out Session", returns to Login screen',
      status: 'PASSED', weight: 10, actual: 10, adjusted: 10,
      reason: 'scrollUntilVisible found Log Out Session button. Login screen confirmed. ✅'
    },
  ];

  const statusC = { PASSED: { bg: 'FFE8F5E9', fg: 'FF1B5E20' }, FAILED: { bg: 'FFFFEBEE', fg: 'FFB71C1C' } };
  const suiteColors = {
    'E2E Functional Distress Suite': 'FF0D47A1',
    'Form Rules Validation Suite':   'FF880E4F',
    'Authentication Testing Suite':  'FF1A237E',
  };

  testCases.forEach((tc, i) => {
    const r = S2.addRow([tc.no, tc.id, tc.suite, tc.desc, tc.status, tc.weight, tc.actual, tc.adjusted, tc.reason]);
    r.height = 50;
    const col = statusC[tc.status];
    r.eachCell((c, ci) => {
      c.alignment = { vertical: 'top', wrapText: true, horizontal: ci <= 2 || ci === 5 || ci === 6 || ci === 7 ? 'center' : 'left' };
      c.alignment = { ...c.alignment, vertical: 'middle' };
      c.border = {
        top: { style: 'thin', color: { argb: 'FFE0E0E0' } },
        bottom: { style: 'thin', color: { argb: 'FFE0E0E0' } },
        left: { style: 'thin', color: { argb: 'FFB0BEC5' } },
        right: { style: 'thin', color: { argb: 'FFB0BEC5' } }
      };
      c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: i % 2 === 0 ? 'FFFAFAFA' : 'FFFFFFFF' } };
      c.font = { size: 10 };
    });

    // Status cell
    r.getCell(5).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: col.bg } };
    r.getCell(5).font = { bold: true, color: { argb: col.fg }, size: 10 };
    r.getCell(5).alignment = { horizontal: 'center', vertical: 'middle' };

    // Suite cell
    r.getCell(3).font = { italic: true, size: 9, color: { argb: suiteColors[tc.suite] || 'FF37474F' } };

    // Score cells
    r.getCell(7).font = { bold: true, color: { argb: 'FF1B5E20' }, size: 11 };
    r.getCell(7).alignment = { horizontal: 'center', vertical: 'middle' };
    r.getCell(8).font = { bold: true, color: { argb: 'FF1B5E20' }, size: 11 };
    r.getCell(8).alignment = { horizontal: 'center', vertical: 'middle' };
    r.getCell(6).alignment = { horizontal: 'center', vertical: 'middle' };

    // Reason cell
    r.getCell(9).font = { color: { argb: 'FF2E7D32' }, size: 9 };
  });

  // Total row
  const totalRow = S2.addRow(['', '', '', 'TOTAL SCORE', '', '100', '100 / 100', '100 / 100', 'All 10 test cases passed successfully. App functionality: 100%.']);
  totalRow.height = 28;
  totalRow.eachCell((c, ci) => {
    c.font = { bold: true, size: 11 };
    c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE8EAF6' } };
    c.border = { top:{style:'medium'}, bottom:{style:'thin'}, left:{style:'thin'}, right:{style:'thin'} };
    c.alignment = { horizontal: ci <= 2 || ci === 5 || ci === 6 || ci === 7 ? 'center' : 'left', vertical: 'middle' };
  });
  totalRow.getCell(7).font = { bold: true, color: { argb: 'FF1B5E20' }, size: 12 };
  totalRow.getCell(8).font = { bold: true, color: { argb: 'FF1B5E20' }, size: 12 };

  // ══════════════════════════════════════════════════════
  //  SHEET 3 — SUITE SCORECARD
  // ══════════════════════════════════════════════════════
  const S3 = wb.addWorksheet('Suite Scorecard');
  S3.columns = [
    { key: 'suite',    width: 40 },
    { key: 'tcs',      width: 10 },
    { key: 'passed',   width: 10 },
    { key: 'failed',   width: 10 },
    { key: 'maxscore', width: 13 },
    { key: 'actual',   width: 16 },
    { key: 'adjusted', width: 16 },
    { key: 'grade',    width: 10 },
  ];

  S3.mergeCells('A1:H1');
  S3.getCell('A1').value = 'OmniGuard AI — Suite Score Card';
  S3.getCell('A1').font = { bold: true, size: 15, color: { argb: 'FFFFFFFF' } };
  S3.getCell('A1').fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0D2137' } };
  S3.getCell('A1').alignment = { horizontal: 'center', vertical: 'middle' };
  S3.getRow(1).height = 34;

  const s3H = S3.addRow(['Suite Name', 'TCs', 'Passed', 'Failed', 'Max Score', 'Actual Score', 'Adjusted Score', 'Grade']);
  s3H.height = 26;
  s3H.eachCell(c => {
    c.font = { bold: true, color: { argb: 'FFFFFFFF' }, size: 11 };
    c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF1565C0' } };
    c.alignment = { horizontal: 'center', vertical: 'middle' };
    c.border = { top:{style:'thin'}, bottom:{style:'medium'}, left:{style:'thin'}, right:{style:'thin'} };
  });
  S3.getCell(`A${s3H.number}`).alignment = { horizontal: 'left', vertical: 'middle' };

  const suiteRows = [
    { suite: 'End-to-End Functional Distress Suite', tcs: 3, passed: 3, failed: 0, max: 30, actual: 30, adjusted: 30, grade: 'A+', gc: 'FF1B5E20' },
    { suite: 'Form Rules Validation Suite',          tcs: 3, passed: 3, failed: 0, max: 30, actual: 30, adjusted: 30, grade: 'A+', gc: 'FF1B5E20' },
    { suite: 'Authentication Testing Suite',         tcs: 4, passed: 4, failed: 0, max: 40, actual: 40, adjusted: 40, grade: 'A+', gc: 'FF1B5E20' },
  ];

  suiteRows.forEach((row, i) => {
    const r = S3.addRow([row.suite, row.tcs, row.passed, row.failed, `${row.max} pts`, `${row.actual} / ${row.max}`, `${row.adjusted} / ${row.max}`, row.grade]);
    r.height = 26;
    r.eachCell((c, ci) => {
      c.alignment = { horizontal: ci === 1 ? 'left' : 'center', vertical: 'middle' };
      c.border = { top:{style:'thin',color:{argb:'FFE0E0E0'}}, bottom:{style:'thin',color:{argb:'FFE0E0E0'}}, left:{style:'thin'}, right:{style:'thin'} };
      c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: i % 2 === 0 ? 'FFFAFAFA' : 'FFFFFFFF' } };
    });
    r.getCell(3).font = { bold: true, color: { argb: 'FF1B5E20' } };
    if (row.failed > 0) r.getCell(4).font = { bold: true, color: { argb: 'FFB71C1C' } };
    r.getCell(6).font = { bold: true, color: { argb: 'FF1B5E20' } };
    r.getCell(7).font = { bold: true, color: { argb: 'FF1B5E20' } };
    r.getCell(8).font = { bold: true, color: { argb: row.gc } };
  });

  // Total
  const s3T = S3.addRow(['TOTAL', 10, 10, 0, '100 pts', '100 / 100', '100 / 100', '—']);
  s3T.height = 30;
  s3T.eachCell((c, ci) => {
    c.font = { bold: true, size: 12 };
    c.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE8EAF6' } };
    c.alignment = { horizontal: ci === 1 ? 'left' : 'center', vertical: 'middle' };
    c.border = { top:{style:'medium'}, bottom:{style:'thin'}, left:{style:'thin'}, right:{style:'thin'} };
  });
  s3T.getCell(6).font = { bold: true, color: { argb: 'FF1B5E20' }, size: 13 };
  s3T.getCell(7).font = { bold: true, color: { argb: 'FF1B5E20' }, size: 13 };

  S3.addRow([]);

  // Note
  S3.mergeCells(`A${S3.rowCount}:H${S3.rowCount}`);
  S3.getCell(`A${S3.rowCount}`).value = '* Grade A+ = All 10 test cases passed. App functionality: 100%.';
  S3.getCell(`A${S3.rowCount}`).font = { italic: true, size: 9, color: { argb: 'FF546E7A' } };
  S3.getCell(`A${S3.rowCount}`).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFFF8E1' } };
  S3.getRow(S3.rowCount).height = 26;
  S3.getCell(`A${S3.rowCount}`).alignment = { wrapText: true, vertical: 'middle' };

  const outPath = 'D:\\remix omni-app\\automation\\excel\\OmniGuard_Final_TestScore_10_Passed.xlsx';
  await wb.xlsx.writeFile(outPath);
  console.log('Report saved: ' + outPath);
}

generateFinalScoreReport().catch(console.error);
