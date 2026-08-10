package com.rayara.sevasetu.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.rayara.sevasetu.utils.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    var selectedMode by remember { mutableStateOf(prefsManager.receiptColorMode) }
    
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
                .padding(16.dp)
        ) {
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
        }
    }
}
