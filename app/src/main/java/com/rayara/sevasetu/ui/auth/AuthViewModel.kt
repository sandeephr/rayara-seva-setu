package com.rayara.sevasetu.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rayara.sevasetu.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val name: String = "",
    val mobileNumber: String = "",
    val password: String = "",
    val otpCode: String = "",
    val showOTPField: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val verificationId: String? = null,
    val isNewUser: Boolean = false
)

class AuthViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private lateinit var authManager: AuthManager
    
    fun initialize(context: Context) {
        authManager = AuthManager(context)
        checkIfLoggedIn()
    }
    
    private fun checkIfLoggedIn() {
        viewModelScope.launch {
            val isLoggedIn = authManager.isUserLoggedIn()
            _uiState.value = _uiState.value.copy(isAuthenticated = isLoggedIn)
        }
    }
    
    fun resetAuthState() {
        _uiState.value = AuthUiState()
    }
    
    fun recheckAuth() {
        if (::authManager.isInitialized) {
            checkIfLoggedIn()
        }
    }
    
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }
    
    fun updateMobileNumber(number: String) {
        if (number.length <= 10 && number.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(mobileNumber = number, errorMessage = null)
        }
    }
    
    fun updatePassword(password: String) {
        // Only allow 6 digits
        if (password.length <= 6 && password.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
        }
    }
    
    fun updateOTPCode(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(otpCode = code, errorMessage = null)
        }
    }
    
    fun sendOTP(context: Context) {
        if (!::authManager.isInitialized) {
            authManager = AuthManager(context)
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val name = _uiState.value.name.trim()
            val mobile = _uiState.value.mobileNumber
            
            // Validate name
            if (name.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "ದಯವಿಟ್ಟು ಹೆಸರು ನಮೂದಿಸಿ (Please enter name)"
                )
                return@launch
            }
            
            // Check if mobile is already registered
            val isMobileRegistered = authManager.isMobileRegistered(mobile)
            
            if (isMobileRegistered) {
                // Existing user - just login
                _uiState.value = _uiState.value.copy(isNewUser = false)
            } else {
                // New user - check username availability
                val isUsernameAvailable = authManager.isUsernameAvailable(name)
                if (!isUsernameAvailable) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "ಈ ಹೆಸರು ಈಗಾಗಲೇ ಬಳಸಲಾಗಿದೆ. ದಯವಿಟ್ಟು ಬೇರೆ ಹೆಸರು ಆಯ್ಕೆಮಾಡಿ\n(This name is already taken. Please choose another name)"
                    )
                    return@launch
                }
                _uiState.value = _uiState.value.copy(isNewUser = true)
            }
            
            // Send OTP
            val phoneNumber = "+91$mobile"
            authManager.sendOTP(
                phoneNumber = phoneNumber,
                onCodeSent = { verificationId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showOTPField = true,
                        verificationId = verificationId
                    )
                },
                onVerificationFailed = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "OTP ಕಳುಹಿಸುವಲ್ಲಿ ತಪ್ಪಾಯಿತು: ${exception.message}\n(Failed to send OTP)"
                    )
                }
            )
        }
    }
    
    fun verifyOTP(context: Context) {
        if (!::authManager.isInitialized) {
            authManager = AuthManager(context)
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val verificationId = _uiState.value.verificationId
            if (verificationId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "ದೋಷ: Verification ID ಕಾಣೆಯಾಗಿದೆ (Error: Verification ID missing)"
                )
                return@launch
            }
            
            val code = _uiState.value.otpCode
            val name = _uiState.value.name.trim()
            val mobile = _uiState.value.mobileNumber
            
            if (_uiState.value.isNewUser) {
                // Register new user
                authManager.verifyOTPAndRegister(
                    verificationId = verificationId,
                    code = code,
                    name = name,
                    mobile = mobile,
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isAuthenticated = true
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "ಪರಿಶೀಲನೆ ವಿಫಲವಾಗಿದೆ: ${exception.message}\n(Verification failed)"
                        )
                    }
                )
            } else {
                // Login existing user
                authManager.loginUser(
                    verificationId = verificationId,
                    code = code,
                    mobile = mobile,
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isAuthenticated = true
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "ಲಾಗಿನ್ ವಿಫಲವಾಗಿದೆ: ${exception.message}\n(Login failed)"
                        )
                    }
                )
            }
        }
    }
    
    fun resetOTP() {
        _uiState.value = _uiState.value.copy(
            showOTPField = false,
            otpCode = "",
            verificationId = null,
            errorMessage = null
        )
    }
    
    // New hybrid password-based login
    fun loginWithPassword(context: Context) {
        if (!::authManager.isInitialized) {
            authManager = AuthManager(context)
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val name = _uiState.value.name.trim()
            val mobile = _uiState.value.mobileNumber
            val password = _uiState.value.password
            val phoneNumber = "+91$mobile"
            
            // Validate inputs
            if (name.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "ದಯವಿಟ್ಟು ಹೆಸರು ನಮೂದಿಸಿ (Please enter name)"
                )
                return@launch
            }
            
            if (mobile.length != 10) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "ದಯವಿಟ್ಟು ಮಾನ್ಯವಾದ ಮೊಬೈಲ್ ಸಂಖ್ಯೆ ನಮೂದಿಸಿ (Please enter valid mobile number)"
                )
                return@launch
            }
            
            if (password.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "ದಯವಿಟ್ಟು ಪಾಸ್‌ವರ್ಡ್ ನಮೂದಿಸಿ (Please enter password)"
                )
                return@launch
            }
            
            if (password.length != 6) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "ಪಾಸ್‌ವರ್ಡ್ 6 ಅಂಕಿಗಳಾಗಿರಬೇಕು (Password must be 6 digits)"
                )
                return@launch
            }
            
            // Check if mobile is already registered (for new users, validate username)
            val isMobileRegistered = authManager.isMobileRegistered(phoneNumber)
            
            if (!isMobileRegistered) {
                // New user - check username availability
                val isUsernameAvailable = authManager.isUsernameAvailable(name)
                if (!isUsernameAvailable) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "ಈ ಹೆಸರು ಈಗಾಗಲೇ ಬಳಸಲಾಗಿದೆ. ದಯವಿಟ್ಟು ಬೇರೆ ಹೆಸರು ಆಯ್ಕೆಮಾಡಿ\n(This name is already taken. Please choose another name)"
                    )
                    return@launch
                }
            }
            
            // Use hybrid authentication
            authManager.loginWithPassword(
                name = name,
                phoneNumber = phoneNumber,
                password = password,
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "ಲಾಗಿನ್ ವಿಫಲವಾಗಿದೆ: ${exception.message}\n(Login failed)"
                    )
                }
            )
        }
    }
}
