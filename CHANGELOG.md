# Changelog - Rayara Seva Setu

## Version 1.0.1 (August 9, 2026)

### ✨ New Feature: Optional Customer Details for Small Amounts

**Customer Request:** Make customer name and phone number optional for amounts ≤ ₹500

#### Changes Made:

1. **Validation Logic Updated** (`BillingViewModel.kt`)
   - Customer details now **optional** for amounts ≤ ₹500
   - Customer details **mandatory** for amounts > ₹500
   - New validation messages:
     - "₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿನ ಮೊತ್ತಕ್ಕೆ ಹೆಸರು ಅಗತ್ಯವಿದೆ" (Name required for amounts > ₹500)
     - "₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿನ ಮೊತ್ತಕ್ಕೆ ದೂರವಾಣಿ ಸಂಖ್ಯೆ ಅಗತ್ಯವಿದೆ" (Phone required for amounts > ₹500)

2. **Default Values** (`Constants.kt`)
   - When customer details are empty for amounts ≤ ₹500:
     - **Default Name:** "ಶ್ರೀ ರಾಯರ ಸೇವಾರ್ಥಿ"
     - **Default Phone:** "-"
   - These defaults are automatically used in receipt generation

3. **UI Updates** (`BillingScreen.kt`)
   - Added hint text: "(₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿಗೆ ಅಗತ್ಯ)" (Required for amounts > ₹500)
   - Field labels updated to show "(ಐಚ್ಛಿಕ)" (Optional)
   - Visual indication that fields are not always mandatory

#### How It Works:

**Scenario 1: Amount ≤ ₹500 (e.g., ₹100, ₹200, ₹500)**
- Customer name and phone are **optional**
- If left blank:
  - Receipt will show: "ಶ್ರೀ ರಾಯರ ಸೇವಾರ್ಥಿ"
  - Phone will show: "-"
- If filled:
  - Receipt will show the entered details

**Scenario 2: Amount > ₹500 (e.g., ₹1000, ₹2000, ₹2500)**
- Customer name and phone are **mandatory**
- App will show error if not filled:
  - "₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿನ ಮೊತ್ತಕ್ಕೆ ಹೆಸರು ಅಗತ್ಯವಿದೆ"
  - "₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿನ ಮೊತ್ತಕ್ಕೆ ದೂರವಾಣಿ ಸಂಖ್ಯೆ ಅಗತ್ಯವಿದೆ"

#### Example Receipts:

**Example 1: ₹200 without customer details**
```
┌─────────────────────────────────────┐
│  ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ      │
│    ಭ್ರಾಹ್ಮಣರ ಬೀದಿ, ದೊಡ್ಡಬಳ್ಳಾಪುರ     │
│                                     │
│ ಸಂ. 28411        ದಿನಾಂಕ: 09/08/2026 │
│                                     │
│ ಶ್ರೀಮತಿ/ಶ್ರೀ: ಶ್ರೀ ರಾಯರ ಸೇವಾರ್ಥಿ      │
│ ದೂರವಾಣಿ: -                         │
│                                     │
│ ─────────────────────────────────── │
│ ಸೇವೆ                      ₹200     │
│ ─────────────────────────────────── │
│ ಒಟ್ಟು:                    ₹200     │
│ ಪಾವತಿ ವಿಧಾನ: ನಗದು                │
│ ─────────────────────────────────── │
│                                     │
│ ಶ್ರೀ ರಾಘವೇಂದ್ರ ಗುರುಸಾರ್ವಭೌಮ         │
│        ಸೇವಾ ಟ್ರಸ್ಟ್ (ರಿ.)            │
│                                     │
│        ಧನ್ಯವಾದಗಳು                   │
└─────────────────────────────────────┘
```

**Example 2: ₹1000 with customer details (mandatory)**
```
┌─────────────────────────────────────┐
│  ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ      │
│    ಭ್ರಾಹ್ಮಣರ ಬೀದಿ, ದೊಡ್ಡಬಳ್ಳಾಪುರ     │
│                                     │
│ ಸಂ. 28412        ದಿನಾಂಕ: 09/08/2026 │
│                                     │
│ ಶ್ರೀಮತಿ/ಶ್ರೀ: ರಾಮೇಶ್ ಕುಮಾರ್            │
│ ದೂರವಾಣಿ: 9876543210                │
│                                     │
│ ─────────────────────────────────── │
│ ಸೇವೆ                     ₹1000     │
│ ─────────────────────────────────── │
│ ಒಟ್ಟು:                   ₹1000     │
│ ಪಾವತಿ ವಿಧಾನ: PhonePe              │
│ ─────────────────────────────────── │
│                                     │
│ ಶ್ರೀ ರಾಘವೇಂದ್ರ ಗುರುಸಾರ್ವಭೌಮ         │
│        ಸೇವಾ ಟ್ರಸ್ಟ್ (ರಿ.)            │
│                                     │
│        ಧನ್ಯವಾದಗಳು                   │
└─────────────────────────────────────┘
```

#### Files Modified:

1. **Constants.kt**
   - Added `MANDATORY_DETAILS_THRESHOLD = 500`
   - Added `DefaultValues` object with default name and phone

2. **BillingViewModel.kt**
   - Updated `validateInput()` to check amount threshold
   - Updated `generateReceipt()` to use default values when appropriate
   - New validation messages for amounts > ₹500

3. **BillingScreen.kt**
   - Added hint text showing fields are optional for small amounts
   - Updated field labels to show "(ಐಚ್ಛಿಕ)"

4. **strings.xml**
   - Added new strings for optional field hints

#### Benefits:

✅ **Faster billing** for small amounts (no need to enter details)  
✅ **Proper tracking** for larger amounts (customer details captured)  
✅ **Clear UI** showing when details are required  
✅ **Professional receipts** with default values for anonymous donations  

#### Testing:

**Test Case 1: Small Amount (₹100)**
1. Select ₹100
2. Leave name and phone blank
3. Select payment mode
4. Generate receipt
5. ✅ Receipt shows "ಶ್ರೀ ರಾಯರ ಸೇವಾರ್ಥಿ" and "-"

**Test Case 2: Large Amount (₹1000)**
1. Select ₹1000
2. Leave name and phone blank
3. Try to generate receipt
4. ✅ Error: "₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿನ ಮೊತ್ತಕ್ಕೆ ಹೆಸರು ಅಗತ್ಯವಿದೆ"

**Test Case 3: Threshold Amount (₹500)**
1. Select ₹500
2. Leave name and phone blank
3. Generate receipt
4. ✅ Receipt shows default values (₹500 is included in optional range)

---

## Version 1.0.0 (August 9, 2026)

### Initial Release

#### Features:
- Fast billing with predefined amounts
- PDF receipt generation
- Receipt history and search
- Kannada language support
- Offline mode
- Payment mode tracking (Cash/PhonePe/Online)

#### Tech Stack:
- Android (Kotlin)
- Jetpack Compose
- Room Database
- iText PDF
- MVVM Architecture

---

**App Name:** Rayara Seva Setu (ರಾಯರ ಸೇವಾ ಸೇತು)  
**Organization:** ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ  
**Current Version:** 1.0.1
