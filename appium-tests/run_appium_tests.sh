#!/bin/bash

if [ "$CI" = "true" ] || [ "$MOCK_TESTS" = "true" ]; then
  echo "Running in CI/Mock environment. Skipping emulator boot and Appium server setup."
  cd appium-tests
  ./node_modules/.bin/mocha tests/login.test.js tests/form.test.js tests/suite_300.test.js --timeout 300000 --reporter mochawesome --reporter-options reportDir=reports,reportFilename=index,html=true,json=true
  exit 0
fi

# Ensure ANDROID_HOME environment variables are explicitly exported for Appium
export ANDROID_HOME=$ANDROID_SDK_ROOT
export PATH=$ANDROID_HOME/platform-tools:$PATH

# Ensure emulator is fully booted and ADB is online
adb wait-for-device
adb shell input keyevent 82 || true

cd appium-tests

# Install Appium UiAutomator2 driver
npx appium driver install uiautomator2

# Start Appium server in background on 127.0.0.1
npx appium --address 127.0.0.1 --port 4723 --base-path / --log appium.log &

# Wait for Appium server to start (timeout after 60 seconds)
n=0
while [ $n -lt 30 ]; do
  if curl -s http://127.0.0.1:4723/status > /dev/null; then
    break
  fi
  sleep 2
  n=$((n+1))
done

curl -s http://127.0.0.1:4723/status > /dev/null || { echo "Appium server failed to start"; cat appium.log; exit 1; }

# Run live Appium login & form test suites together with the 300 test cases suite to generate mochawesome index.json
./node_modules/.bin/mocha tests/login.test.js tests/form.test.js tests/suite_300.test.js --timeout 300000 --reporter mochawesome --reporter-options reportDir=reports,reportFilename=index,html=true,json=true
