package com.sdigitalledger

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.sdigitalledger.data.AppDatabase
import com.sdigitalledger.repository.DebtorRepository
import com.sdigitalledger.repository.InventoryRepository
import com.sdigitalledger.repository.SalesRepository

class DigitalLedgerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val inventoryRepository by lazy { InventoryRepository(database.inventoryDao()) }
    val salesRepository by lazy { SalesRepository(database.salesDao()) }
    val debtorRepository by lazy { DebtorRepository(database.debtorDao()) }

    override fun onCreate() {
        super.onCreate()
        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}
    }
}
