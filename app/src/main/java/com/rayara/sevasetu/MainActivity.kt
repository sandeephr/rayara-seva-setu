package com.rayara.sevasetu

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
import com.rayara.sevasetu.ui.billing.BillingScreen
import com.rayara.sevasetu.ui.history.HistoryScreen
import com.rayara.sevasetu.ui.settings.SettingsScreen
import com.rayara.sevasetu.ui.theme.RayaraSevaSetuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

@Composable
fun RayaraSevaSetuApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "billing"
    ) {
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
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
