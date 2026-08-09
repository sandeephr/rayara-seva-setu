# 🚀 Quick Start Guide - Rayara Seva Setu

## ⚡ 5-Minute Setup

### 1️⃣ Open in Android Studio
```
File → Open → Select "RSS" folder → OK
```

### 2️⃣ Wait for Gradle Sync
- Bottom status bar will show "Gradle sync in progress..."
- Wait for "Gradle sync finished" (5-10 minutes first time)

### 3️⃣ Connect Device
**Phone:** Enable USB Debugging → Connect USB  
**OR**  
**Emulator:** Device Manager → Create Device → Pixel 5 → API 34 → Start

### 4️⃣ Run App
```
Click ▶️ (Run button) → Select device → Wait 2-3 minutes
```

## ✅ Test Your First Receipt

1. **Customer Name:** ರಾಮೇಶ್ ಕುಮಾರ್
2. **Phone:** 9876543210
3. **Amount:** Tap **₹500**
4. **Payment:** Tap **PhonePe**
5. **Generate:** Tap **"ರಶೀದಿ ರಚಿಸಿ"**
6. **View:** PDF opens automatically!

## 📱 App Features

### Main Screen (Billing)
- Enter customer name & phone
- Quick select amounts: ₹100, ₹200, ₹500, ₹1000, ₹2000, ₹2500
- Custom amount option
- Payment mode: Cash / PhonePe / Online
- Generate PDF receipt instantly

### History Screen
- View all receipts
- Search by receipt number or customer name
- See today's total collection
- Tap receipt to view PDF
- Delete receipts

## 🎯 Key Shortcuts

| Action | Shortcut |
|--------|----------|
| Run App | `Shift + F10` |
| Build Project | `Ctrl + F9` |
| Clean Project | `Build → Clean Project` |
| Sync Gradle | `File → Sync Project with Gradle Files` |

## 📦 Build APK

### For Testing (Debug APK)
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```
APK location: `app/build/outputs/apk/debug/app-debug.apk`

### For Production (Release APK)
```
Build → Generate Signed Bundle / APK → Create keystore → Build
```

## 🔧 Common Issues

### ❌ Gradle Sync Failed
```
File → Invalidate Caches → Invalidate and Restart
```

### ❌ App Won't Install
```
Build → Clean Project → Build → Rebuild Project → Run
```

### ❌ PDF Won't Open
- Install PDF viewer app on device
- Grant storage permissions

## 📊 Receipt Format

```
┌─────────────────────────────────┐
│ ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ    │
│   ಭ್ರಾಹ್ಮಣರ ಬೀದಿ, ದೊಡ್ಡಬಳ್ಳಾಪುರ    │
│                                 │
│ ಸಂ. 28411      ದಿನಾಂಕ: 09/08/26 │
│                                 │
│ ಶ್ರೀಮತಿ/ಶ್ರೀ: ರಾಮೇಶ್ ಕುಮಾರ್        │
│ ದೂರವಾಣಿ: 9876543210            │
│ ─────────────────────────────── │
│ ಸೇವೆ                    ₹500   │
│ ─────────────────────────────── │
│ ಒಟ್ಟು:                  ₹500   │
│ ಪಾವತಿ ವಿಧಾನ: PhonePe          │
│ ─────────────────────────────── │
│                                 │
│ ಶ್ರೀ ರಾಘವೇಂದ್ರ ಗುರುಸಾರ್ವಭೌಮ      │
│       ಸೇವಾ ಟ್ರಸ್ಟ್ (ರಿ.)          │
│                                 │
│         ಧನ್ಯವಾದಗಳು              │
└─────────────────────────────────┘
```

## 🎨 App Screenshots

### Billing Screen
- Clean, simple interface
- Large buttons for quick selection
- Kannada language throughout
- Orange theme (matches organization)

### History Screen
- List of all receipts
- Search functionality
- Today's total summary
- Tap to view/reprint

## 🔮 Coming Soon (Phase 2)

- 🖨️ Bluetooth thermal printer support
- 📱 WhatsApp receipt sharing
- ☁️ Cloud backup
- 📊 Advanced reports
- 🔐 Multi-user support

## 📞 Need Help?

1. Check **SETUP_GUIDE.md** for detailed instructions
2. Check **README.md** for project overview
3. View Android Studio **Logcat** for errors
4. Contact development team

## 🎉 You're Ready!

Your mobile billing app is ready to use. Start generating receipts and tracking donations digitally!

---

**Version:** 1.0.0  
**Platform:** Android 7.0+ (API 24+)  
**Language:** Kannada  
**Size:** ~15 MB
