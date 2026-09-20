package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class VoucherWithLines(
    val voucher: JournalEntryEntity,
    val lines: List<JournalLineEntity>
)

@Dao
interface CorporateDao {
    // Companies
    @Query("SELECT * FROM companies ORDER BY id ASC")
    fun getAllCompaniesFlow(): Flow<List<CompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: CompanyEntity): Long

    @Query("UPDATE companies SET isSelected = (id = :companyId)")
    suspend fun selectCompany(companyId: Long)

    // Chart of Accounts
    @Query("SELECT * FROM chart_of_accounts ORDER BY code ASC")
    fun getAllAccountsFlow(): Flow<List<AccountNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountNodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountNodeEntity>)

    // Journal Entries (Vouchers)
    @Query("SELECT * FROM journal_entries ORDER BY voucherNumber DESC")
    fun getAllJournalEntriesFlow(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalLines(lines: List<JournalLineEntity>)

    @Query("SELECT * FROM journal_lines WHERE voucherId = :voucherId")
    suspend fun getLinesForVoucher(voucherId: Long): List<JournalLineEntity>

    @Query("SELECT * FROM journal_lines ORDER BY voucherId DESC")
    fun getAllJournalLinesFlow(): Flow<List<JournalLineEntity>>

    @Query("SELECT MAX(voucherNumber) FROM journal_entries")
    suspend fun getMaxVoucherNumber(): Int?

    // Helper for balanced voucher insertion
    @Transaction
    suspend fun insertCompleteVoucher(entry: JournalEntryEntity, lines: List<JournalLineEntity>): Long {
        val voucherId = insertJournalEntry(entry)
        val linkedLines = lines.map { it.copy(voucherId = voucherId) }
        insertJournalLines(linkedLines)
        return voucherId
    }
}
