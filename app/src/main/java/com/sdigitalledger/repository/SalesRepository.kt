package com.sdigitalledger.repository

import com.sdigitalledger.data.SalesDao
import com.sdigitalledger.data.entities.Sale
import kotlinx.coroutines.flow.Flow

class SalesRepository(private val salesDao: SalesDao) {
    val allSales: Flow<List<Sale>> = salesDao.getAllSales()
    val creditSales: Flow<List<Sale>> = salesDao.getCreditSales()

    suspend fun insert(sale: Sale) {
        salesDao.insertSale(sale)
    }

    suspend fun delete(sale: Sale) {
        salesDao.deleteSale(sale)
    }

    fun getTodayTotalSales(startOfDay: Long): Flow<Double?> {
        return salesDao.getTodayTotalSales(startOfDay)
    }
}
