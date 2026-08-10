# App Icon Setup Guide

## 🎨 Adding Custom App Icon

You have the icon image at: `C:\Users\AL12381\Downloads\Designer.png`

---

## 📋 **METHOD 1: Using Android Studio (Recommended - Easiest)**

### **Steps:**

1. **Open Android Studio**
2. **Right-click on `app` folder** in Project view
3. **Select:** New → Image Asset
4. **In the dialog:**
   - Asset Type: Launcher Icons (Adaptive and Legacy)
   - Name: `ic_launcher`
   - Path: Browse to `C:\Users\AL12381\Downloads\Designer.png`
   - Trim: Yes (if needed)
   - Resize: 100%
5. **Click "Next"** then **"Finish"**

Android Studio will automatically create all required sizes:
- `mipmap-mdpi/ic_launcher.png` (48x48)
- `mipmap-hdpi/ic_launcher.png` (72x72)
- `mipmap-xhdpi/ic_launcher.png` (96x96)
- `mipmap-xxhdpi/ic_launcher.png` (144x144)
- `mipmap-xxxhdpi/ic_launcher.png` (192x192)
- `mipmap-anydpi-v26/ic_launcher.xml` (Adaptive icon)

---

## 📋 **METHOD 2: Manual Setup (If no Android Studio)**

### **Step 1: Create Icon Sizes**

You need to create 5 different sizes of your icon:

| Folder | Size | Pixels |
|--------|------|--------|
| mipmap-mdpi | 48x48 | Low density |
| mipmap-hdpi | 72x72 | Medium density |
| mipmap-xhdpi | 96x96 | High density |
| mipmap-xxhdpi | 144x144 | Extra high density |
| mipmap-xxxhdpi | 192x192 | Extra extra high density |

### **Step 2: Use Online Tool**

1. **Go to:** https://icon.kitchen/
   - OR: https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html

2. **Upload your image:** `Designer.png`

3. **Configure:**
   - Name: `ic_launcher`
   - Shape: Circle or Square (your choice)
   - Background: Transparent or color

4. **Download the ZIP** file

5. **Extract** and you'll get all the mipmap folders

### **Step 3: Copy to Project**

1. **Extract the downloaded ZIP**

2. **Copy all mipmap folders** to:
   ```
   C:\Users\AL12381\OneDrive - Elevance Health\Documents\Code\RSS\rayara-seva-setu\app\src\main\res\
   ```

3. **Final structure:**
   ```
   res/
   ├── mipmap-mdpi/
   │   └── ic_launcher.png
   ├── mipmap-hdpi/
   │   └── ic_launcher.png
   ├── mipmap-xhdpi/
   │   └── ic_launcher.png
   ├── mipmap-xxhdpi/
   │   └── ic_launcher.png
   ├── mipmap-xxxhdpi/
   │   └── ic_launcher.png
   ├── mipmap-anydpi-v26/
   │   ├── ic_launcher.xml
   │   └── ic_launcher_round.xml
   ├── values/
   └── values-kn/
   ```

---

## 📋 **METHOD 3: Quick Single Icon (Simple but not ideal)**

If you just want a quick fix:

1. **Resize your image** to 192x192 pixels using any image editor

2. **Create folder:**
   ```
   C:\Users\AL12381\OneDrive - Elevance Health\Documents\Code\RSS\rayara-seva-setu\app\src\main\res\mipmap-xxxhdpi\
   ```

3. **Copy** `Designer.png` and rename to `ic_launcher.png`

4. **Repeat for other densities** (or Android will scale automatically, but won't look as good)

---

## ✅ **Verify AndroidManifest.xml**

After adding icons, check that `AndroidManifest.xml` has:

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    ...>
```

This should already be there, but verify it points to `ic_launcher`.

---

## 🎯 **RECOMMENDED APPROACH**

**Use Method 2 (Online Tool)** - It's the easiest without Android Studio:

1. Go to: https://icon.kitchen/
2. Upload `Designer.png`
3. Download ZIP
4. Extract to `app/src/main/res/`
5. Commit and push

---

## 🧪 **Testing**

After adding the icon:

1. **Build the app**
2. **Install on phone**
3. **Check home screen** - your icon should appear
4. **Check app drawer** - icon should be there too

---

## 📊 **File Sizes**

Expected file sizes:
- mdpi: ~2-5 KB
- hdpi: ~3-8 KB
- xhdpi: ~5-12 KB
- xxhdpi: ~8-20 KB
- xxxhdpi: ~12-30 KB

**Total:** ~50-100 KB added to APK

---

## ⚠️ **Common Issues**

**Issue 1: Icon looks blurry**
- Solution: Make sure you're using PNG format with transparency
- Use high-resolution source image (at least 512x512)

**Issue 2: Icon has white background**
- Solution: Use PNG with transparent background
- Or set background color in icon.kitchen

**Issue 3: Icon not showing**
- Solution: Clean build and reinstall app
- Check AndroidManifest.xml has correct icon reference

---

## 🚀 **Quick Steps Summary**

1. Go to https://icon.kitchen/
2. Upload `C:\Users\AL12381\Downloads\Designer.png`
3. Download generated icons (ZIP)
4. Extract to `app/src/main/res/`
5. Commit and push
6. Build and install

---

## 📞 **Need Help?**

If you face issues:
1. Share screenshot of your res folder structure
2. Let me know which method you're using
3. I'll help debug

---

**Estimated Time:** 5-10 minutes

**Last Updated:** August 10, 2026, 5:35 PM IST
