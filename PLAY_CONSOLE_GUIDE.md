# Google Play Console & Release Guide: VoiceLetter AI

इस दस्तावेज़ में Google Play Store और Play Console पर **VoiceLetter AI** ऐप को पब्लिश करने और APK/AAB फ़ाइलें डाउनलोड करने की संपूर्ण जानकारी दी गई है।

---

## 1. App Details for Google Play Console

| Field | Value |
|---|---|
| **App Name** | VoiceLetter AI |
| **Short Description** | Speak naturally & create professional formal letters with AI in any language |
| **Category** | Productivity / Tools (उत्पादकता) |
| **Package Name / Application ID** | `com.aistudio.voiceletterai.qvrtzk` |
| **Target SDK** | 36 (Android 16 / 15+ compatible) |
| **Minimum SDK** | 24 (Android 7.0 Nougat+) |
| **Supported Devices** | All Android Smartphones, Foldables, and Tablets |

---

## 2. Permissions Declared & User Data Policy

| Permission | Reason for Google Play Console Form |
|---|---|
| `android.permission.RECORD_AUDIO` | Required exclusively for speech-to-text voice dictation when the user taps the microphone button to create or edit letters. Audio is processed on-device or via user's speech recognition. |
| `android.permission.INTERNET` | Required for optional AI text generation with Gemini and cloud speech recognition. |

*Google Play Data Safety Section:*
- **Microphone Data**: Collected temporarily for transcription only when active; not sold to third parties.
- **Documents & Text**: Stored locally on the user's device in SQLite Room Database.

---

## 3. How to Build APK and AAB (App Bundle)

AI Studio में आप टर्मिनल या सेटिंग्स से सीधे फ़ाइलें बना और डाउनलोड कर सकते हैं:

### 1. Debug APK (तुरंत फ़ोन में इंस्टॉल करने के लिए):
```bash
gradle :app:assembleDebug
```
*Generated file location:*
`app/build/outputs/apk/debug/app-debug.apk`
(यह फ़ाइल प्रोजेक्ट के रूट में `VoiceLetterAI-debug.apk` के नाम से भी उपलब्ध करा दी गई है।)

### 2. Play Store AAB (Google Play Console में अपलोड करने के लिए):
```bash
gradle :app:bundleRelease
```
*Generated file location:*
`app/build/outputs/bundle/release/app-release.aab`

### 3. Release APK:
```bash
gradle :app:assembleRelease
```
*Generated file location:*
`app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 4. Keystore & Signing Details

Google Play Store पर ऐप अपलोड करने के लिए Play App Signing का उपयोग किया जाता है:
1. Play Console में नया ऐप बनाएं (`com.aistudio.voiceletterai.qvrtzk`).
2. **App signing by Google Play** विकल्प को चुनें।
3. `bundleRelease` द्वारा जनरेट की गई `.aab` फ़ाइल को **Production** या **Internal Testing** ट्रैक में ड्रैग और ड्रॉप करें।

---

## 5. Main Features Configured in VoiceLetter AI

1. **Continuous Voice Recognition**: लगातार बिना 5 सेकंड में कटे बोलने की सुविधा (Continuous Listening with pause tolerance).
2. **Smart Intent Filtering**: "एक लेटर बनाना है" जैसे बोलचाल के शब्दों को पहचान कर केवल मुख्य विषय को पत्र में जोड़ना।
3. **100% Native Hindi & Multilingual Support**: हिन्दी, इंग्लिश सहित सभी प्रमुख भारतीय व विश्व भाषाओं में प्रामाणिक पत्र लेखन।
4. **Export Options**: PDF (यूनिकोड फॉन्ट सपोर्ट), DOCX, TXT और सीधा शेयर/प्रिंट।
5. **Offline Room Database**: उपयोगकर्ता के सभी पत्र स्थानीय रूप से सुरक्षित सहेजे जाते हैं।
