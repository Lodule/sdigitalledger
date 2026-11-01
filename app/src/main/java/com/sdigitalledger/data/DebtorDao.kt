package com.sdigitalledger.data

import androidx.room.*
import com.sdigitalledger.data.entities.Debtor
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtorDao {
    @Query("SELECT * FROM debtors ORDER BY name ASC")
    fun getAllDebtors(): Flow<List<Debtor>>

    @Query("SELECT * FROM debtors WHERE id = :id")
    suspend fun getDebtorById(id: Int): Debtor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtor(debtor: Debtor)

    @Update
    suspend fun updateDebtor(debtor: Debtor)

    @Delete
    suspend fun deleteDebtor(debtor: Debtor)

    @Query("UPDATE debtors SET totalAmountOwed = totalAmountOwed + :amount WHERE id = :id")
    suspend fun updateDebt(id: Int, amount: Double)
}
