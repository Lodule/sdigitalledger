package com.sdigitalledger.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debtors")
data class Debtor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phoneNumber: String,
    val totalAmountOwed: Double,
    val lastReminderSent: Long = 0
)
