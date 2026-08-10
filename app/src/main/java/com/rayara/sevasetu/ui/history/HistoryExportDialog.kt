package com.rayara.sevasetu.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

enum class ExportType {
    THIS_MONTH,
    LAST_MONTH,
    THIS_YEAR,
    CUSTOM_RANGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryExportDialog(
    onDismiss: () -> Unit,
    onExport: (ExportType, String, String) -> Unit
) {
    var selectedType by remember { mutableStateOf(ExportType.THIS_MONTH) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ವಹಿವಾಟು ರಫ್ತು ಮಾಡಿ") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("ಅವಧಿ ಆಯ್ಕೆಮಾಡಿ:", style = MaterialTheme.typography.bodyMedium)
                
                ExportTypeOption(
                    text = "ಈ ತಿಂಗಳು",
                    selected = selectedType == ExportType.THIS_MONTH,
                    onClick = { selectedType = ExportType.THIS_MONTH }
                )
                
                ExportTypeOption(
                    text = "ಕಳೆದ ತಿಂಗಳು",
                    selected = selectedType == ExportType.LAST_MONTH,
                    onClick = { selectedType = ExportType.LAST_MONTH }
                )
                
                ExportTypeOption(
                    text = "ಈ ವರ್ಷ",
                    selected = selectedType == ExportType.THIS_YEAR,
                    onClick = { selectedType = ExportType.THIS_YEAR }
                )
                
                ExportTypeOption(
                    text = "ಕಸ್ಟಮ್ ಶ್ರೇಣಿ",
                    selected = selectedType == ExportType.CUSTOM_RANGE,
                    onClick = { selectedType = ExportType.CUSTOM_RANGE }
                )
                
                if (selectedType == ExportType.CUSTOM_RANGE) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("ಪ್ರಾರಂಭ ದಿನಾಂಕ (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("ಅಂತಿಮ ದಿನಾಂಕ (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val (start, end) = getDateRange(selectedType, startDate, endDate)
                    onExport(selectedType, start, end)
                }
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ರಫ್ತು ಮಾಡಿ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ರದ್ದುಮಾಡಿ")
            }
        }
    )
}

@Composable
fun ExportTypeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

fun getDateRange(type: ExportType, customStart: String, customEnd: String): Pair<String, String> {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    
    return when (type) {
        ExportType.THIS_MONTH -> {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val start = dateFormat.format(calendar.time)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val end = dateFormat.format(calendar.time)
            Pair(start, end)
        }
        ExportType.LAST_MONTH -> {
            calendar.add(Calendar.MONTH, -1)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val start = dateFormat.format(calendar.time)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val end = dateFormat.format(calendar.time)
            Pair(start, end)
        }
        ExportType.THIS_YEAR -> {
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            val start = dateFormat.format(calendar.time)
            calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
            val end = dateFormat.format(calendar.time)
            Pair(start, end)
        }
        ExportType.CUSTOM_RANGE -> {
            Pair(customStart, customEnd)
        }
    }
}
