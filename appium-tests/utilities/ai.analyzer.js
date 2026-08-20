const logger = require('./logger');

class AIAnalyzer {
  /**
   * Captures the active screen XML hierarchy layout source and analyses UI controls.
   */
  static async analyzeCurrentScreen(driver) {
    logger.info('AI Analyser: Inspecting current screen structure...');
    try {
      const sourceXml = await driver.getPageSource();
      const info = {
        activityName: await driver.getCurrentActivity(),
        inputs: [],
        buttons: [],
        lists: [],
        anomalies: []
      };

      // We simulate element scanning by searching layout selectors matching standard Android resource types
      const editTexts = await driver.$$('//android.widget.EditText');
      for (const edit of editTexts) {
        info.inputs.push({
          id: await edit.getAttribute('resource-id') || 'unspecified-id',
          text: await edit.getText() || '',
          hint: await edit.getAttribute('hint') || '',
          clickable: await edit.getAttribute('clickable') === 'true',
          focused: await edit.getAttribute('focused') === 'true'
        });
      }

      const buttons = await driver.$$('//android.widget.Button | //android.widget.ImageButton');
      for (const btn of buttons) {
        info.buttons.push({
          id: await btn.getAttribute('resource-id') || 'unspecified-id',
          text: await btn.getText() || await btn.getAttribute('content-desc') || 'Unnamed Button',
          clickable: await btn.getAttribute('clickable') === 'true'
        });
      }

      const lists = await driver.$$('//android.widget.ListView | //androidx.recyclerview.widget.RecyclerView');
      for (const list of lists) {
        info.lists.push({
          id: await list.getAttribute('resource-id') || 'unspecified-list',
          scrollable: await list.getAttribute('scrollable') === 'true'
        });
      }

      logger.info(`AI Analyser: Detected ${info.inputs.length} inputs, ${info.buttons.length} buttons, and ${info.lists.length} lists.`);
      return info;
    } catch (err) {
      logger.error(`AI Analyser failed: ${err.message}`);
      return null;
    }
  }

  /**
   * Form Autodiscovery: Returns pre-configured validation sets for dynamic testing.
   */
  static discoverFormRules(screenInfo) {
    logger.info('AI Analyser: Mapping form rule strategies dynamically...');
    const rules = [];
    
    screenInfo.inputs.forEach(input => {
      const id = input.id.toLowerCase();
      const hint = input.hint.toLowerCase();
      
      if (id.includes('email') || hint.includes('email')) {
        rules.push({
          targetId: input.id,
          type: 'email',
          testValues: {
            invalid: ['plainaddress', '@missingusername.com', 'user@.com'],
            valid: ['valid.user@example.com']
          }
        });
      } else if (id.includes('phone') || id.includes('mobile') || hint.includes('phone') || hint.includes('mobile')) {
        rules.push({
          targetId: input.id,
          type: 'phone',
          testValues: {
            invalid: ['123', 'abc', '123456789012345'],
            valid: ['+919999988888', '9876543210']
          }
        });
      } else if (id.includes('password') || hint.includes('password')) {
        rules.push({
          targetId: input.id,
          type: 'password',
          testValues: {
            invalid: ['123', 'short'],
            valid: ['P@ssword123!', 'AdminSecurity#9']
          }
        });
      } else {
        rules.push({
          targetId: input.id,
          type: 'generic_text',
          testValues: {
            invalid: ['', 'A'.repeat(101)], // testing empty & boundary values
            valid: ['Standard Input']
          }
        });
      }
    });

    return rules;
  }
}

module.exports = AIAnalyzer;
