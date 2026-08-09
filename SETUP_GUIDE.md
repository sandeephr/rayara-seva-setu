# Rayara Seva Setu - Setup Guide

## Prerequisites

Before you begin, ensure you have the following installed:

1. **Android Studio** (Latest version - Hedgehog or newer)
   - Download from: https://developer.android.com/studio
   
2. **JDK 17** (Java Development Kit)
   - Usually comes bundled with Android Studio
   - Or download from: https://adoptium.net/

3. **Android Device or Emulator**
   - Physical Android device (Android 7.0 / API 24 or higher)
   - OR Android Emulator from Android Studio

## Setup Instructions

### Step 1: Open Project in Android Studio

1. Launch Android Studio
2. Click **"Open"** or **"Open an Existing Project"**
3. Navigate to: `C:\Users\AL12381\OneDrive - Elevance Health\Documents\Code\RSS`
4. Click **"OK"**

### Step 2: Wait for Gradle Sync

1. Android Studio will automatically start syncing Gradle
2. This may take 5-10 minutes on first run (downloads dependencies)
3. Wait for the message: **"Gradle sync finished"**
4. If you see any errors, click **"Sync Project with Gradle Files"** (🐘 icon)

### Step 3: Configure Android SDK

1. Go to **File → Project Structure → SDK Location**
2. Ensure Android SDK location is set (usually auto-detected)
3. Click **"Apply"** and **"OK"**

### Step 4: Connect Device or Start Emulator

#### Option A: Physical Android Device
1. Enable **Developer Options** on your phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable **USB Debugging**:
   - Settings → Developer Options → USB Debugging (ON)
3. Connect phone to computer via USB
4. Allow USB debugging when prompted on phone

#### Option B: Android Emulator
1. Click **"Device Manager"** in Android Studio (phone icon)
2. Click **"Create Device"**
3. Select **"Pixel 5"** or any modern device
4. Select **"API 34"** (Android 14) or latest
5. Click **"Next"** → **"Finish"**
6. Click **"Play"** button to start emulator

### Step 5: Run the App

1. Click the **"Run"** button (▶️ green play icon) in Android Studio
2. OR press **Shift + F10**
3. Select your device/emulator from the dropdown
4. Wait for app to build and install (2-3 minutes first time)
5. App will automatically launch on your device

## Testing the App

### First Receipt Test

1. **Enter Customer Details:**
   - Name: ರಾಮೇಶ್ ಕುಮಾರ್
   - Phone: 9876543210

2. **Select Amount:**
   - Tap **₹500** button

3. **Select Payment Mode:**
   - Tap **PhonePe**

4. **Generate Receipt:**
   - Tap **"ರಶೀದಿ ರಚಿಸಿ"** button
   - PDF will be generated
   - Receipt viewer will open automatically

5. **View History:**
   - Tap **History** icon (clock) in top-right
   - See your generated receipt
   - Tap on receipt to view PDF again

## Troubleshooting

### Problem: Gradle Sync Failed

**Solution:**
1. Check internet connection
2. Click **File → Invalidate Caches → Invalidate and Restart**
3. Wait for Android Studio to restart and sync again

### Problem: App Won't Install on Device

**Solution:**
1. Ensure USB debugging is enabled
2. Try **Build → Clean Project**
3. Then **Build → Rebuild Project**
4. Run again

### Problem: PDF Not Opening

**Solution:**
1. Install a PDF viewer app on your device (e.g., Adobe Acrobat, Google PDF Viewer)
2. Grant storage permissions to the app when prompted

### Problem: Kannada Text Not Showing

**Solution:**
1. Ensure your device supports Kannada fonts (most modern devices do)
2. Update your device to latest Android version
3. Install Noto Sans Kannada font if needed

## Building APK for Distribution

### Debug APK (For Testing)

1. Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for build to complete
3. Click **"locate"** in the notification
4. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`
5. Transfer this APK to any Android device and install

### Release APK (For Production)

1. Go to **Build → Generate Signed Bundle / APK**
2. Select **APK**
3. Create a new keystore (first time only):
   - Click **"Create new..."**
   - Choose location and password
   - Fill in certificate details
4. Select **"release"** build variant
5. Click **"Finish"**
6. APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Next Steps

### Phase 1 Complete ✅
- [x] Basic billing functionality
- [x] PDF generation
- [x] Receipt history
- [x] Kannada language support

### Phase 2 (Future)
- [ ] Bluetooth thermal printer integration
- [ ] QR code on receipts
- [ ] WhatsApp sharing
- [ ] Cloud backup

## Bluetooth Printer Integration (Phase 2)

When ready to add thermal printer support:

1. Purchase a Bluetooth thermal printer (58mm or 80mm)
2. We'll add printer SDK to the project
3. Add printer pairing and connection logic
4. Modify PDF generator to also support thermal printing
5. Test with actual printer

**Recommended Printers:**
- Budget: RPP02N (₹3,000-4,000)
- Mid-range: Zebronics ZEB-POS58BT (₹5,000-6,000)
- Premium: Epson TM-P20 (₹12,000-15,000)

## Support

For any issues or questions:
1. Check this guide first
2. Google the error message
3. Check Android Studio's **Logcat** for detailed errors
4. Contact the development team

## File Structure Reference

```
RSS/
├── app/
│   ├── src/main/
│   │   ├── java/com/rayara/sevasetu/
│   │   │   ├── MainActivity.kt          # App entry point
│   │   │   ├── data/                    # Database & models
│   │   │   ├── ui/
│   │   │   │   ├── billing/            # Billing screen
│   │   │   │   ├── history/            # History screen
│   │   │   │   └── theme/              # App colors & theme
│   │   │   └── utils/                  # PDF generator, constants
│   │   ├── res/                        # Resources (strings, layouts)
│   │   └── AndroidManifest.xml         # App configuration
│   └── build.gradle.kts                # App dependencies
├── build.gradle.kts                    # Project configuration
├── settings.gradle.kts                 # Project settings
└── README.md                           # Project overview
```

## Version History

- **v1.0.0** (August 2026) - Initial release
  - Basic billing
  - PDF receipts
  - History tracking
  - Kannada support

---

**App Name:** Rayara Seva Setu (ರಾಯರ ಸೇವಾ ಸೇತು)  
**Organization:** ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ  
**Version:** 1.0.0  
**Last Updated:** August 2026
