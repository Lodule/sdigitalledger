package com.sdigitalledger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdigitalledger.utils.PremiumManager
import kotlinx.coroutines.delay

enum class MobileMoneyProvider {
    MTN_MOMO,
    AIRTEL_MONEY
}

@Composable
fun MobileMoneyPaymentDialog(
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf(MobileMoneyProvider.MTN_MOMO) }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentStatusText by remember { mutableStateOf("") }
    var paymentComplete by remember { mutableStateOf(false) }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            paymentStatusText = "Sending Mobile Money prompt to $phoneNumber..."
            delay(1500)
            paymentStatusText = "Awaiting PIN confirmation for UGX ${PremiumManager.PREMIUM_PRICE_UGX}..."
            delay(2000)
            paymentStatusText = "Payment Received! Activating Digital Ledger Pro (Monthly Subscription)..."
            delay(1000)
            paymentComplete = true
            isProcessing = false
            onPaymentSuccess()
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upgrade to Pro - UGX 5,000/Month", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                if (paymentComplete) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Payment Successful!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "UGX 5,000 received. Digital Ledger Pro is activated for 1 Month!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else if (isProcessing) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = paymentStatusText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please check your phone screen and enter your Mobile Money PIN.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Plan Benefits Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("👑 Digital Ledger Pro (UGX 5,000 / Month):", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Remove all AdMob Banner Ads")
                            Text("• Export PDF & Excel Financial Reports")
                            Text("• Cloud Backup & Restore")
                            Text("• Priority WhatsApp & Email Support")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Mobile Money Network:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // MTN MoMo Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                                .background(
                                    color = if (selectedProvider == MobileMoneyProvider.MTN_MOMO) Color(0xFFFFCC00).copy(alpha = 0.3f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = if (selectedProvider == MobileMoneyProvider.MTN_MOMO) 2.dp else 1.dp,
                                    color = if (selectedProvider == MobileMoneyProvider.MTN_MOMO) Color(0xFFFFCC00) else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedProvider = MobileMoneyProvider.MTN_MOMO }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("MTN MoMo", fontWeight = FontWeight.Bold, color = Color(0xFFD48800))
                        }

                        // Airtel Money Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                                .background(
                                    color = if (selectedProvider == MobileMoneyProvider.AIRTEL_MONEY) Color(0xFFE53935).copy(alpha = 0.2f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = if (selectedProvider == MobileMoneyProvider.AIRTEL_MONEY) 2.dp else 1.dp,
                                    color = if (selectedProvider == MobileMoneyProvider.AIRTEL_MONEY) Color(0xFFE53935) else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedProvider = MobileMoneyProvider.AIRTEL_MONEY }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Airtel Money", fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Mobile Money Phone Number") },
                        placeholder = { Text("e.g. 0771234567 / 0701234567") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Price: UGX 5,000 / Month",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            if (!isProcessing && !paymentComplete) {
                Button(
                    onClick = {
                        if (phoneNumber.length >= 10) {
                            isProcessing = true
                        }
                    },
                    enabled = phoneNumber.length >= 10
                ) {
                    Text("Pay UGX 5,000 / Month")
                }
            } else if (paymentComplete) {
                Button(onClick = onDismiss) {
                    Text("Done")
                }
            }
        },
        dismissButton = {
            if (!isProcessing) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
