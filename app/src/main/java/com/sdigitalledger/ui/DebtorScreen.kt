package com.sdigitalledger.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sdigitalledger.data.entities.Debtor
import com.sdigitalledger.viewmodel.DebtorViewModel
import java.net.URLEncoder

@Composable

fun DebtorScreen(viewModel: DebtorViewModel) {
    val debtors by viewModel.allDebtors.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var showSettleDialog by remember { mutableStateOf<Debtor?>(null) }
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Debtor")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn {
                items(debtors) { debtor ->
                    DebtorRow(
                        debtor = debtor,
                        onSendReminder = {
                            val message = "Hello ${debtor.name}, this is a reminder regarding your outstanding balance of UGX ${debtor.totalAmountOwed} at our shop. Please settle it at your earliest convenience."
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?phone=${debtor.phoneNumber}&text=${URLEncoder.encode(message, "UTF-8")}")
                            }
                            context.startActivity(intent)
                        },
                        onSettle = { showSettleDialog = debtor }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddDebtorDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, amount ->
                viewModel.insert(Debtor(name = name, phoneNumber = phone, totalAmountOwed = amount))
                showAddDialog = false
            }
        )
    }

    showSettleDialog?.let { debtor ->
        SettleDebtDialog(
            debtor = debtor,
            onDismiss = { showSettleDialog = null },
            onConfirm = { amountPaid ->
                if (amountPaid >= debtor.totalAmountOwed) {
                    viewModel.delete(debtor)
                } else {
                    viewModel.updateDebt(debtor.id, -amountPaid)
                }
                showSettleDialog = null
            }
        )
    }
}

@Composable
fun DebtorRow(debtor: Debtor, onSendReminder: () -> Unit, onSettle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = debtor.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Phone: ${debtor.phoneNumber}")
                Text(
                    text = "Owed: UGX ${debtor.totalAmountOwed}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                IconButton(onClick = onSendReminder) {
                    Icon(Icons.Default.Notifications, contentDescription = "Reminder")
                }
                IconButton(onClick = onSettle) {
                    Icon(Icons.Default.Check, contentDescription = "Settle")
                }
            }
        }
    }
}

@Composable
fun AddDebtorDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Debtor") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Customer Name") })
                TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
                TextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount Owed") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val a = amount.toDoubleOrNull() ?: 0.0
                onConfirm(name, phone, a)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SettleDebtDialog(debtor: Debtor, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amountPaid by remember { mutableStateOf(debtor.totalAmountOwed.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settle Debt - ${debtor.name}") },
        text = {
            Column {
                Text("Enter amount paid by customer:")
                TextField(value = amountPaid, onValueChange = { amountPaid = it }, label = { Text("Amount Paid") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val a = amountPaid.toDoubleOrNull() ?: 0.0
                onConfirm(a)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
