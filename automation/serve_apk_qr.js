const http = require('http');
const fs = require('fs');
const path = require('path');
const os = require('os');
const qrcode = require('qrcode-terminal');

// Path to the APK
const apkPath = path.resolve(__dirname, '../app/build/outputs/apk/debug/app-debug.apk');

if (!fs.existsSync(apkPath)) {
  console.error(`\n❌ Error: APK not found at ${apkPath}`);
  console.log('Please build the app first in Android Studio or by running:');
  console.log('  gradlew assembleDebug\n');
  process.exit(1);
}

// Get the local network IPv4 address
function getLocalIpAddress() {
  const interfaces = os.networkInterfaces();
  for (const interfaceName in interfaces) {
    for (const iface of interfaces[interfaceName]) {
      // Skip loopback and non-IPv4 addresses
      if (iface.family === 'IPv4' && !iface.internal) {
        return iface.address;
      }
    }
  }
  return '127.0.0.1';
}

const localIp = getLocalIpAddress();
const port = 8080;
const downloadUrl = `http://${localIp}:${port}/app-debug.apk`;

// Create HTTP server
const server = http.createServer((req, res) => {
  if (req.url === '/app-debug.apk') {
    const stat = fs.statSync(apkPath);
    res.writeHead(200, {
      'Content-Type': 'application/vnd.android.package-archive',
      'Content-Length': stat.size,
      'Content-Disposition': 'attachment; filename=app-debug.apk'
    });
    const readStream = fs.createReadStream(apkPath);
    readStream.pipe(res);
  } else {
    res.writeHead(404, { 'Content-Type': 'text/plain' });
    res.end('Not Found');
  }
});

server.listen(port, () => {
  console.log('\n================================================================');
  console.log('🚀 Android APK Local Installer Server');
  console.log(`📡 URL: ${downloadUrl}`);
  console.log('================================================================\n');

  console.log('Scan the QR code below using your mobile camera or scanner to install:\n');
  
  // Render the QR code in the terminal
  qrcode.generate(downloadUrl, { small: true });

  console.log('\n================================================================');
  console.log('💡 Note: Ensure your mobile phone is connected to the SAME Wi-Fi network.');
  console.log('Press Ctrl+C to stop the server.');
  console.log('================================================================\n');
});
