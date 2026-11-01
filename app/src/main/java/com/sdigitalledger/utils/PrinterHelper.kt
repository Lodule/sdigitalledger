package com.sdigitalledger.utils

import android.content.Context
import android.widget.Toast

class PrinterHelper(private val context: Context) {
    fun printReceipt(items: Map<String, Double>, totalAmount: Double) {
        // Placeholder helper for Bluetooth / Thermal printing receipt generation
        val receiptText = buildString {
            appendLine("--- DIGITAL LEDGER RECEIPT ---")
            items.forEach { (item, price) ->
                appendLine("$item: $$price")
            }
            appendLine("------------------------------")
            appendLine("TOTAL: $$totalAmount")
        }
        Toast.makeText(context, "Receipt prepared:\n$receiptText", Toast.LENGTH_SHORT).show()
    }
}
