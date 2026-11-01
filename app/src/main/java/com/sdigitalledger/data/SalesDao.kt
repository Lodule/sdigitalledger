package com.sdigitalledger.data

import androidx.room.*
import com.sdigitalledger.data.entities.Sale
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDao {
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE isCredit = 1")
    fun getCreditSales(): Flow<List<Sale>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale)

    @Delete
    suspend fun deleteSale(sale: Sale)

    @Query("SELECT SUM(totalAmount) FROM sales WHERE date >= :startOfDay")
    fun getTodayTotalSales(startOfDay: Long): Flow<Double?>
}
