# 📱 How to Run Rayara Seva Setu on Your Phone

## ✅ What's New: Preview Feature Added!

**New Workflow:**
1. Generate receipt → **Preview dialog appears**
2. Review receipt details
3. Choose action:
   - **Print/Share** → Opens PDF for printing
   - **New Bill** → Clear form and start next bill
   - **Close** → Keep preview, continue later

**Benefits:**
- ✅ Review receipt before printing
- ✅ Continue with next bill immediately
- ✅ No need to wait for printer
- ✅ Perfect for busy/crowded environments

---

## 🚀 Method 1: Run Directly from Android Studio (Recommended for Testing)

### Prerequisites:
- ✅ Android Studio installed
- ✅ USB cable
- ✅ Android phone (Android 7.0+)

### Step 1: Enable Developer Options on Phone

1. Open **Settings** on your phone
2. Go to **About Phone**
3. Find **Build Number**
4. **Tap "Build Number" 7 times** rapidly
5. You'll see message: "You are now a developer!"

### Step 2: Enable USB Debugging

1. Go back to **Settings**
2. Find **Developer Options** (usually in System or Advanced)
3. Turn on **USB Debugging**
4. Turn on **Install via USB** (if available)

### Step 3: Connect Phone to Computer

1. Connect phone to computer using USB cable
2. On phone, you'll see popup: "Allow USB debugging?"
3. Check "Always allow from this computer"
4. Tap **OK**

### Step 4: Open Project in Android Studio

1. Launch **Android Studio**
2. Click **Open**
3. Navigate to: `C:\Users\AL12381\OneDrive - Elevance Health\Documents\Code\RSS`
4. Click **OK**
5. Wait for Gradle sync (5-10 minutes first time)

### Step 5: Select Your Device

1. At top of Android Studio, you'll see device dropdown
2. Your phone should appear (e.g., "Samsung Galaxy M31")
3. If not visible:
   - Check USB connection
   - Check USB debugging is on
   - Try different USB cable
   - Click refresh icon in dropdown

### Step 6: Run the App

1. Click green **Run** button (▶️) at top
2. OR press **Shift + F10**
3. Wait 2-3 minutes for build
4. App will automatically install and launch on your phone!

### Step 7: Test the App

1. **First Receipt:**
   - Amount: ₹200 (leave name/phone blank - optional for ≤₹500)
   - Payment: Cash
   - Generate → **Preview appears!**
   - Click "New Bill" to continue

2. **Second Receipt:**
   - Amount: ₹1000 (name/phone required for >₹500)
   - Fill details
   - Generate → Preview → Print when ready

---

## 🔧 Method 2: Install APK Directly (For Production Use)

### Step 1: Build APK in Android Studio

1. Open project in Android Studio
2. Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**
3. Wait for build (2-3 minutes)
4. Click **"locate"** in notification
5. APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Step 2: Transfer APK to Phone

**Option A: USB Cable**
1. Connect phone to computer
2. Copy `app-debug.apk` to phone's Download folder

**Option B: WhatsApp/Email**
1. Send APK file to yourself via WhatsApp/Email
2. Download on phone

**Option C: Google Drive/Cloud**
1. Upload APK to Google Drive
2. Download on phone

### Step 3: Install APK on Phone

1. Open **File Manager** on phone
2. Go to **Downloads** folder
3. Tap on **app-debug.apk**
4. If prompted "Install unknown apps":
   - Tap **Settings**
   - Enable **Allow from this source**
   - Go back and tap APK again
5. Tap **Install**
6. Tap **Open** when done

### Step 4: App is Ready!

- Find "Rayara Seva Setu" icon on home screen
- Tap to open and start billing!

---

## 📋 New Preview Feature - How It Works

### Old Behavior (Before):
```
Generate Receipt → PDF opens immediately → Can't continue until closed
```

### New Behavior (Now):
```
Generate Receipt → Preview Dialog → Choose Action:
  1. Print/Share → Opens PDF
  2. New Bill → Clear form, start next
  3. Close → Keep preview
```

### Preview Dialog Shows:
- ✅ Organization name
- ✅ Receipt number
- ✅ Date
- ✅ Customer name & phone
- ✅ Service & amount
- ✅ Total
- ✅ Payment mode
- ✅ Success message

### Three Action Buttons:

