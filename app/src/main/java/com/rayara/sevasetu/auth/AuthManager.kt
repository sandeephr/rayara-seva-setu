package com.rayara.sevasetu.auth

import android.content.Context
import android.provider.Settings
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.rayara.sevasetu.data.database.AppDatabase
import com.rayara.sevasetu.data.database.entities.User
import kotlinx.coroutines.tasks.await
import java.util.UUID
import java.util.concurrent.TimeUnit

class AuthManager(private val context: Context) {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    
    private val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    
    // Check if user is logged in
    suspend fun isUserLoggedIn(): Boolean {
        return userDao.getCurrentUser() != null
    }
    
    // Get current user
    suspend fun getCurrentUser(): User? {
        return userDao.getCurrentUser()
    }
    
    // Check if username is available
    suspend fun isUsernameAvailable(name: String): Boolean {
        // Check local database
        val localUser = userDao.getUserByName(name)
        if (localUser != null) return false
        
        // Check Firestore
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("name", name)
                .get()
                .await()
            snapshot.isEmpty
        } catch (e: Exception) {
            true // If offline, allow (will be checked again when online)
        }
    }
    
    // Check if mobile is already registered
    suspend fun isMobileRegistered(mobile: String): Boolean {
        // Check local database
        val localUser = userDao.getUserByMobile(mobile)
        if (localUser != null) return true
        
        // Check Firestore
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("mobileNumber", mobile)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
    
    // Validate credentials (password + whitelist check)
    fun validateCredentials(
        phoneNumber: String,
        password: String
    ): ValidationResult {
        // Check password
        if (!AuthConfig.isPasswordValid(password)) {
            return ValidationResult.InvalidPassword
        }
        
        // Check if phone is in whitelist
        if (!AuthConfig.isPhoneAllowed(phoneNumber)) {
            return ValidationResult.PhoneNotAllowed
        }
        
        return ValidationResult.Valid
    }
    
    // Send OTP (kept for backward compatibility, but now uses test phone auth)
    fun sendOTP(
        phoneNumber: String,
        onCodeSent: (verificationId: String) -> Unit,
        onVerificationFailed: (Exception) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification (rare)
                }
                
                override fun onVerificationFailed(e: FirebaseException) {
                    onVerificationFailed(e)
                }
                
                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    onCodeSent(verificationId)
                }
            })
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    
    // Direct login with password (new hybrid method)
    suspend fun loginWithPassword(
        name: String,
        phoneNumber: String,
        password: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            // Validate credentials
            val validation = validateCredentials(phoneNumber, password)
            if (validation != ValidationResult.Valid) {
                throw Exception(validation.message)
            }
            
            // Use Firebase test phone authentication
            val credential = PhoneAuthProvider.getCredential(
                phoneNumber,
                AuthConfig.DEFAULT_PASSWORD
            )
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Firebase authentication failed")
            
            // Check if user exists (login) or new user (register)
            val existingUser = userDao.getUserByMobile(phoneNumber)
            
            if (existingUser != null) {
                // Existing user - login
                checkAndForceLogout(existingUser.userId)
                
                val updatedUser = existingUser.copy(
                    currentDeviceId = deviceId,
                    loginToken = UUID.randomUUID().toString(),
                    lastLoginAt = System.currentTimeMillis()
                )
                
                userDao.insertUser(updatedUser)
                
                firestore.collection("users")
                    .document(existingUser.userId)
                    .set(updatedUser)
                    .await()
                
                createSession(updatedUser.userId)
                onSuccess(updatedUser)
            } else {
                // New user - register
                val userId = UUID.randomUUID().toString()
                val newUser = User(
                    userId = userId,
                    name = name,
                    mobileNumber = phoneNumber,
                    isVerified = true,
                    firebaseUid = firebaseUser.uid,
                    currentDeviceId = deviceId,
                    loginToken = UUID.randomUUID().toString(),
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis()
                )
                
                userDao.insertUser(newUser)
                
                firestore.collection("users")
                    .document(userId)
                    .set(newUser)
                    .await()
                
                createSession(userId)
                onSuccess(newUser)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
    
    // Verify OTP and register user
    suspend fun verifyOTPAndRegister(
        verificationId: String,
        code: String,
        name: String,
        mobile: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Firebase user is null")
            
            // Create user object
            val userId = UUID.randomUUID().toString()
            val user = User(
                userId = userId,
                name = name,
                mobileNumber = mobile,
                isVerified = true,
                firebaseUid = firebaseUser.uid,
                currentDeviceId = deviceId,
                loginToken = UUID.randomUUID().toString()
            )
            
            // Save to local database
            userDao.insertUser(user)
            
            // Save to Firestore
            firestore.collection("users")
                .document(userId)
                .set(user)
                .await()
            
            // Create session
            createSession(userId)
            
            onSuccess(user)
        } catch (e: Exception) {
            onFailure(e)
        }
    }
    
    // Login existing user
    suspend fun loginUser(
        verificationId: String,
        code: String,
        mobile: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Firebase user is null")
            
            // Get user from Firestore
            val snapshot = firestore.collection("users")
                .whereEqualTo("mobileNumber", mobile)
                .get()
                .await()
            
            if (snapshot.isEmpty) {
                throw Exception("User not found")
            }
            
            val userData = snapshot.documents[0].toObject(User::class.java)
                ?: throw Exception("Failed to parse user data")
            
            // Check if logged in on another device
            checkAndForceLogout(userData.userId)
            
            // Update device info
            val updatedUser = userData.copy(
                currentDeviceId = deviceId,
                loginToken = UUID.randomUUID().toString(),
                lastLoginAt = System.currentTimeMillis()
            )
            
            // Save to local database
            userDao.insertUser(updatedUser)
            
            // Update Firestore
            firestore.collection("users")
                .document(userData.userId)
                .set(updatedUser)
                .await()
            
            // Create session
            createSession(updatedUser.userId)
            
            onSuccess(updatedUser)
        } catch (e: Exception) {
            onFailure(e)
        }
    }
    
    // Create session in Firestore
    private suspend fun createSession(userId: String) {
        val session = mapOf(
            "userId" to userId,
            "deviceId" to deviceId,
            "loginTime" to System.currentTimeMillis(),
            "forceLogout" to false
        )
        
        firestore.collection("user_sessions")
            .document(userId)
            .set(session)
            .await()
    }
    
    // Check and force logout from other devices
    private suspend fun checkAndForceLogout(userId: String) {
        try {
            val sessionDoc = firestore.collection("user_sessions")
                .document(userId)
                .get()
                .await()
            
            if (sessionDoc.exists()) {
                val currentDeviceId = sessionDoc.getString("deviceId")
                if (currentDeviceId != null && currentDeviceId != deviceId) {
                    // Force logout from old device
                    firestore.collection("user_sessions")
                        .document(userId)
                        .update("forceLogout", true)
                        .await()
                    
                    // Wait 2 seconds for old device to logout
                    kotlinx.coroutines.delay(2000)
                }
            }
        } catch (e: Exception) {
            // Ignore errors
        }
    }
    
    // Check if forced to logout
    suspend fun checkForceLogout(): Boolean {
        val user = getCurrentUser() ?: return false
        
        return try {
            val sessionDoc = firestore.collection("user_sessions")
                .document(user.userId)
                .get()
                .await()
            
            sessionDoc.getBoolean("forceLogout") ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    // Logout
    suspend fun logout() {
        val user = getCurrentUser()
        if (user != null) {
            // Remove session from Firestore
            try {
                firestore.collection("user_sessions")
                    .document(user.userId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                // Ignore
            }
        }
        
        // Clear local database
        userDao.deleteAllUsers()
        
        // Sign out from Firebase
        auth.signOut()
    }
}

/**
 * Result of credential validation
 */
sealed class ValidationResult(val message: String) {
    object Valid : ValidationResult("Valid credentials")
    object InvalidPassword : ValidationResult("Invalid password")
    object PhoneNotAllowed : ValidationResult("This phone number is not authorized to use the app")
}
