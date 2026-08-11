package com.rayara.sevasetu.data.database.dao

import androidx.room.*
import com.rayara.sevasetu.data.database.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): User?
    
    @Query("SELECT * FROM users WHERE mobileNumber = :mobile")
    suspend fun getUserByMobile(mobile: String): User?
    
    @Query("SELECT * FROM users WHERE name = :name")
    suspend fun getUserByName(name: String): User?
    
    @Query("SELECT * FROM users WHERE firebaseUid = :firebaseUid")
    suspend fun getUserByFirebaseUid(firebaseUid: String): User?
    
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): User?
    
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<User?>
    
    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE userId = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long)
    
    @Query("UPDATE users SET currentDeviceId = :deviceId, loginToken = :token WHERE userId = :userId")
    suspend fun updateDeviceInfo(userId: String, deviceId: String, token: String)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
