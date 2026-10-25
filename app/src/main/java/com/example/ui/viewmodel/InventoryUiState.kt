package com.example.ui.viewmodel

import com.example.data.local.InventoryItemEntity
import com.example.data.local.TransactionEntity
import com.example.data.model.AppLanguage
import com.example.data.model.Department
import com.example.data.model.Facility
import com.example.data.model.ItemCategory
import com.example.data.model.UserRole

data class InventoryUiState(
    val items: List<InventoryItemEntity> = emptyList(),
    val filteredItems: List<InventoryItemEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val selectedFacility: Facility = Facility.ALL,
    val selectedCategory: ItemCategory = ItemCategory.ALL,
    val selectedDepartment: Department = Department.ALL,
    val searchQuery: String = "",
    val currentRole: UserRole = UserRole.ADMIN,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val currentTab: Int = 0, // 0: Dashboard, 1: Inventory, 2: Barcode Scanner, 3: Transactions/Issue, 4: Audit & Reports
    val isSyncing: Boolean = false,
    val lastSyncTime: Long = System.currentTimeMillis(),
    val syncMessage: String = "Central Cloud Synchronized",
    val selectedItemForDetail: InventoryItemEntity? = null,
    val itemForBarcodeLabel: InventoryItemEntity? = null,
    val scannedItemResult: InventoryItemEntity? = null,
    val infoMessage: String? = null,
    val errorMessage: String? = null
) {
    // Calculated KPI metrics
    val totalAssetsCount: Int get() = items.sumOf { it.totalQuantity }
    val totalAvailableCount: Int get() = items.sumOf { it.availableQuantity }
    val totalAssignedCount: Int get() = totalAssetsCount - totalAvailableCount
    val lowStockCount: Int get() = items.count { it.availableQuantity <= it.minimumAlertThreshold }
    val totalValuation: Double get() = items.sumOf { it.totalQuantity * it.unitPrice }

    val hardwareCount: Int get() = items.count { it.category == "HARDWARE" }
    val networkCount: Int get() = items.count { it.category == "NETWORK" }
    val consumablesCount: Int get() = items.count { it.category == "CONSUMABLES" }
    val softwareCount: Int get() = items.count { it.category == "SOFTWARE" }

    // Department breakdown
    val departmentCounts: Map<String, Int> get() {
        val map = mutableMapOf<String, Int>()
        items.forEach { item ->
            val dept = item.assignedDepartment ?: "Unassigned / Store"
            map[dept] = (map[dept] ?: 0) + (item.totalQuantity - item.availableQuantity)
        }
        return map
    }

    // Facility breakdown
    val facilityCounts: Map<String, Int> get() {
        val map = mutableMapOf<String, Int>()
        items.forEach { item ->
            map[item.facility] = (map[item.facility] ?: 0) + item.totalQuantity
        }
        return map
    }
}
