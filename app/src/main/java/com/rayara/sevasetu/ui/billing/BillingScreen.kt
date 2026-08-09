package com.rayara.sevasetu.ui.billing

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rayara.sevasetu.data.models.PaymentMode
import com.rayara.sevasetu.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    viewModel: BillingViewModel = viewModel(),
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    if (uiState.showPreviewDialog && uiState.previewReceipt != null) {
        ReceiptPreviewDialog(
            receipt = uiState.previewReceipt!!,
            pdfFile = uiState.generatedPdfFile,
            onDismiss = viewModel::dismissPreview,
            onPrint = { pdfFile ->
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    pdfFile
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                context.startActivity(Intent.createChooser(intent, "ರಶೀದಿ ಮುದ್ರಿಸಿ"))
            },
            onNewBill = viewModel::closePreviewAndContinue
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ರಾಯರ ಸೇವಾ ಸೇತು") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "ಇತಿಹಾಸ")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OrganizationHeader()
            
            CustomerDetailsSection(
                customerName = uiState.customerName,
                customerPhone = uiState.customerPhone,
                onNameChange = viewModel::updateCustomerName,
                onPhoneChange = viewModel::updateCustomerPhone
            )
            
            ServiceSelectionSection(
                selectedAmount = uiState.selectedAmount,
                customAmount = uiState.customAmount,
                onAmountSelect = viewModel::selectAmount,
                onCustomAmountChange = viewModel::updateCustomAmount
            )
            
            PaymentModeSection(
                selectedMode = uiState.paymentMode,
                onModeSelect = viewModel::selectPaymentMode
            )
            
            if (uiState.errorMessage != null) {
                ErrorMessage(uiState.errorMessage!!)
            }
            
            if (uiState.successMessage != null) {
                SuccessMessage(uiState.successMessage!!)
            }
            
            ActionButtons(
                isLoading = uiState.isLoading,
                onGenerateReceipt = viewModel::generateReceipt,
                onClear = viewModel::clearForm
            )
        }
    }
}

@Composable
fun OrganizationHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Constants.Organization.NAME,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Constants.Organization.ADDRESS,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CustomerDetailsSection(
    customerName: String,
    customerPhone: String,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ಗ್ರಾಹಕರ ವಿವರಗಳು",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "(₹500 ಕ್ಕಿಂತ ಹೆಚ್ಚಿಗೆ ಅಗತ್ಯ)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            OutlinedTextField(
                value = customerName,
                onValueChange = onNameChange,
                label = { Text("ಶ್ರೀಮತಿ / ಶ್ರೀ (ಐಚ್ಛಿಕ)") },
                placeholder = { Text("ಹೆಸರು ನಮೂದಿಸಿ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )
            
            OutlinedTextField(
                value = customerPhone,
                onValueChange = onPhoneChange,
                label = { Text("ದೂರವಾಣಿ ಸಂಖ್ಯೆ (ಐಚ್ಛಿಕ)") },
                placeholder = { Text("10 ಅಂಕಿಯ ಸಂಖ್ಯೆ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                }
            )
        }
    }
}

@Composable
fun ServiceSelectionSection(
    selectedAmount: Int?,
    customAmount: String,
    onAmountSelect: (Int) -> Unit,
    onCustomAmountChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ಸೇವೆ ಆಯ್ಕೆಮಾಡಿ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Constants.ServiceAmounts.PREDEFINED_AMOUNTS.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { amount ->
                        AmountButton(
                            amount = amount,
                            isSelected = selectedAmount == amount,
                            onClick = { onAmountSelect(amount) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            HorizontalDivider()
            
            OutlinedTextField(
                value = customAmount,
                onValueChange = onCustomAmountChange,
                label = { Text("ಕಸ್ಟಮ್ ಮೊತ್ತ") },
                placeholder = { Text("ಬೇರೆ ಮೊತ್ತ ನಮೂದಿಸಿ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(Icons.Default.CurrencyRupee, contentDescription = null)
                }
            )
        }
    }
}

@Composable
fun AmountButton(
    amount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) 
                MaterialTheme.colorScheme.onPrimary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = "₹$amount",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun PaymentModeSection(
    selectedMode: PaymentMode?,
    onModeSelect: (PaymentMode) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ಪಾವತಿ ವಿಧಾನ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMode.values().forEach { mode ->
                    PaymentModeButton(
                        mode = mode,
                        isSelected = selectedMode == mode,
                        onClick = { onModeSelect(mode) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentModeButton(
    mode: PaymentMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurface
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = if (isSelected) 2.dp else 1.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (mode) {
                    PaymentMode.CASH -> Icons.Default.Money
                    PaymentMode.PHONEPE -> Icons.Default.PhoneAndroid
                    PaymentMode.ONLINE -> Icons.Default.CreditCard
                },
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mode.kannadaName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SuccessMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActionButtons(
    isLoading: Boolean,
    onGenerateReceipt: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onClear,
            modifier = Modifier.weight(1f).height(56.dp),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.Clear, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ತೆರವುಗೊಳಿಸಿ")
        }
        
        Button(
            onClick = onGenerateReceipt,
            modifier = Modifier.weight(2f).height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Print, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ರಶೀದಿ ರಚಿಸಿ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReceiptPreviewDialog(
    receipt: com.rayara.sevasetu.data.database.entities.Receipt,
    pdfFile: java.io.File?,
    onDismiss: () -> Unit,
    onPrint: (java.io.File) -> Unit,
    onNewBill: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ರಶೀದಿ ಪೂರ್ವವೀಕ್ಷಣೆ")
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = Constants.Organization.NAME,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        HorizontalDivider()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ಸಂ. ${receipt.receiptNumber}", fontWeight = FontWeight.Bold)
                            Text(receipt.date, style = MaterialTheme.typography.bodySmall)
                        }
                        
                        Text("${receipt.customerName}", style = MaterialTheme.typography.bodyMedium)
                        Text("${receipt.customerPhone}", style = MaterialTheme.typography.bodySmall)
                        
                        HorizontalDivider()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(receipt.serviceDescription)
                            Text(receipt.getFormattedAmount(), fontWeight = FontWeight.Bold)
                        }
                        
                        HorizontalDivider()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ಒಟ್ಟು:", fontWeight = FontWeight.Bold)
                            Text(
                                receipt.getFormattedAmount(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Text(
                            "ಪಾವತಿ: ${receipt.getPaymentModeEnum().kannadaName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Text(
                    text = "ರಶೀದಿ ಯಶಸ್ವಿಯಾಗಿ ರಚಿಸಲಾಗಿದೆ!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        pdfFile?.let { onPrint(it) }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = pdfFile != null
                ) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ಮುದ್ರಿಸಿ / ಹಂಚಿಕೊಳ್ಳಿ", fontWeight = FontWeight.Bold)
                }
                
                OutlinedButton(
                    onClick = onNewBill,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ಹೊಸ ರಶೀದಿ", fontWeight = FontWeight.Bold)
                }
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ಮುಚ್ಚಿ")
                }
            }
        }
    )
}
