package com.sdigitalledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sdigitalledger.data.entities.Debtor
import com.sdigitalledger.repository.DebtorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DebtorViewModel(private val repository: DebtorRepository) : ViewModel() {

    val allDebtors: Flow<List<Debtor>> = repository.allDebtors

    fun insert(debtor: Debtor) = viewModelScope.launch {
        repository.insert(debtor)
    }

    fun update(debtor: Debtor) = viewModelScope.launch {
        repository.update(debtor)
    }

    fun delete(debtor: Debtor) = viewModelScope.launch {
        repository.delete(debtor)
    }

    fun updateDebt(id: Int, amount: Double) = viewModelScope.launch {
        repository.updateDebt(id, amount)
    }

    class DebtorViewModelFactory(private val repository: DebtorRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DebtorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DebtorViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
