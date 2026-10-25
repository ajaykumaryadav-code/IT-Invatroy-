package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_transactions",
    indices = [
        Index(value = ["assetTag"]),
        Index(value = ["type"]),
        Index(value = ["fromFacility"]),
        Index(value = ["toFacility"]),
        Index(value = ["timestamp"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetTag: String,
    val itemName: String,
    val type: String, // RECEIVE, ISSUE, RETURN, TRANSFER, ADJUSTMENT
    val quantity: Int,
    val fromFacility: String? = null,
    val toFacility: String? = null,
    val fromDepartment: String? = null,
    val toDepartment: String? = null,
    val employeeName: String? = null,
    val employeeId: String? = null,
    val approvedBy: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED" // SYNCED, QUEUED
)
