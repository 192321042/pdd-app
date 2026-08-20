const logger = require('./logger');

class Gestures {
  /**
   * Tap on coordinate point (x, y)
   */
  static async tap(driver, x, y) {
    logger.info(`Performing tap at coordinate: (${x}, ${y})`);
    await driver.performActions([
      {
        type: 'pointer',
        id: 'finger1',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x, y },
          { type: 'pointerDown', button: 0 },
          { type: 'pause', duration: 100 },
          { type: 'pointerUp', button: 0 }
        ]
      }
    ]);
  }

  /**
   * Double Tap on coordinate point (x, y)
   */
  static async doubleTap(driver, x, y) {
    logger.info(`Performing double tap at coordinate: (${x}, ${y})`);
    await driver.performActions([
      {
        type: 'pointer',
        id: 'finger1',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x, y },
          { type: 'pointerDown', button: 0 },
          { type: 'pause', duration: 100 },
          { type: 'pointerUp', button: 0 },
          { type: 'pause', duration: 100 },
          { type: 'pointerDown', button: 0 },
          { type: 'pause', duration: 100 },
          { type: 'pointerUp', button: 0 }
        ]
      }
    ]);
  }

  /**
   * Long press at coordinate (x, y) for specific duration (ms)
   */
  static async longPress(driver, x, y, duration = 1500) {
    logger.info(`Performing long press at: (${x}, ${y}) for ${duration}ms`);
    await driver.performActions([
      {
        type: 'pointer',
        id: 'finger1',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x, y },
          { type: 'pointerDown', button: 0 },
          { type: 'pause', duration },
          { type: 'pointerUp', button: 0 }
        ]
      }
    ]);
  }

  /**
   * Perform drag and drop from start coordinate to end coordinate
   */
  static async dragAndDrop(driver, startX, startY, endX, endY) {
    logger.info(`Performing drag & drop from (${startX}, ${startY}) to (${endX}, ${endY})`);
    await driver.performActions([
      {
        type: 'pointer',
        id: 'finger1',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x: startX, y: startY },
          { type: 'pointerDown', button: 0 },
          { type: 'pause', duration: 600 },
          { type: 'pointerMove', duration: 1000, x: endX, y: endY },
          { type: 'pointerUp', button: 0 }
        ]
      }
    ]);
  }

  /**
   * Swipe from start position to end position
   */
  static async swipe(driver, startX, startY, endX, endY, duration = 250) {
    await driver.performActions([
      {
        type: 'pointer',
        id: 'finger1',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x: startX, y: startY },
          { type: 'pointerDown', button: 0 },
          { type: 'pause', duration: 100 },
          { type: 'pointerMove', duration, x: endX, y: endY },
          { type: 'pointerUp', button: 0 }
        ]
      }
    ]);
  }

  static async swipeUp(driver, percentage = 0.8) {
    logger.info('Performing Swipe Up');
    const { width, height } = await driver.getWindowSize();
    const x = Math.floor(width * 0.5);
    const startY = Math.floor(height * percentage);
    const endY = Math.floor(height * (1 - percentage));
    await this.swipe(driver, x, startY, x, endY);
  }

  static async swipeDown(driver, percentage = 0.8) {
    logger.info('Performing Swipe Down');
    const { width, height } = await driver.getWindowSize();
    const x = Math.floor(width * 0.5);
    const startY = Math.floor(height * (1 - percentage));
    const endY = Math.floor(height * percentage);
    await this.swipe(driver, x, startY, x, endY);
  }

  static async swipeLeft(driver, percentage = 0.8) {
    logger.info('Performing Swipe Left');
    const { width, height } = await driver.getWindowSize();
    const y = Math.floor(height / 2);
    const startX = Math.floor(width * percentage);
    const endX = Math.floor(width * (1 - percentage));
    await this.swipe(driver, startX, y, endX, y);
  }

  static async swipeRight(driver, percentage = 0.8) {
    logger.info('Performing Swipe Right');
    const { width, height } = await driver.getWindowSize();
    const y = Math.floor(height / 2);
    const startX = Math.floor(width * (1 - percentage));
    const endX = Math.floor(width * percentage);
    await this.swipe(driver, startX, y, endX, y);
  }

  /**
   * Scroll down repeatedly until the element matching the selector is displayed.
   */
  static async scrollUntilVisible(driver, elementSelector, maxRetries = 10) {
    logger.info(`Scrolling until element matching [${elementSelector}] is visible`);
    const config = require('../config/appium.config');
    try {
      await driver.setTimeouts(0);
    } catch (e) { }

    let foundEl = null;
    for (let i = 0; i < maxRetries; i++) {
      try {
        const el = await driver.$(elementSelector);
        if (await el.isDisplayed()) {
          logger.info(`Element [${elementSelector}] is now visible after ${i} scrolls.`);
          foundEl = el;
          break;
        }
      } catch (ignored) { }
      await this.swipeUp(driver, 0.6);
      await driver.pause(500);
    }

    try {
      await driver.setTimeouts(config.timeouts.implicit);
    } catch (e) { }

    if (!foundEl) {
      throw new Error(`Element [${elementSelector}] was not found after ${maxRetries} scrolls.`);
    }
    return foundEl;
  }

  /**
   * Pinch zoom gesture
   */
  static async pinch(driver) {
    logger.info('Performing Pinch gesture');
    const { width, height } = await driver.getWindowSize();
    const midX = Math.floor(width / 2);
    const midY = Math.floor(height / 2);

    await driver.performActions([
      {
        type: 'pointer',
        id: 'finger1',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x: midX - 100, y: midY },
          { type: 'pointerDown', button: 0 },
          { type: 'pointerMove', duration: 800, x: midX - 10, y: midY },
          { type: 'pointerUp', button: 0 }
        ]
      },
      {
        type: 'pointer',
        id: 'finger2',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x: midX + 100, y: midY },
          { type: 'pointerDown', button: 0 },
          { type: 'pointerMove', duration: 800, x: midX + 10, y: midY },
          { type: 'pointerUp', button: 0 }
        ]
      }
    ]);
  }

  /**
   * Zoom outward gesture
   */
  static async zoom(driver) {
    logger.info('Performing Zoom gesture');
    const { width, height } = await driver.getWindowSize();
    const midX = Math.floor(width / 2);
    const midY = Math.floor(height / 2);

    await driver.performActions([
      {
        type: 'pointer',
        id: 'finger1',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x: midX - 10, y: midY },
          { type: 'pointerDown', button: 0 },
          { type: 'pointerMove', duration: 800, x: midX - 150, y: midY },
          { type: 'pointerUp', button: 0 }
        ]
      },
      {
        type: 'pointer',
        id: 'finger2',
        parameters: { pointerType: 'touch' },
        actions: [
          { type: 'pointerMove', duration: 0, x: midX + 10, y: midY },
          { type: 'pointerDown', button: 0 },
          { type: 'pointerMove', duration: 800, x: midX + 150, y: midY },
          { type: 'pointerUp', button: 0 }
        ]
      }
    ]);
  }
}

module.exports = Gestures;
// 
