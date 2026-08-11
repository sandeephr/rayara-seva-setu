package com.rayara.sevasetu.sync

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rayara.sevasetu.data.database.AppDatabase
import com.rayara.sevasetu.data.database.entities.Receipt
import kotlinx.coroutines.tasks.await

class FirestoreSync(private val context: Context) {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val database = AppDatabase.getDatabase(context)
    private val receiptDao = database.receiptDao()
    
    companion object {
        private const val TAG = "FirestoreSync"
        private const val RECEIPTS_COLLECTION = "receipts"
    }
    
    // Sync local receipt to Firestore
    suspend fun syncReceiptToFirestore(receipt: Receipt): Boolean {
        return try {
            val receiptMap = mapOf(
                "id" to receipt.id,
                "receiptNumber" to receipt.receiptNumber,
                "customerName" to receipt.customerName,
                "customerPhone" to receipt.customerPhone,
                "amount" to receipt.amount,
                "serviceDescription" to receipt.serviceDescription,
                "paymentMode" to receipt.paymentMode,
                "timestamp" to receipt.timestamp,
                "date" to receipt.date,
                "time" to receipt.time,
                "createdByUserId" to receipt.createdByUserId,
                "createdByUserName" to receipt.createdByUserName,
                "createdByMobile" to receipt.createdByMobile,
                "isOfflineEntry" to receipt.isOfflineEntry,
                "deviceId" to receipt.deviceId
            )
            
            firestore.collection(RECEIPTS_COLLECTION)
                .document(receipt.id.toString())
                .set(receiptMap)
                .await()
            
            // Mark as synced in local database
            val updatedReceipt = receipt.copy(syncedToServer = true)
            receiptDao.updateReceipt(updatedReceipt)
            
            Log.d(TAG, "Receipt ${receipt.receiptNumber} synced to Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync receipt to Firestore", e)
            false
        }
    }
    
    // Fetch all receipts from Firestore and merge with local
    suspend fun syncReceiptsFromFirestore(): Int {
        return try {
            val snapshot = firestore.collection(RECEIPTS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            
            var syncedCount = 0
            
            for (document in snapshot.documents) {
                try {
                    val id = document.getLong("id") ?: continue
                    val receiptNumber = document.getString("receiptNumber") ?: continue
                    
                    // Check if receipt already exists locally
                    val existingReceipt = receiptDao.getReceiptById(id)
                    if (existingReceipt != null) {
                        continue // Skip if already exists
                    }
                    
                    // Create receipt from Firestore data
                    val receipt = Receipt(
                        id = id,
                        receiptNumber = receiptNumber,
                        customerName = document.getString("customerName") ?: "",
                        customerPhone = document.getString("customerPhone") ?: "",
                        amount = document.getDouble("amount") ?: 0.0,
                        serviceDescription = document.getString("serviceDescription") ?: "",
                        paymentMode = document.getString("paymentMode") ?: "CASH",
                        timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                        date = document.getString("date") ?: "",
                        time = document.getString("time") ?: "",
                        pdfPath = null, // PDF is device-specific
                        isDeleted = false,
                        createdByUserId = document.getString("createdByUserId") ?: "",
                        createdByUserName = document.getString("createdByUserName") ?: "",
                        createdByMobile = document.getString("createdByMobile") ?: "",
                        syncedToServer = true,
                        isOfflineEntry = document.getBoolean("isOfflineEntry") ?: false,
                        deviceId = document.getString("deviceId") ?: ""
                    )
                    
                    receiptDao.insertReceipt(receipt)
                    syncedCount++
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse receipt from Firestore", e)
                }
            }
            
            Log.d(TAG, "Synced $syncedCount receipts from Firestore")
            syncedCount
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch receipts from Firestore", e)
            0
        }
    }
    
    // Sync all unsynced local receipts to Firestore
    suspend fun syncUnsyncedReceipts(): Int {
        return try {
            val unsyncedReceipts = receiptDao.getUnsyncedReceipts()
            var syncedCount = 0
            
            for (receipt in unsyncedReceipts) {
                if (syncReceiptToFirestore(receipt)) {
                    syncedCount++
                }
            }
            
            Log.d(TAG, "Synced $syncedCount unsynced receipts to Firestore")
            syncedCount
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync unsynced receipts", e)
            0
        }
    }
    
    // Delete all receipts from Firestore
    suspend fun deleteAllReceiptsFromFirestore(): Boolean {
        return try {
            val snapshot = firestore.collection(RECEIPTS_COLLECTION)
                .get()
                .await()
            
            var deletedCount = 0
            for (document in snapshot.documents) {
                try {
                    document.reference.delete().await()
                    deletedCount++
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to delete receipt ${document.id}", e)
                }
            }
            
            Log.d(TAG, "Deleted $deletedCount receipts from Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete all receipts from Firestore", e)
            false
        }
    }
    
    // Full bidirectional sync
    suspend fun performFullSync(): SyncResult {
        return try {
            // First, push unsynced local receipts
            val pushedCount = syncUnsyncedReceipts()
            
            // Then, pull new receipts from Firestore
            val pulledCount = syncReceiptsFromFirestore()
            
            SyncResult(
                success = true,
                pushedCount = pushedCount,
                pulledCount = pulledCount,
                errorMessage = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Full sync failed", e)
            SyncResult(
                success = false,
                pushedCount = 0,
                pulledCount = 0,
                errorMessage = e.message
            )
        }
    }
}

data class SyncResult(
    val success: Boolean,
    val pushedCount: Int,
    val pulledCount: Int,
    val errorMessage: String?
)
