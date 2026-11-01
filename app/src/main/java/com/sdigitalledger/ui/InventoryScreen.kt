package com.sdigitalledger.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sdigitalledger.data.entities.InventoryItem
import com.sdigitalledger.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
    val items by viewModel.allItems.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn {
                items(items) { item ->
                    InventoryRow(item, onDelete = { viewModel.delete(item) })
                }
            }
        }
    }

    if (showDialog) {
        AddItemDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, qty, price ->
                viewModel.insert(InventoryItem(name = name, quantity = qty, price = price))
                showDialog = false
            }
        )
    }
}

@Composable
fun InventoryRow(item: InventoryItem, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Qty: ${item.quantity} | Price: UGX ${item.price}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Inventory Item") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") })
                TextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") })
                TextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val q = quantity.toIntOrNull() ?: 0
                val p = price.toDoubleOrNull() ?: 0.0
                onConfirm(name, q, p)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
