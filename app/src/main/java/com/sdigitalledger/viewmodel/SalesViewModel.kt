package com.sdigitalledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sdigitalledger.data.entities.Debtor
import com.sdigitalledger.data.entities.InventoryItem
import com.sdigitalledger.data.entities.Sale
import com.sdigitalledger.repository.DebtorRepository
import com.sdigitalledger.repository.InventoryRepository
import com.sdigitalledger.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

class SalesViewModel(
    private val salesRepository: SalesRepository,
    private val inventoryRepository: InventoryRepository,
    private val debtorRepository: DebtorRepository
) : ViewModel() {

    val allSales: Flow<List<Sale>> = salesRepository.allSales
    val creditSales: Flow<List<Sale>> = salesRepository.creditSales

    fun recordSale(
        itemId: Int,
        quantity: Int,
        totalAmount: Double,
        customerName: String?,
        customerPhone: String?,
        isCredit: Boolean
    ) = viewModelScope.launch {
        // 1. Record the sale
        val sale = Sale(
            itemId = itemId,
            quantity = quantity,
            totalAmount = totalAmount,
            date = System.currentTimeMillis(),
            customerName = customerName,
            customerPhone = customerPhone,
            isCredit = isCredit
        )
        salesRepository.insert(sale)

        // 2. Update inventory
        val item = inventoryRepository.getItemById(itemId)
        if (item != null) {
            val updatedItem = item.copy(quantity = item.quantity - quantity)
            inventoryRepository.update(updatedItem)
        }

        // 3. If credit sale, update or create debtor
        if (isCredit && !customerName.isNullOrBlank() && !customerPhone.isNullOrBlank()) {
            val existingDebtor = debtorRepository.allDebtors.firstOrNull()?.find { 
                it.phoneNumber == customerPhone 
            }
            if (existingDebtor != null) {
                debtorRepository.updateDebt(existingDebtor.id, totalAmount)
            } else {
                debtorRepository.insert(
                    Debtor(
                        name = customerName,
                        phoneNumber = customerPhone,
                        totalAmountOwed = totalAmount
                    )
                )
            }
        }
    }

    fun delete(sale: Sale) = viewModelScope.launch {
        salesRepository.delete(sale)
    }

    fun getTodayTotalSales(): Flow<Double?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return salesRepository.getTodayTotalSales(calendar.timeInMillis)
    }

    class SalesViewModelFactory(
        private val salesRepository: SalesRepository,
        private val inventoryRepository: InventoryRepository,
        private val debtorRepository: DebtorRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SalesViewModel(salesRepository, inventoryRepository, debtorRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
