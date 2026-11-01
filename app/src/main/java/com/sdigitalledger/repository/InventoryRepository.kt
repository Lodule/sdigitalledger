package com.sdigitalledger.repository

import com.sdigitalledger.data.InventoryDao
import com.sdigitalledger.data.entities.InventoryItem
import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val inventoryDao: InventoryDao) {
    val allItems: Flow<List<InventoryItem>> = inventoryDao.getAllItems()

    suspend fun insert(item: InventoryItem) {
        inventoryDao.insertItem(item)
    }

    suspend fun update(item: InventoryItem) {
        inventoryDao.updateItem(item)
    }

    suspend fun delete(item: InventoryItem) {
        inventoryDao.deleteItem(item)
    }

    suspend fun getItemById(id: Int): InventoryItem? {
        return inventoryDao.getItemById(id)
    }
}
