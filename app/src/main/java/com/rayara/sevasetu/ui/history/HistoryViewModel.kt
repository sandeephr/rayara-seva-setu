package com.rayara.sevasetu.ui.history

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rayara.sevasetu.data.database.AppDatabase
import com.rayara.sevasetu.data.database.entities.Receipt
import com.rayara.sevasetu.data.repository.ReceiptRepository
import com.rayara.sevasetu.sync.FirestoreSync
import com.rayara.sevasetu.utils.PDFExporter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryUiState(
    val receipts: List<Receipt> = emptyList(),
    val searchQuery: String = "",
    val todayTotal: Double = 0.0,
    val todayCount: Int = 0,
    val isLoading: Boolean = false
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = ReceiptRepository(database.receiptDao())
    private val firestoreSync = FirestoreSync(application)
    
    init {
        // Don't auto-sync on init to prevent pulling back deleted receipts
        // Sync will happen when user explicitly creates/edits receipts
        Log.d("HistoryViewModel", "HistoryViewModel initialized (auto-sync disabled)")
    }
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val uiState: StateFlow<HistoryUiState> = combine(
        _searchQuery,
        repository.getAllReceipts()
    ) { query, allReceipts ->
        val filteredReceipts = if (query.isBlank()) {
            allReceipts
        } else {
            allReceipts.filter { receipt ->
                receipt.receiptNumber.contains(query, ignoreCase = true) ||
                receipt.customerName.contains(query, ignoreCase = true) ||
                receipt.customerPhone.contains(query, ignoreCase = true)
            }
        }
        
        val todayTotal = repository.getTodayTotal()
        val todayCount = filteredReceipts.size
        
        HistoryUiState(
            receipts = filteredReceipts,
            searchQuery = query,
            todayTotal = todayTotal,
            todayCount = todayCount,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HistoryUiState(isLoading = true)
    )
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun deleteReceipt(receipt: Receipt) {
        viewModelScope.launch {
            // Delete from local database (soft delete)
            repository.deleteReceipt(receipt)
            
            // Also delete from Firestore to prevent ghost transactions
            try {
                firestoreSync.deleteReceiptFromFirestore(receipt.id)
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Failed to delete from Firestore", e)
                // Continue anyway - local delete succeeded
            }
        }
    }
    
    fun exportTransactions(context: Context, startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                Log.d("HistoryViewModel", "Starting export: $startDate to $endDate")
                
                val receipts = repository.getReceiptsByDateRange(startDate, endDate)
                Log.d("HistoryViewModel", "Found ${receipts.size} receipts")
                
                if (receipts.isEmpty()) {
                    Toast.makeText(
                        context,
                        "ಈ ಅವಧಿಯಲ್ಲಿ ಯಾವುದೇ ವಹಿವಾಟುಗಳಿಲ್ಲ",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }
                
                val pdfExporter = PDFExporter(context)
                val pdfFile = pdfExporter.exportTransactions(receipts, startDate, endDate)
                Log.d("HistoryViewModel", "PDF created: ${pdfFile.absolutePath}")
                
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    pdfFile
                )
                
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                
                Toast.makeText(
                    context,
                    "${receipts.size} ವಹಿವಾಟುಗಳನ್ನು ರಫ್ತು ಮಾಡಲಾಗಿದೆ",
                    Toast.LENGTH_SHORT
                ).show()
                
                context.startActivity(Intent.createChooser(intent, "ವಹಿವಾಟು ವರದಿ ತೆರೆಯಿರಿ"))
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Export failed", e)
                Toast.makeText(
                    context,
                    "ರಫ್ತು ಮಾಡುವಲ್ಲಿ ತಪ್ಪಾಯಿತು: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
