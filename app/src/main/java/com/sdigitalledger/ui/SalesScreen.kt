package com.sdigitalledger.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sdigitalledger.data.entities.InventoryItem
import com.sdigitalledger.data.entities.Sale
import com.sdigitalledger.viewmodel.InventoryViewModel
import com.sdigitalledger.viewmodel.SalesViewModel

@Composable
fun SalesScreen(
    salesViewModel: SalesViewModel,
    inventoryViewModel: InventoryViewModel,
    isPremium: Boolean = false,
    onUpgradeClick: () -> Unit = {}
) {
    val sales by salesViewModel.allSales.collectAsState(initial = emptyList())
    val inventoryItems by inventoryViewModel.allItems.collectAsState(initial = emptyList())
    val todayTotal by salesViewModel.getTodayTotalSales().collectAsState(initial = 0.0)
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Record Sale")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. Ledger Dashboard Summary Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ledger Dashboard",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = {
                            if (isPremium) {
                                Toast.makeText(context, "Exporting Ledger to PDF/Excel...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "PDF/Excel exports require Premium!", Toast.LENGTH_SHORT).show()
                                onUpgradeClick()
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Export Report")
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Balance / Total Sales: UGX ${todayTotal ?: 0.0}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Total Transactions: ${sales.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 2. Suggested Ad Placement: AdMob Banner Ad (Free Version only)
            if (!isPremium) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SPONSORED ADVERTISEMENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AdMobBannerView()
                    }
                }
            }

            // 3. Recent Transactions List
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sales) { sale ->
                    val itemName = inventoryItems.find { it.id == sale.itemId }?.name ?: "Unknown Item"
                    SaleRow(sale, itemName)
                }
            }
        }
    }

    if (showDialog) {
        AddSaleDialog(
            inventoryItems = inventoryItems,
            onDismiss = { showDialog = false },
            onConfirm = { itemId, qty, total, customer, phone, isCredit ->
                salesViewModel.recordSale(
                    itemId = itemId,
                    quantity = qty,
                    totalAmount = total,
                    customerName = customer,
                    customerPhone = phone,
                    isCredit = isCredit
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun SaleRow(sale: Sale, itemName: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = itemName, style = MaterialTheme.typography.titleMedium)
                Text(text = "Qty: ${sale.quantity} | Total: UGX ${sale.totalAmount}")
                if (sale.isCredit) {
                    Text(text = "Credit to: ${sale.customerName ?: "Unknown"}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleDialog(
    inventoryItems: List<InventoryItem>,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Double, String, String, Boolean) -> Unit
) {
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }
    var quantity by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var isCredit by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Sale") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedItem?.name ?: "Select Item",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        inventoryItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.name) },
                                onClick = {
                                    selectedItem = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") })
                Spacer(modifier = Modifier.height(8.dp))
                if (isCredit) {
                    TextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Customer Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = customerPhone, onValueChange = { customerPhone = it }, label = { Text("Customer Phone (WhatsApp)") })
                } else {
                    TextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Customer Name (Optional)") })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCredit, onCheckedChange = { isCredit = it })
                    Text("Is Credit Sale?")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                selectedItem?.let { item ->
                    val q = quantity.toIntOrNull() ?: 0
                    val total = q * item.price
                    onConfirm(item.id, q, total, customerName, customerPhone, isCredit)
                }
            }) {
                Text("Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
