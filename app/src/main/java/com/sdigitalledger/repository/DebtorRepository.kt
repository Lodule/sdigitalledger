package com.sdigitalledger.repository

import com.sdigitalledger.data.DebtorDao
import com.sdigitalledger.data.entities.Debtor
import kotlinx.coroutines.flow.Flow

class DebtorRepository(private val debtorDao: DebtorDao) {
    val allDebtors: Flow<List<Debtor>> = debtorDao.getAllDebtors()

    suspend fun insert(debtor: Debtor) {
        debtorDao.insertDebtor(debtor)
    }

    suspend fun update(debtor: Debtor) {
        debtorDao.updateDebtor(debtor)
    }

    suspend fun delete(debtor: Debtor) {
        debtorDao.deleteDebtor(debtor)
    }

    suspend fun getDebtorById(id: Int): Debtor? {
        return debtorDao.getDebtorById(id)
    }

    suspend fun updateDebt(id: Int, amount: Double) {
        debtorDao.updateDebt(id, amount)
    }
}
