package com.rayara.sevasetu.data.database.dao

import androidx.room.*
import com.rayara.sevasetu.data.database.entities.Receipt
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt): Long
    
    @Update
    suspend fun updateReceipt(receipt: Receipt)
    
    @Delete
    suspend fun deleteReceipt(receipt: Receipt)
    
    @Query("SELECT * FROM receipts WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllReceipts(): Flow<List<Receipt>>
    
    @Query("SELECT * FROM receipts WHERE id = :id AND isDeleted = 0")
    suspend fun getReceiptById(id: Long): Receipt?
    
    @Query("SELECT * FROM receipts WHERE receiptNumber = :receiptNumber AND isDeleted = 0")
    suspend fun getReceiptByNumber(receiptNumber: String): Receipt?
    
    @Query("""
        SELECT * FROM receipts 
        WHERE isDeleted = 0 
        AND (receiptNumber LIKE '%' || :query || '%' 
             OR customerName LIKE '%' || :query || '%'
             OR customerPhone LIKE '%' || :query || '%')
        ORDER BY timestamp DESC
    """)
    fun searchReceipts(query: String): Flow<List<Receipt>>
    
    @Query("""
        SELECT * FROM receipts 
        WHERE isDeleted = 0 
        AND date = :date
        ORDER BY timestamp DESC
    """)
    fun getReceiptsByDate(date: String): Flow<List<Receipt>>
    
    @Query("""
        SELECT SUM(amount) FROM receipts 
        WHERE isDeleted = 0 
        AND date = :date
    """)
    suspend fun getTotalAmountByDate(date: String): Double?
    
    @Query("""
        SELECT COUNT(*) FROM receipts 
        WHERE isDeleted = 0 
        AND date = :date
    """)
    suspend fun getReceiptCountByDate(date: String): Int
    
    @Query("SELECT MAX(CAST(receiptNumber AS INTEGER)) FROM receipts")
    suspend fun getLastReceiptNumber(): Int?
    
    @Query("UPDATE receipts SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteReceipt(id: Long)
    
    @Query("DELETE FROM receipts WHERE isDeleted = 1")
    suspend fun permanentlyDeleteSoftDeleted()
    
    @Query("SELECT * FROM receipts WHERE isDeleted = 0 ORDER BY timestamp DESC")
    suspend fun getAllReceiptsList(): List<Receipt>
    
    @Query("""
        SELECT * FROM receipts 
        WHERE isDeleted = 0 
        AND (
            substr(date, 7, 4) || substr(date, 4, 2) || substr(date, 1, 2) 
            BETWEEN 
            substr(:startDate, 7, 4) || substr(:startDate, 4, 2) || substr(:startDate, 1, 2)
            AND 
            substr(:endDate, 7, 4) || substr(:endDate, 4, 2) || substr(:endDate, 1, 2)
        )
        ORDER BY timestamp DESC
    """)
    suspend fun getReceiptsByDateRange(startDate: String, endDate: String): List<Receipt>
}
