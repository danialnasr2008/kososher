package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BankDao {
    @Query("SELECT * FROM bank_cards ORDER BY displayOrder ASC, lastUpdatedTimestamp DESC")
    fun getAllCardsFlow(): Flow<List<BankCardEntity>>

    @Query("SELECT * FROM bank_cards WHERE id = :id LIMIT 1")
    suspend fun getCardById(id: String): BankCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCard(card: BankCardEntity)

    @Insert
    suspend fun insertTransaction(tx: BankTransactionEntity): Long

    @Query("SELECT * FROM bank_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<BankTransactionEntity>>

    @Query("SELECT * FROM bank_transactions WHERE cardId = :cardId ORDER BY timestamp DESC")
    fun getTransactionsForCard(cardId: String): Flow<List<BankTransactionEntity>>

    @Query("SELECT SUM(latestBalanceRial) FROM bank_cards WHERE isExcludedFromTotal = 0")
    fun getCombinedTotalRialFlow(): Flow<Long?>

    @Query("UPDATE bank_cards SET isExcludedFromTotal = :isExcluded WHERE id = :cardId")
    suspend fun updateCardExcluded(cardId: String, isExcluded: Boolean)

    @Query("UPDATE bank_transactions SET isExportedToLedger = 1 WHERE id = :txId")
    suspend fun markTransactionExported(txId: Long)

    @Query("DELETE FROM bank_cards")
    suspend fun clearCards()

    @Query("DELETE FROM bank_transactions")
    suspend fun clearTransactions()
}
