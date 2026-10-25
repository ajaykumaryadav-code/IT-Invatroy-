package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_items",
    indices = [
        Index(value = ["assetTag"], unique = true),
        Index(value = ["barcode"], unique = true),
        Index(value = ["facility"]),
        Index(value = ["category"])
    ]
)
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetTag: String,
    val name: String,
    val category: String, // HARDWARE, CONSUMABLES, NETWORK, SOFTWARE
    val subCategory: String,
    val brandModel: String,
    val serialNumber: String,
    val macAddress: String? = null,
    val ipAddress: String? = null,
    val facility: String, // LUCKNOW, KANPUR, AYODHYA, etc.
    val locationDetails: String,
    val totalQuantity: Int,
    val availableQuantity: Int,
    val minimumAlertThreshold: Int = 3,
    val unitPrice: Double,
    val currency: String = "₹",
    val supplierName: String,
    val invoiceNumber: String,
    val purchaseDate: String,
    val warrantyExpiryDate: String,
    val condition: String, // NEW, GOOD, UNDER_REPAIR, SCRAPPED
    val licenseKey: String? = null,
    val softwareExpiryDate: String? = null,
    val barcode: String,
    val assignedToEmployee: String? = null,
    val assignedEmployeeId: String? = null,
    val assignedDepartment: String? = null,
    val assignedDate: String? = null,
    val lastSyncStatus: String = "SYNCED",
    val updatedAt: Long = System.currentTimeMillis()
)
