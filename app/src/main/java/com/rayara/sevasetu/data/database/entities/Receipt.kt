package com.rayara.sevasetu.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rayara.sevasetu.data.models.PaymentMode

@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val receiptNumber: String,
    
    val customerName: String,
    
    val customerPhone: String,
    
    val amount: Double,
    
    val serviceDescription: String,
    
    val paymentMode: String,
    
    val timestamp: Long = System.currentTimeMillis(),
    
    val date: String,
    
    val time: String,
    
    val pdfPath: String? = null,
    
    val isDeleted: Boolean = false,
    
    // User tracking
    val createdByUserId: String = "",
    
    val createdByUserName: String = "",
    
    val createdByMobile: String = "",
    
    // Sync tracking
    val syncedToServer: Boolean = false,
    
    val isOfflineEntry: Boolean = false,
    
    val deviceId: String = ""
) {
    fun getPaymentModeEnum(): PaymentMode {
        return PaymentMode.fromString(paymentMode)
    }
    
    fun getFormattedAmount(): String {
        return "₹${amount.toInt()}"
    }
}
