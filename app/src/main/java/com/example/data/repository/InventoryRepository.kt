package com.example.data.repository

import com.example.data.local.InventoryDao
import com.example.data.local.InventoryItemEntity
import com.example.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventoryRepository(private val dao: InventoryDao) {

    fun getItems(facility: String): Flow<List<InventoryItemEntity>> {
        return if (facility == "ALL") {
            dao.getAllItemsFlow()
        } else {
            dao.getItemsByFacilityFlow(facility)
        }
    }

    fun getTransactions(facility: String): Flow<List<TransactionEntity>> {
        return if (facility == "ALL") {
            dao.getAllTransactionsFlow()
        } else {
            dao.getTransactionsByFacilityFlow(facility)
        }
    }

    suspend fun getItemByBarcode(barcode: String): InventoryItemEntity? {
        return dao.getItemByBarcode(barcode.trim())
    }

    suspend fun getItemByAssetTag(assetTag: String): InventoryItemEntity? {
        return dao.getItemByAssetTag(assetTag.trim())
    }

    suspend fun getItemById(id: Long): InventoryItemEntity? {
        return dao.getItemById(id)
    }

    suspend fun receiveNewItem(item: InventoryItemEntity, receivedBy: String): Long {
        val itemId = dao.insertItem(item)
        val transaction = TransactionEntity(
            assetTag = item.assetTag,
            itemName = item.name,
            type = "RECEIVE",
            quantity = item.totalQuantity,
            toFacility = item.facility,
            approvedBy = receivedBy,
            notes = "Received new procurement from ${item.supplierName} (Invoice: ${item.invoiceNumber}).",
            timestamp = System.currentTimeMillis()
        )
        dao.insertTransaction(transaction)
        return itemId
    }

    suspend fun receiveStockExisting(assetTag: String, additionalQty: Int, invoice: String, supplier: String, receivedBy: String): Boolean {
        val existing = dao.getItemByAssetTag(assetTag) ?: return false
        val updated = existing.copy(
            totalQuantity = existing.totalQuantity + additionalQty,
            availableQuantity = existing.availableQuantity + additionalQty,
            invoiceNumber = if (invoice.isNotBlank()) invoice else existing.invoiceNumber,
            supplierName = if (supplier.isNotBlank()) supplier else existing.supplierName,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateItem(updated)
        dao.insertTransaction(
            TransactionEntity(
                assetTag = updated.assetTag,
                itemName = updated.name,
                type = "RECEIVE",
                quantity = additionalQty,
                toFacility = updated.facility,
                approvedBy = receivedBy,
                notes = "Additional stock received: +$additionalQty units (Invoice: $invoice).",
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun issueItem(
        assetTag: String,
        quantity: Int,
        department: String,
        employeeName: String,
        employeeId: String,
        approvedBy: String,
        notes: String
    ): Result<Unit> {
        val item = dao.getItemByAssetTag(assetTag)
            ?: return Result.failure(Exception("Item $assetTag not found"))

        if (item.availableQuantity < quantity) {
            return Result.failure(Exception("Insufficient stock available: current available is ${item.availableQuantity}"))
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val updated = item.copy(
            availableQuantity = item.availableQuantity - quantity,
            assignedToEmployee = employeeName,
            assignedEmployeeId = employeeId,
            assignedDepartment = department,
            assignedDate = today,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateItem(updated)

        dao.insertTransaction(
            TransactionEntity(
                assetTag = item.assetTag,
                itemName = item.name,
                type = "ISSUE",
                quantity = quantity,
                fromFacility = item.facility,
                toDepartment = department,
                employeeName = employeeName,
                employeeId = employeeId,
                approvedBy = approvedBy,
                notes = if (notes.isNotBlank()) notes else "Issued to $employeeName ($employeeId), Department: $department.",
                timestamp = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }

    suspend fun returnItem(
        assetTag: String,
        quantity: Int,
        condition: String,
        receivedBy: String,
        notes: String
    ): Result<Unit> {
        val item = dao.getItemByAssetTag(assetTag)
            ?: return Result.failure(Exception("Item $assetTag not found"))

        val newAvailable = (item.availableQuantity + quantity).coerceAtMost(item.totalQuantity)
        val updated = item.copy(
            availableQuantity = newAvailable,
            condition = condition,
            assignedToEmployee = if (newAvailable == item.totalQuantity) null else item.assignedToEmployee,
            assignedEmployeeId = if (newAvailable == item.totalQuantity) null else item.assignedEmployeeId,
            assignedDepartment = if (newAvailable == item.totalQuantity) null else item.assignedDepartment,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateItem(updated)

        dao.insertTransaction(
            TransactionEntity(
                assetTag = item.assetTag,
                itemName = item.name,
                type = "RETURN",
                quantity = quantity,
                toFacility = item.facility,
                fromDepartment = item.assignedDepartment,
                employeeName = item.assignedToEmployee,
                employeeId = item.assignedEmployeeId,
                approvedBy = receivedBy,
                notes = "Returned $quantity unit(s). Condition assessed as: $condition. $notes",
                timestamp = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }

    suspend fun transferItem(
        assetTag: String,
        quantity: Int,
        targetFacility: String,
        approvedBy: String,
        notes: String
    ): Result<Unit> {
        val sourceItem = dao.getItemByAssetTag(assetTag)
            ?: return Result.failure(Exception("Source item $assetTag not found"))

        if (sourceItem.facility == targetFacility) {
            return Result.failure(Exception("Target facility cannot be the same as source facility"))
        }

        if (sourceItem.availableQuantity < quantity) {
            return Result.failure(Exception("Insufficient available stock to transfer: ${sourceItem.availableQuantity} available"))
        }

        // Deduct from source
        val updatedSource = sourceItem.copy(
            totalQuantity = sourceItem.totalQuantity - quantity,
            availableQuantity = sourceItem.availableQuantity - quantity,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateItem(updatedSource)

        // Check if destination item with similar specs exists in target facility
        val targetTag = "${sourceItem.assetTag.substringBeforeLast("-")}-${targetFacility.take(3)}"
        val existingTarget = dao.getItemByAssetTag(targetTag)
        if (existingTarget != null) {
            val updatedTarget = existingTarget.copy(
                totalQuantity = existingTarget.totalQuantity + quantity,
                availableQuantity = existingTarget.availableQuantity + quantity,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateItem(updatedTarget)
        } else {
            // Create item record at destination
            val newDestinationItem = sourceItem.copy(
                id = 0,
                assetTag = "$targetTag-${System.currentTimeMillis() % 1000}",
                barcode = "$targetTag-${System.currentTimeMillis() % 1000}",
                facility = targetFacility,
                locationDetails = "Transferred from ${sourceItem.facility}. Pending allocation.",
                totalQuantity = quantity,
                availableQuantity = quantity,
                assignedToEmployee = null,
                assignedEmployeeId = null,
                assignedDepartment = null,
                updatedAt = System.currentTimeMillis()
            )
            dao.insertItem(newDestinationItem)
        }

        dao.insertTransaction(
            TransactionEntity(
                assetTag = sourceItem.assetTag,
                itemName = sourceItem.name,
                type = "TRANSFER",
                quantity = quantity,
                fromFacility = sourceItem.facility,
                toFacility = targetFacility,
                approvedBy = approvedBy,
                notes = "Inter-facility transfer: $quantity unit(s) from ${sourceItem.facility} to $targetFacility. $notes",
                timestamp = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }

    suspend fun auditStockAdjustment(
        assetTag: String,
        physicalCount: Int,
        auditedBy: String,
        reason: String
    ): Result<Unit> {
        val item = dao.getItemByAssetTag(assetTag)
            ?: return Result.failure(Exception("Item $assetTag not found"))

        val diff = physicalCount - item.availableQuantity
        val updated = item.copy(
            availableQuantity = physicalCount,
            totalQuantity = (item.totalQuantity + diff).coerceAtLeast(physicalCount),
            updatedAt = System.currentTimeMillis()
        )
        dao.updateItem(updated)

        dao.insertTransaction(
            TransactionEntity(
                assetTag = item.assetTag,
                itemName = item.name,
                type = "ADJUSTMENT",
                quantity = kotlin.math.abs(diff),
                fromFacility = item.facility,
                approvedBy = auditedBy,
                notes = "Physical audit reconciliation: count changed from ${item.availableQuantity} to $physicalCount (Variance: $diff). Reason: $reason",
                timestamp = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }

    suspend fun updateItemDetails(item: InventoryItemEntity) {
        dao.updateItem(item.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteItem(item: InventoryItemEntity) {
        dao.deleteItem(item)
    }
}