**1. ಮುದ್ರಿಸಿ / ಹಂಚಿಕೊಳ್ಳಿ (Print/Share)**
- Opens PDF in viewer
- Choose to:
  - Print to any printer
  - Share via WhatsApp
  - Share via Email
  - Save to phone
- Preview stays open after sharing

**2. ಹೊಸ ರಶೀದಿ (New Bill)**
- Closes preview
- Clears all fields
- Ready for next customer
- **Fastest way to continue billing!**

**3. ಮುಚ್ಚಿ (Close)**
- Closes preview
- Keeps current form data
- Can review/edit before next bill

---

## 🎯 Typical Usage Flow (Crowded Place)

### Fast Billing Mode:
```
Customer 1:
  ₹200 → Generate → Preview → "New Bill" (5 seconds)

Customer 2:
  ₹500 → Generate → Preview → "New Bill" (5 seconds)

Customer 3:
  ₹1000 → Enter name/phone → Generate → Preview → "New Bill" (15 seconds)

...continue...

End of Day:
  Go to History → See all receipts → Print batch if needed
```

### With Printing:
```
Customer 1:
  ₹200 → Generate → Preview → "Print" → Select printer → "New Bill"

Customer 2:
  ₹500 → Generate → Preview → "Print" → WhatsApp share → "New Bill"
```

---

## 🖨️ Printer Options

### Option 1: Regular Printer (Current)
- **Any WiFi/USB printer**
- Print from PDF viewer
- Works with office printers
- Can batch print later

### Option 2: Bluetooth Thermal Printer (Phase 2)
- **Direct printing from app**
- 3-5 second print time
- Portable, battery-powered
- No ink needed
- Cost: ₹3,000-8,000

**When ready for thermal printer:**
- We'll add direct Bluetooth printing
- One-tap print (no PDF viewer)
- Instant receipt printing

---

## 🔍 Troubleshooting

### Phone Not Detected in Android Studio

**Solution 1: Check USB Debugging**
- Settings → Developer Options → USB Debugging (ON)

**Solution 2: Try Different USB Mode**
- When connected, swipe down notification
- Tap "USB for file transfer"
- Select "File Transfer" or "MTP"

**Solution 3: Install USB Drivers**
- Windows may need phone-specific drivers
- Check phone manufacturer website

**Solution 4: Try Different Cable**
- Some cables are charge-only
- Use data transfer cable

### App Won't Install (Unknown Sources)

**Solution:**
1. Settings → Security
2. Enable "Unknown Sources" or "Install unknown apps"
3. Select your file manager/browser
4. Enable installation

### Preview Dialog Not Showing

**Solution:**
- This is new feature, make sure you have latest code
- Rebuild app: Build → Clean Project → Rebuild Project

### PDF Won't Open

**Solution:**
1. Install PDF viewer app:
   - Google PDF Viewer
   - Adobe Acrobat Reader
   - WPS Office
2. Grant storage permissions when prompted

---

## 📊 Testing Checklist

### Basic Tests:
- [ ] ₹100 without details → Preview → New Bill
- [ ] ₹500 without details → Preview → Print
- [ ] ₹1000 with details → Preview → New Bill
- [ ] Generate 5 receipts quickly
- [ ] View history
- [ ] Search receipt
- [ ] Reprint old receipt

### Preview Feature Tests:
- [ ] Preview shows correct details
- [ ] "Print" button opens PDF
- [ ] "New Bill" clears form
- [ ] "Close" keeps form data
- [ ] Can generate multiple receipts without closing preview

---

## 🎉 You're Ready!

Your app now has:
- ✅ Fast billing (15-20 seconds)
- ✅ **Preview before printing**
- ✅ **Continue with next bill immediately**
- ✅ Optional customer details (≤₹500)
- ✅ Receipt history
- ✅ Kannada language
- ✅ Offline mode

**Perfect for busy, crowded environments!**

---

## 📞 Quick Reference

**Generate Receipt:** Fill form → Generate → Preview appears  
**Print Receipt:** Preview → "Print/Share" button  
**Next Customer:** Preview → "New Bill" button  
**View History:** Top-right clock icon  
**Search Receipt:** History → Search box  

---

**Version:** 1.0.1  
**Last Updated:** August 9, 2026  
**New Feature:** Receipt Preview Dialog
