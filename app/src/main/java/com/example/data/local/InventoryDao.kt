package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY updatedAt DESC")
    fun getAllItemsFlow(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE facility = :facility ORDER BY updatedAt DESC")
    fun getItemsByFacilityFlow(facility: String): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE assetTag = :assetTag LIMIT 1")
    suspend fun getItemByAssetTag(assetTag: String): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE barcode = :barcode OR assetTag = :barcode LIMIT 1")
    suspend fun getItemByBarcode(barcode: String): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE availableQuantity <= minimumAlertThreshold")
    fun getLowStockItemsFlow(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items: List<InventoryItemEntity>)

    @Update
    suspend fun updateItem(item: InventoryItemEntity)

    @Delete
    suspend fun deleteItem(item: InventoryItemEntity)

    @Query("DELETE FROM inventory_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("SELECT COUNT(*) FROM inventory_items")
    suspend fun getItemCount(): Int

    // Transactions
    @Query("SELECT * FROM inventory_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM inventory_transactions WHERE fromFacility = :facility OR toFacility = :facility ORDER BY timestamp DESC")
    fun getTransactionsByFacilityFlow(facility: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM inventory_transactions WHERE assetTag = :assetTag ORDER BY timestamp DESC")
    fun getTransactionsByAssetTagFlow(assetTag: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<TransactionEntity>)

    @Query("SELECT COUNT(*) FROM inventory_transactions")
    suspend fun getTransactionCount(): Int
}
