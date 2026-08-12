package com.rayara.sevasetu.auth

/**
 * Authentication configuration for the app
 * Contains whitelisted phone numbers and default password
 */
object AuthConfig {
    
    /**
     * Default password for all users
     * This is used with Firebase test phone authentication
     */
    const val DEFAULT_PASSWORD = "123456"
    
    /**
     * Whitelisted phone numbers that are allowed to use the app
     * These must match the test phone numbers configured in Firebase Console
     */
    val ALLOWED_PHONE_NUMBERS = listOf(
        "+91 99867 19566",
        "+91 89716 55687",
        "+91 87923 33734",
        "+91 90366 60982",
        "+91 92422 29325",
        "+91 96203 93230",
        "+91 70225 73492"
    )
    
    /**
     * Check if a phone number is in the whitelist
     */
    fun isPhoneAllowed(phoneNumber: String): Boolean {
        // Normalize phone number (remove spaces)
        val normalized = phoneNumber.replace(" ", "")
        return ALLOWED_PHONE_NUMBERS.any { it.replace(" ", "") == normalized }
    }
    
    /**
     * Validate password
     */
    fun isPasswordValid(password: String): Boolean {
        return password == DEFAULT_PASSWORD
    }
    
    /**
     * Admin credentials for Clear All access
     */
    private const val ADMIN_NAME = "Sandeep HR"
    private const val ADMIN_PHONE = "+91 89716 55687"
    
    /**
     * Check if user is admin (can access Clear All button)
     */
    fun isAdmin(name: String, phoneNumber: String): Boolean {
        val normalizedPhone = phoneNumber.replace(" ", "")
        val normalizedAdminPhone = ADMIN_PHONE.replace(" ", "")
        return name == ADMIN_NAME && normalizedPhone == normalizedAdminPhone
    }
}
