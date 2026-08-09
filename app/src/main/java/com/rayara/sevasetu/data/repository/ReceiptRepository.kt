package com.rayara.sevasetu.data.repository

import com.rayara.sevasetu.data.database.dao.ReceiptDao
import com.rayara.sevasetu.data.database.entities.Receipt
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class ReceiptRepository(private val receiptDao: ReceiptDao) {
    
    fun getAllReceipts(): Flow<List<Receipt>> {
        return receiptDao.getAllReceipts()
    }
    
    suspend fun getReceiptById(id: Long): Receipt? {
        return receiptDao.getReceiptById(id)
    }
    
    suspend fun getReceiptByNumber(receiptNumber: String): Receipt? {
        return receiptDao.getReceiptByNumber(receiptNumber)
    }
    
    fun searchReceipts(query: String): Flow<List<Receipt>> {
        return receiptDao.searchReceipts(query)
    }
    
    fun getReceiptsByDate(date: String): Flow<List<Receipt>> {
        return receiptDao.getReceiptsByDate(date)
    }
    
    fun getTodayReceipts(): Flow<List<Receipt>> {
        val today = getCurrentDate()
        return receiptDao.getReceiptsByDate(today)
    }
    
    suspend fun getTotalAmountByDate(date: String): Double {
        return receiptDao.getTotalAmountByDate(date) ?: 0.0
    }
    
    suspend fun getTodayTotal(): Double {
        val today = getCurrentDate()
        return getTotalAmountByDate(today)
    }
    
    suspend fun getReceiptCountByDate(date: String): Int {
        return receiptDao.getReceiptCountByDate(date)
    }
    
    suspend fun insertReceipt(receipt: Receipt): Long {
        return receiptDao.insertReceipt(receipt)
    }
    
    suspend fun updateReceipt(receipt: Receipt) {
        receiptDao.updateReceipt(receipt)
    }
    
    suspend fun deleteReceipt(receipt: Receipt) {
        receiptDao.softDeleteReceipt(receipt.id)
    }
    
    suspend fun generateNextReceiptNumber(): String {
        val lastNumber = receiptDao.getLastReceiptNumber() ?: 28410
        return (lastNumber + 1).toString()
    }
    
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(Date())
    }
}
