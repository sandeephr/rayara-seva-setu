# Rayara Seva Setu (ರಾಯರ ಸೇವಾ ಸೇತು)

Mobile Billing Application for ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ

## Overview

A fast, offline-capable Android mobile app for generating donation receipts with Bluetooth thermal printer support.

## Features

### Phase 1 (Current)
- ✅ Quick billing with predefined amounts (₹100, ₹200, ₹500, ₹1000, ₹2000, ₹2500)
- ✅ Custom amount entry
- ✅ Customer details (Name, Phone)
- ✅ Payment mode tracking (Cash/PhonePe/Online)
- ✅ Auto-generated receipt numbers
- ✅ PDF receipt generation
- ✅ Receipt history and search
- ✅ Daily summary reports
- ✅ Kannada language support
- ✅ Offline mode (no internet required)

### Phase 2 (Upcoming)
- 🔄 Bluetooth thermal printer integration
- 🔄 QR code on receipts
- 🔄 WhatsApp receipt sharing
- 🔄 Cloud backup

## Tech Stack

- **Platform:** Android (Kotlin)
- **UI Framework:** Jetpack Compose
- **Database:** Room (SQLite)
- **PDF Generation:** iText
- **Architecture:** MVVM with Clean Architecture

## Project Structure

```
RSS/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/rayara/sevasetu/
│   │   │   │   ├── data/
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   └── entities/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── models/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── billing/
│   │   │   │   │   ├── history/
│   │   │   │   │   ├── settings/
│   │   │   │   │   └── theme/
│   │   │   │   ├── utils/
│   │   │   │   │   ├── PDFGenerator.kt
│   │   │   │   │   ├── ReceiptFormatter.kt
│   │   │   │   │   └── Constants.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── font/
│   │   │   │   ├── values/
│   │   │   │   └── drawable/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Organization Details

**Name:** ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ  
**Location:** ಭ್ರಾಹ್ಮಣರ ಬೀದಿ, ದೊಡ್ಡಬಳ್ಳಾಪುರ  
**Trust:** ಶ್ರೀ ರಾಘವೇಂದ್ರ ಗುರುಸಾರ್ವಭೌಮ ಸೇವಾ ಟ್ರಸ್ಟ್ (ರಿ.)

## Receipt Format

```
┌─────────────────────────────────────┐
│  ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ      │
│    ಭ್ರಾಹ್ಮಣರ ಬೀದಿ, ದೊಡ್ಡಬಳ್ಳಾಪುರ     │
│                                     │
│ ಸಂ. 28411        ದಿನಾಂಕ: 09/08/2026 │
│                                     │
│ ಶ್ರೀಮತಿ/ಶ್ರೀ: [Customer Name]        │
│ ದೂರವಾಣಿ: [Phone Number]             │
│                                     │
│ ─────────────────────────────────── │
│ ಸೇವೆ                      ₹500     │
│ ─────────────────────────────────── │
│ ಒಟ್ಟು:                    ₹500     │
│ ಪಾವತಿ ವಿಧಾನ: PhonePe              │
│ ─────────────────────────────────── │
│                                     │
│ ಶ್ರೀ ರಾಘವೇಂದ್ರ ಗುರುಸಾರ್ವಭೌಮ         │
│        ಸೇವಾ ಟ್ರಸ್ಟ್ (ರಿ.)            │
│                                     │
│        ಧನ್ಯವಾದಗಳು                   │
└─────────────────────────────────────┘
```

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- JDK 17 or higher
- Android SDK (API 24+)
- Gradle 8.0+

### Installation

1. Clone or open this project in Android Studio
2. Wait for Gradle sync to complete
3. Connect Android device or start emulator
4. Click Run ▶️

### Building APK

```bash
./gradlew assembleDebug
```

APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## Usage

### Quick Billing Flow
1. Open app
2. Enter customer name and phone
3. Tap amount button (₹100, ₹200, etc.) or enter custom amount
4. Select payment mode (Cash/PhonePe/Online)
5. Tap "Generate Receipt"
6. Receipt generated as PDF
7. Share via WhatsApp or Print

### View History
- Tap menu icon → History
- Search by receipt number or customer name
- View daily totals
- Reprint any receipt

## Development Status

- [x] Project setup
- [x] Database schema
- [x] UI design
- [ ] Billing screen implementation
- [ ] PDF generation
- [ ] History screen
- [ ] Settings screen
- [ ] Testing
- [ ] Thermal printer integration

## Future Enhancements

- Multiple language support (Kannada, English, Hindi)
- SMS receipt option
- Cloud sync and backup
- Multi-user support
- Advanced reporting
- Export to Excel

## License

Private project for ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ

## Contact

For support or queries, contact the development team.

---

**Version:** 1.0.0  
**Last Updated:** August 2026
