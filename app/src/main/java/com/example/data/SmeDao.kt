package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SmeDao {
    // Contacts
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContactsFlow(): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    // Invoices
    @Query("SELECT * FROM invoices ORDER BY issueDateTimestamp DESC")
    fun getAllInvoicesFlow(): Flow<List<InvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)

    // Inventory
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllInventoryItemsFlow(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItemEntity)

    @Update
    suspend fun updateInventoryItem(item: InventoryItemEntity)

    @Delete
    suspend fun deleteInventoryItem(item: InventoryItemEntity)

    // Petty Cash
    @Query("SELECT * FROM petty_cash ORDER BY fundName ASC")
    fun getAllPettyCashFlow(): Flow<List<PettyCashEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPettyCash(fund: PettyCashEntity)

    @Update
    suspend fun updatePettyCash(fund: PettyCashEntity)
}
