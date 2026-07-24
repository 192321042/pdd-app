import React, { useState } from 'react';
import {
  StyleSheet,
  TextInput,
  TouchableOpacity,
  ScrollView,
  View,
  KeyboardAvoidingView,
  Platform,
  Switch,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import QRCode from 'react-native-qrcode-svg';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';

const COLOR_PRESETS = [
  { name: 'Sleek Blue', value: '#007AFF', bg: '#E3F2FD' },
  { name: 'Sunset Orange', value: '#FF9500', bg: '#FFF3E0' },
  { name: 'Neon Green', value: '#34C759', bg: '#E8F5E9' },
  { name: 'Royal Purple', value: '#AF52DE', bg: '#F3E5F5' },
  { name: 'Sleek Dark', value: '#1C1C1E', bg: '#ECEFF1' },
];

export default function HomeScreen() {
  const [text, setText] = useState('https://expo.dev');
  const [size, setSize] = useState(180);
  const [fgColor, setFgColor] = useState('#007AFF');
  const [showLogo, setShowLogo] = useState(true);
  const [history, setHistory] = useState<string[]>([
    'https://expo.dev',
    'https://reactnative.dev',
  ]);

  const handleGenerate = () => {
    if (text.trim() && !history.includes(text.trim())) {
      setHistory([text.trim(), ...history.slice(0, 4)]);
    }
  };

  const selectPreset = (color: string) => {
    setFgColor(color);
  };

  const loadHistoryItem = (item: string) => {
    setText(item);
  };

  // Center logo asset (relative path to assets/images/react-logo.png)
  const logoSource = require('../../assets/images/react-logo.png');

  return (
    <ThemedView style={styles.container}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={{ flex: 1 }}
      >
        <ScrollView
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          <SafeAreaView style={styles.safeArea}>
            {/* Header */}
            <ThemedView style={styles.header}>
              <ThemedText type="title" style={styles.headerTitle}>
                QR Code Generator
              </ThemedText>
              <ThemedText type="subtitle" style={styles.headerSubtitle}>
                Create fully customized scannable QR codes instantly
              </ThemedText>
            </ThemedView>

            {/* QR Preview Card */}
            <ThemedView type="backgroundElement" style={styles.previewCard}>
              <View style={styles.qrWrapper}>
                {text.trim().length > 0 ? (
                  <QRCode
                    value={text}
                    size={size}
                    color={fgColor}
                    backgroundColor="#FFFFFF"
                    logo={showLogo ? logoSource : undefined}
                    logoSize={Math.floor(size * 0.22)}
                    logoBackgroundColor="#FFFFFF"
                    logoBorderRadius={8}
                    logoMargin={3}
                  />
                ) : (
                  <ThemedText type="small" style={styles.placeholderText}>
                    Enter text below to generate QR code
                  </ThemedText>
                )}
              </View>
              {text.trim().length > 0 && (
                <ThemedText type="code" style={[styles.activeUrl, { color: fgColor }]}>
                  {text.length > 30 ? text.substring(0, 27) + '...' : text}
                </ThemedText>
              )}
            </ThemedView>

            {/* Form Inputs & Customization */}
            <ThemedView style={styles.formContainer}>
              {/* TextInput */}
              <ThemedView style={styles.inputGroup}>
                <ThemedText type="defaultSemiBold">Link or Text Content</ThemedText>
                <TextInput
                  style={styles.textInput}
                  placeholder="Enter URL, text, or phone number"
                  placeholderTextColor="#8E8E93"
                  value={text}
                  onChangeText={(val) => {
                    setText(val);
                  }}
                  autoCapitalize="none"
                  autoCorrect={false}
                />
              </ThemedView>

              {/* Color Customization Preset Buttons */}
              <ThemedView style={styles.inputGroup}>
                <ThemedText type="defaultSemiBold">Custom Foreground Color</ThemedText>
                <View style={styles.colorPresetRow}>
                  {COLOR_PRESETS.map((preset) => {
                    const isSelected = fgColor === preset.value;
                    return (
                      <TouchableOpacity
                        key={preset.value}
                        style={[
                          styles.colorPresetBtn,
                          { backgroundColor: preset.value },
                          isSelected && styles.colorPresetBtnSelected,
                        ]}
                        onPress={() => selectPreset(preset.value)}
                        activeOpacity={0.8}
                      >
                        {isSelected && <View style={styles.innerDot} />}
                      </TouchableOpacity>
                    );
                  })}
                </View>
              </ThemedView>

              {/* Configuration Toggles */}
              <ThemedView type="backgroundElement" style={styles.controlRow}>
                <ThemedView style={styles.toggleContainer}>
                  <ThemedText type="default">Center Brand Logo</ThemedText>
                  <Switch
                    value={showLogo}
                    onValueChange={setShowLogo}
                    trackColor={{ false: '#767577', true: fgColor }}
                    thumbColor={Platform.OS === 'android' ? '#f4f3f4' : ''}
                  />
                </ThemedView>

                <ThemedView style={styles.divider} />

                {/* Size adjustment controls */}
                <ThemedView style={styles.sizeControlContainer}>
                  <ThemedText type="default">QR Code Dimension</ThemedText>
                  <View style={styles.sizeBtnRow}>
                    <TouchableOpacity
                      style={[styles.sizeBtn, size === 140 && styles.activeSizeBtn]}
                      onPress={() => setSize(140)}
                    >
                      <ThemedText type="small" style={size === 140 && styles.activeSizeBtnText}>Small</ThemedText>
                    </TouchableOpacity>
                    <TouchableOpacity
                      style={[styles.sizeBtn, size === 180 && styles.activeSizeBtn]}
                      onPress={() => setSize(180)}
                    >
                      <ThemedText type="small" style={size === 180 && styles.activeSizeBtnText}>Medium</ThemedText>
                    </TouchableOpacity>
                    <TouchableOpacity
                      style={[styles.sizeBtn, size === 220 && styles.activeSizeBtn]}
                      onPress={() => setSize(220)}
                    >
                      <ThemedText type="small" style={size === 220 && styles.activeSizeBtnText}>Large</ThemedText>
                    </TouchableOpacity>
                  </View>
                </ThemedView>
              </ThemedView>

              {/* Generate button (Manual submission history logger) */}
              <TouchableOpacity
                style={[styles.generateBtn, { backgroundColor: fgColor }]}
                onPress={handleGenerate}
                activeOpacity={0.9}
              >
                <ThemedText type="defaultSemiBold" style={styles.generateBtnText}>
                  Save to Quick History
                </ThemedText>
              </TouchableOpacity>
            </ThemedView>

            {/* Quick History Log */}
            {history.length > 0 && (
              <ThemedView style={styles.historySection}>
                <ThemedText type="defaultSemiBold" style={styles.historyTitle}>
                  Quick History Log
                </ThemedText>
                <View style={styles.historyList}>
                  {history.map((item, idx) => (
                    <TouchableOpacity
                      key={`${item}-${idx}`}
                      style={styles.historyItem}
                      onPress={() => loadHistoryItem(item)}
                      activeOpacity={0.7}
                    >
                      <View style={[styles.historyDot, { backgroundColor: fgColor }]} />
                      <ThemedText type="small" style={styles.historyText} numberOfLines={1}>
                        {item}
                      </ThemedText>
                    </TouchableOpacity>
                  ))}
                </View>
              </ThemedView>
            )}
          </SafeAreaView>
        </ScrollView>
      </KeyboardAvoidingView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
  },
  safeArea: {
    flex: 1,
    paddingHorizontal: Spacing.four,
    paddingTop: Spacing.three,
    paddingBottom: BottomTabInset + Spacing.four,
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    width: '100%',
    gap: Spacing.four,
  },
  header: {
    alignItems: 'center',
    gap: Spacing.one,
  },
  headerTitle: {
    fontSize: 26,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  headerSubtitle: {
    textAlign: 'center',
    opacity: 0.7,
    fontSize: 14,
    paddingHorizontal: Spacing.two,
  },
  previewCard: {
    padding: Spacing.four,
    borderRadius: Spacing.four,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 12,
    elevation: 4,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#EFEFEF',
  },
  qrWrapper: {
    padding: Spacing.three,
    backgroundColor: '#FFFFFF',
    borderRadius: Spacing.three,
    alignItems: 'center',
    justifyContent: 'center',
  },
  placeholderText: {
    color: '#8E8E93',
    marginVertical: Spacing.four,
  },
  activeUrl: {
    marginTop: Spacing.three,
    fontSize: 12,
    fontWeight: 'bold',
    opacity: 0.8,
  },
  formContainer: {
    gap: Spacing.four,
  },
  inputGroup: {
    gap: Spacing.two,
  },
  textInput: {
    height: 52,
    borderWidth: 1.5,
    borderColor: '#D1D1D6',
    borderRadius: Spacing.three,
    paddingHorizontal: Spacing.three,
    fontSize: 16,
    backgroundColor: '#F2F2F7',
    color: '#000000',
  },
  colorPresetRow: {
    flexDirection: 'row',
    gap: Spacing.three,
    marginTop: Spacing.one,
  },
  colorPresetBtn: {
    width: 44,
    height: 44,
    borderRadius: 22,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: 'transparent',
  },
  colorPresetBtnSelected: {
    borderColor: '#FFFFFF',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevation: 3,
  },
  innerDot: {
    width: 10,
    height: 10,
    borderRadius: 5,
    backgroundColor: '#FFFFFF',
  },
  controlRow: {
    padding: Spacing.three,
    borderRadius: Spacing.three,
    gap: Spacing.three,
  },
  toggleContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: 'transparent',
  },
  divider: {
    height: 1,
    backgroundColor: '#E5E5EA',
    opacity: 0.5,
  },
  sizeControlContainer: {
    gap: Spacing.two,
    backgroundColor: 'transparent',
  },
  sizeBtnRow: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
  sizeBtn: {
    flex: 1,
    height: 38,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#E5E5EA',
  },
  activeSizeBtn: {
    backgroundColor: '#3A3A3C',
  },
  activeSizeBtnText: {
    color: '#FFFFFF',
  },
  generateBtn: {
    height: 52,
    borderRadius: Spacing.three,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 3,
  },
  generateBtnText: {
    color: '#FFFFFF',
    fontSize: 16,
  },
  historySection: {
    gap: Spacing.two,
    marginTop: Spacing.two,
  },
  historyTitle: {
    fontSize: 15,
  },
  historyList: {
    gap: Spacing.two,
  },
  historyItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    paddingVertical: Spacing.two,
    paddingHorizontal: Spacing.three,
    backgroundColor: '#F2F2F7',
    borderRadius: 8,
  },
  historyDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  historyText: {
    flex: 1,
    color: '#3A3A3C',
  },
});

