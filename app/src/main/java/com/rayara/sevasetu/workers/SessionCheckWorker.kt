package com.rayara.sevasetu.workers

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rayara.sevasetu.auth.AuthManager

class SessionCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val TAG = "SessionCheckWorker"
        const val WORK_NAME = "session_check_work"
    }
    
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Checking session validity...")
            
            val authManager = AuthManager(applicationContext)
            
            // Check if user is logged in
            val isLoggedIn = authManager.isUserLoggedIn()
            if (!isLoggedIn) {
                Log.d(TAG, "User not logged in, skipping check")
                return Result.success()
            }
            
            // Check if forced to logout
            val shouldLogout = authManager.checkForceLogout()
            
            if (shouldLogout) {
                Log.w(TAG, "Force logout detected! Logging out user...")
                
                // Logout the user
                authManager.logout()
                
                // Send broadcast to notify app
                val intent = Intent("com.rayara.sevasetu.FORCE_LOGOUT")
                applicationContext.sendBroadcast(intent)
                
                Log.d(TAG, "User logged out successfully")
            } else {
                Log.d(TAG, "Session is valid")
            }
            
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Session check failed", e)
            Result.retry()
        }
    }
}
