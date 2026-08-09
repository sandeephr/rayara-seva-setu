package com.rayara.sevasetu.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rayara.sevasetu.data.database.AppDatabase
import com.rayara.sevasetu.data.database.entities.Receipt
import com.rayara.sevasetu.data.repository.ReceiptRepository
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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun deleteReceipt(receipt: Receipt) {
        viewModelScope.launch {
            repository.deleteReceipt(receipt)
        }
    }
}
