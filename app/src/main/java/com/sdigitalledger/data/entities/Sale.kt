package com.sdigitalledger.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Int,
    val quantity: Int,
    val totalAmount: Double,
    val date: Long,
    val customerName: String? = null,
    val customerPhone: String? = null,
    val isCredit: Boolean = false
)
