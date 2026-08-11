package com.rayara.sevasetu.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["mobileNumber"], unique = true),
        Index(value = ["name"], unique = true)
    ]
)
data class User(
    @PrimaryKey
    val userId: String,
    
    val name: String,
    
    val mobileNumber: String,
    
    val isVerified: Boolean = false,
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val lastLoginAt: Long = System.currentTimeMillis(),
    
    val currentDeviceId: String? = null,
    
    val loginToken: String? = null,
    
    val firebaseUid: String? = null
)
