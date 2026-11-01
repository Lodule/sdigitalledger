package com.sdigitalledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sdigitalledger.data.entities.InventoryItem
import com.sdigitalledger.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    val allItems: Flow<List<InventoryItem>> = repository.allItems

    fun insert(item: InventoryItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: InventoryItem) = viewModelScope.launch {
        repository.update(item)
    }

    fun delete(item: InventoryItem) = viewModelScope.launch {
        repository.delete(item)
    }

    class InventoryViewModelFactory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InventoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
