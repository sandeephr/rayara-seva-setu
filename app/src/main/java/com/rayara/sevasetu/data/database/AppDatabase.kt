package com.rayara.sevasetu.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rayara.sevasetu.data.database.dao.ReceiptDao
import com.rayara.sevasetu.data.database.dao.UserDao
import com.rayara.sevasetu.data.database.entities.Receipt
import com.rayara.sevasetu.data.database.entities.User

@Database(
    entities = [Receipt::class, User::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun receiptDao(): ReceiptDao
    
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rayara_seva_setu_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
