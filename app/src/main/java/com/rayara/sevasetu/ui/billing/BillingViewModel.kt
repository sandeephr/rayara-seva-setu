package com.rayara.sevasetu.ui.billing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rayara.sevasetu.data.database.AppDatabase
import com.rayara.sevasetu.data.database.entities.Receipt
import com.rayara.sevasetu.data.models.PaymentMode
import com.rayara.sevasetu.data.repository.ReceiptRepository
import com.rayara.sevasetu.utils.Constants
import com.rayara.sevasetu.utils.PDFGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class BillingUiState(
    val customerName: String = "",
    val customerPhone: String = "",
    val selectedAmount: Int? = null,
    val customAmount: String = "",
    val paymentMode: PaymentMode? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val generatedPdfFile: File? = null,
    val showPreviewDialog: Boolean = false,
    val previewReceipt: Receipt? = null
)

class BillingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = ReceiptRepository(database.receiptDao())
    private val pdfGenerator = PDFGenerator(application)
    
    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()
    
    fun updateCustomerName(name: String) {
        _uiState.value = _uiState.value.copy(customerName = name, errorMessage = null)
    }
    
    fun updateCustomerPhone(phone: String) {
        if (phone.length <= Constants.Validation.MAX_PHONE_LENGTH) {
            _uiState.value = _uiState.value.copy(customerPhone = phone, errorMessage = null)
        }
    }
    
    fun selectAmount(amount: Int) {
        _uiState.value = _uiState.value.copy(
            selectedAmount = amount,
            customAmount = "",
            errorMessage = null
        )
    }
    
    fun updateCustomAmount(amount: String) {
        if (amount.isEmpty() || amount.toIntOrNull() != null) {
            _uiState.value = _uiState.value.copy(
                customAmount = amount,
                selectedAmount = null,
                errorMessage = null
            )
        }
    }
    
    fun selectPaymentMode(mode: PaymentMode) {
        _uiState.value = _uiState.value.copy(paymentMode = mode, errorMessage = null)
    }
    
    fun generateReceipt() {
        viewModelScope.launch {
            val state = _uiState.value
            
            val validationError = validateInput(state)
            if (validationError != null) {
                _uiState.value = state.copy(errorMessage = validationError)
                return@launch
            }
            
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            
            try {
                val receiptNumber = repository.generateNextReceiptNumber()
                val amount = (state.selectedAmount ?: state.customAmount.toInt()).toDouble()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                val currentTime = timeFormat.format(Date())
                
                val finalCustomerName = if (amount <= Constants.Validation.MANDATORY_DETAILS_THRESHOLD && state.customerName.trim().isEmpty()) {
                    Constants.DefaultValues.DEFAULT_CUSTOMER_NAME
                } else {
                    state.customerName.trim()
                }
                
                val finalCustomerPhone = if (amount <= Constants.Validation.MANDATORY_DETAILS_THRESHOLD && state.customerPhone.trim().isEmpty()) {
                    Constants.DefaultValues.DEFAULT_PHONE_NUMBER
                } else {
                    state.customerPhone.trim()
                }
                
                val receipt = Receipt(
                    receiptNumber = receiptNumber,
                    customerName = finalCustomerName,
                    customerPhone = finalCustomerPhone,
                    amount = amount,
                    serviceDescription = "${Constants.Receipt.SERVICE_LABEL}",
                    paymentMode = state.paymentMode!!.name,
                    date = currentDate,
                    time = currentTime
                )
                
                val receiptId = repository.insertReceipt(receipt)
                val savedReceipt = repository.getReceiptById(receiptId)
                
                if (savedReceipt != null) {
                    val pdfFile = pdfGenerator.generateReceiptPDF(savedReceipt)
                    
                    val updatedReceipt = savedReceipt.copy(pdfPath = pdfFile.absolutePath)
                    repository.updateReceipt(updatedReceipt)
                    
                    // Clear form data but keep preview dialog open
                    _uiState.value = BillingUiState(
                        isLoading = false,
                        showPreviewDialog = true,
                        previewReceipt = updatedReceipt,
                        generatedPdfFile = pdfFile,
                        successMessage = "ರಶೀದಿ ಯಶಸ್ವಿಯಾಗಿ ರಚಿಸಲಾಗಿದೆ"
                    )
                } else {
                    _uiState.value = state.copy(
                        isLoading = false,
                        errorMessage = "ರಶೀದಿ ಉಳಿಸಲು ವಿಫಲವಾಗಿದೆ"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    errorMessage = "ದೋಷ: ${e.message}"
                )
            }
        }
    }
    
    private fun validateInput(state: BillingUiState): String? {
        val amount = state.selectedAmount ?: state.customAmount.toIntOrNull()
        if (amount == null || amount < Constants.Validation.MIN_AMOUNT) {
            return "ದಯವಿಟ್ಟು ಮೊತ್ತ ಆಯ್ಕೆಮಾಡಿ"
        }
        
        if (amount > Constants.Validation.MANDATORY_DETAILS_THRESHOLD) {
            if (state.customerName.trim().length < Constants.Validation.MIN_NAME_LENGTH) {
                return "₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿನ ಮೊತ್ತಕ್ಕೆ ಹೆಸರು ಅಗತ್ಯವಿದೆ"
            }
            
            if (state.customerPhone.trim().length != Constants.Validation.MIN_PHONE_LENGTH) {
                return "₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿನ ಮೊತ್ತಕ್ಕೆ ದೂರವಾಣಿ ಸಂಖ್ಯೆ ಅಗತ್ಯವಿದೆ"
            }
        }
        
        if (state.paymentMode == null) {
            return "ದಯವಿಟ್ಟು ಪಾವತಿ ವಿಧಾನ ಆಯ್ಕೆಮಾಡಿ"
        }
        
        return null
    }
    
    fun clearForm() {
        _uiState.value = BillingUiState()
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null,
            generatedPdfFile = null
        )
    }
    
    fun dismissPreview() {
        _uiState.value = _uiState.value.copy(
            showPreviewDialog = false,
            previewReceipt = null,
            generatedPdfFile = null
        )
    }
    
    fun closePreviewAndContinue() {
        _uiState.value = BillingUiState()
    }
}
