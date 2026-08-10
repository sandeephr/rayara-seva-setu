# Kannada Font Fix - Bundle Font with App

## Problem
The current approach relies on system fonts which may not exist on all Android devices:
- `/system/fonts/NotoSansKannada-Regular.ttf` - May not exist
- `/system/fonts/DroidSansFallback.ttf` - May not exist

If both fail, `getKannadaFont()` returns `null` and Kannada text shows as blank boxes.

## Solution
Bundle a Kannada font with the app in the assets folder.

## Steps to Fix

### 1. Download Noto Sans Kannada Font
- Go to: https://fonts.google.com/noto/specimen/Noto+Sans+Kannada
- Download the font
- Extract `NotoSansKannada-Regular.ttf`

### 2. Add Font to Assets
- Create folder: `app/src/main/assets/fonts/`
- Copy `NotoSansKannada-Regular.ttf` to this folder

### 3. Update PDFGenerator.kt

Replace the `getKannadaFont()` method with:

```kotlin
private fun getKannadaFont(): PdfFont? {
    return try {
        // First try to load from assets (bundled with app)
        val assetManager = context.assets
        val fontStream = assetManager.open("fonts/NotoSansKannada-Regular.ttf")
        val fontBytes = fontStream.readBytes()
        fontStream.close()
        
        PdfFontFactory.createFont(
            fontBytes, 
            PdfEncodings.IDENTITY_H, 
            PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
        )
    } catch (e: Exception) {
        try {
            // Fallback to system font
            PdfFontFactory.createFont(
                "/system/fonts/NotoSansKannada-Regular.ttf", 
                PdfEncodings.IDENTITY_H, 
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            )
        } catch (e2: Exception) {
            try {
                // Last fallback
                PdfFontFactory.createFont(
                    "/system/fonts/DroidSansFallback.ttf", 
                    PdfEncodings.IDENTITY_H, 
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
                )
            } catch (e3: Exception) {
                null
            }
        }
    }
}
```

## Why This Works
1. **Guaranteed availability** - Font is bundled with the app
2. **No dependency on system fonts** - Works on all Android devices
3. **Fallback mechanism** - Still tries system fonts if assets fail
4. **Force embedding** - Ensures font is embedded in PDF

## Alternative: Use Google Fonts
If you don't want to bundle the font, you can use the Compose approach with downloadable fonts, but for PDFs, bundling is more reliable.

## File Size Impact
- NotoSansKannada-Regular.ttf: ~200-300 KB
- Minimal impact on APK size

## Testing
After implementing:
1. Generate a receipt with Kannada text
2. Open the PDF
3. Verify all Kannada characters are visible
4. Test on different Android versions (8, 9, 10, 11, 12, 13, 14)
