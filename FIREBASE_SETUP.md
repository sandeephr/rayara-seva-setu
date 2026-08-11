# Firebase Setup Instructions

## Steps to Configure Firebase:

1. **Go to Firebase Console**: https://console.firebase.google.com/
2. **Create a new project** or select existing project
3. **Add Android app**:
   - Package name: `com.rayara.sevasetu`
   - App nickname: Rayara Seva Setu
   - Download `google-services.json`
4. **Place the file**: Copy `google-services.json` to `app/` directory
5. **Enable Authentication**:
   - Go to Authentication → Sign-in method
   - Enable "Phone" authentication
   - Add test phone numbers if needed (for testing without SMS)
6. **Enable Firestore**:
   - Go to Firestore Database
   - Create database in production mode
   - Start collection: `users`
   - Start collection: `receipts`
   - Start collection: `user_sessions`

## Security Rules for Firestore:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Receipts collection - all authenticated users can read/write
    match /receipts/{receiptId} {
      allow read, write: if request.auth != null;
    }
    
    // User sessions - only owner can read/write
    match /user_sessions/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Test Phone Numbers (Optional):

Add these in Firebase Console → Authentication → Sign-in method → Phone → Test phone numbers:

- Phone: +91 9999999999, Code: 123456
- Phone: +91 8888888888, Code: 123456

This allows testing without actual SMS.

## Free Tier Limits:

- Phone Auth: 10,000 verifications/month
- Firestore: 1GB storage + 50K reads/day + 20K writes/day
- **Cost: ₹0** for typical usage

## After Setup:

Run the app and it will automatically connect to Firebase!
