package com.rayara.sevasetu.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.rayara.sevasetu.auth.AuthManager
import com.rayara.sevasetu.data.database.AppDatabase
import com.rayara.sevasetu.utils.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    val authManager = remember { AuthManager(context) }
    val database = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()
    
    var selectedMode by remember { mutableStateOf(prefsManager.receiptColorMode) }
    var currentUser by remember { mutableStateOf<com.rayara.sevasetu.data.database.entities.User?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    
    // Load current user
    LaunchedEffect(Unit) {
        currentUser = authManager.getCurrentUser()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ಸೆಟ್ಟಿಂಗ್ಸ್") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "ಹಿಂದೆ")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // User Profile Section
            if (currentUser != null) {
                Text(
                    text = "ಖಾತೆ",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentUser!!.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "+91 ${currentUser!!.mobileNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        
                        OutlinedButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ಲಾಗ್ ಔಟ್")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Text(
                text = "ರಸೀದಿ ವಿನ್ಯಾಸ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .selectableGroup()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ಬಣ್ಣದ ಮೋಡ್",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Color mode option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (selectedMode == PreferencesManager.COLOR_MODE_COLOR),
                                onClick = {
                                    selectedMode = PreferencesManager.COLOR_MODE_COLOR
                                    prefsManager.receiptColorMode = PreferencesManager.COLOR_MODE_COLOR
                                },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedMode == PreferencesManager.COLOR_MODE_COLOR),
                            onClick = null
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = "🎨 ಬಣ್ಣದ ರಸೀದಿ",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "ಸುಂದರವಾದ ಕೇಸರಿ ಗ್ರೇಡಿಯಂಟ್ ಮತ್ತು ಬಣ್ಣಗಳು",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // B&W mode option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (selectedMode == PreferencesManager.COLOR_MODE_BW),
                                onClick = {
                                    selectedMode = PreferencesManager.COLOR_MODE_BW
                                    prefsManager.receiptColorMode = PreferencesManager.COLOR_MODE_BW
                                },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedMode == PreferencesManager.COLOR_MODE_BW),
                            onClick = null
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = "⚫⚪ ಕಪ್ಪು ಮತ್ತು ಬಿಳಿ",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "B&W ಪ್ರಿಂಟರ್‌ಗಳಿಗೆ ಅನುಕೂಲಿಸಲಾಗಿದೆ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Info card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ℹ️ ಮಾಹಿತಿ",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "• ಬಣ್ಣದ ಮೋಡ್: ಬಣ್ಣದ ಪ್ರಿಂಟರ್‌ಗಳಿಗೆ ಉತ್ತಮ\n" +
                               "• B&W ಮೋಡ್: ಕಪ್ಪು ಮತ್ತು ಬಿಳಿ ಪ್ರಿಂಟರ್‌ಗಳಿಗೆ ಉತ್ತಮ\n" +
                               "• ಎರಡೂ ಮೋಡ್‌ಗಳು ಎಲ್ಲಾ ಪ್ರಿಂಟರ್‌ಗಳಲ್ಲಿ ಕೆಲಸ ಮಾಡುತ್ತವೆ",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Dangerous Zone
            Text(
                text = "⚠️ ಅಪಾಯಕಾರಿ ವಲಯ",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ಎಲ್ಲಾ ವಹಿವಾಟುಗಳನ್ನು ತೆರವುಗೊಳಿಸಿ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "(ಪರೀಕ್ಷಾ ಉದ್ದೇಶಕ್ಕಾಗಿ ಮಾತ್ರ)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Button(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ಎಲ್ಲಾ ವಹಿವಾಟುಗಳನ್ನು ಅಳಿಸಿ")
                    }
                }
            }
        }
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("ಲಾಗ್ ಔಟ್?") },
            text = { Text("ನೀವು ಖಚಿತವಾಗಿ ಲಾಗ್ ಔಟ್ ಮಾಡಲು ಬಯಸುವಿರಾ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            authManager.logout()
                            showLogoutDialog = false
                            onLogout()
                        }
                    }
                ) {
                    Text("ಹೌದು")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("ಇಲ್ಲ")
                }
            }
        )
    }
    
    // Clear All Transactions Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("ಎಲ್ಲಾ ವಹಿವಾಟುಗಳನ್ನು ಅಳಿಸುವುದೇ?") },
            text = { 
                Text(
                    "ಇದು ಎಲ್ಲಾ ರಸೀದಿಗಳನ್ನು ಶಾಶ್ವತವಾಗಿ ಅಳಿಸುತ್ತದೆ. ಈ ಕ್ರಿಯೆಯನ್ನು ರದ್ದುಗೊಳಿಸಲಾಗುವುದಿಲ್ಲ!\n\n" +
                    "ನೀವು ಖಚಿತವಾಗಿರುವಿರಾ?"
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            database.receiptDao().deleteAllReceipts()
                            showClearDialog = false
                            Toast.makeText(
                                context,
                                "ಎಲ್ಲಾ ವಹಿವಾಟುಗಳನ್ನು ಅಳಿಸಲಾಗಿದೆ",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Navigate back to force UI refresh
                            kotlinx.coroutines.delay(500)
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("ಅಳಿಸಿ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("ರದ್ದುಮಾಡಿ")
                }
            }
        )
    }
}
