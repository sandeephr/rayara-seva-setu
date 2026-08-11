package com.rayara.sevasetu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rayara.sevasetu.auth.AuthManager
import com.rayara.sevasetu.ui.auth.AuthViewModel
import com.rayara.sevasetu.ui.auth.LoginScreen
import com.rayara.sevasetu.ui.billing.BillingScreen
import com.rayara.sevasetu.ui.history.HistoryScreen
import com.rayara.sevasetu.ui.settings.SettingsScreen
import com.rayara.sevasetu.ui.theme.RayaraSevaSetuTheme
import com.rayara.sevasetu.workers.SessionCheckWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    
    private val forceLogoutReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.rayara.sevasetu.FORCE_LOGOUT") {
                // Restart activity to show login screen
                recreate()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule periodic session check
        scheduleSessionCheck()
        
        // Register broadcast receiver for force logout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                forceLogoutReceiver,
                IntentFilter("com.rayara.sevasetu.FORCE_LOGOUT"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                forceLogoutReceiver,
                IntentFilter("com.rayara.sevasetu.FORCE_LOGOUT")
            )
        }
        
        setContent {
            RayaraSevaSetuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RayaraSevaSetuApp()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(forceLogoutReceiver)
    }
    
    private fun scheduleSessionCheck() {
        val sessionCheckRequest = PeriodicWorkRequestBuilder<SessionCheckWorker>(
            15, TimeUnit.MINUTES // Check every 15 minutes
        ).build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SessionCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            sessionCheckRequest
        )
    }
}

@Composable
fun RayaraSevaSetuApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val scope = rememberCoroutineScope()
    
    var isCheckingAuth by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(false) }
    
    // Check if user is logged in
    LaunchedEffect(Unit) {
        scope.launch {
            val authManager = AuthManager(context)
            isLoggedIn = authManager.isUserLoggedIn()
            isCheckingAuth = false
        }
    }
    
    // Initialize auth view model
    LaunchedEffect(Unit) {
        authViewModel.initialize(context)
    }
    
    if (isCheckingAuth) {
        // Show loading
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Empty - could add loading indicator
        }
        return
    }
    
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "billing" else "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("billing") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("billing") {
            BillingScreen(
                onNavigateToHistory = {
                    navController.navigate("history")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("history") {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("settings") {
            val activity = context as? ComponentActivity
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    // Restart activity to force fresh auth check
                    activity?.recreate()
                }
            )
        }
    }
}
