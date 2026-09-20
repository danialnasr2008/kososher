package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class CompanyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nationalId: String = "",
    val fiscalYear: String = "1405",
    val isSelected: Boolean = false
)

enum class AccountLevel {
    GROUP,       // گروه (۱ رقم) مثلا ۱: دارایی‌ها
    TOTAL,       // کل (۲ رقم) مثلا ۱۰: دارایی‌های جاری
    SUBSIDIARY,  // معین (۴ رقم) مثلا ۱۰۱۰: صندوق و بانک‌ها
    DETAIL       // تفصیلی (۶ رقم یا شناور) مثلا ۱۰۱۰۱: بانک ملی جاری
}

enum class AccountNature {
    DEBIT,  // بدهکار
    CREDIT  // بستانکار
}

@Entity(tableName = "chart_of_accounts")
data class AccountNodeEntity(
    @PrimaryKey val code: String, // e.g. "1", "11", "1101", "110101"
    val name: String,
    val level: AccountLevel,
    val parentCode: String = "",
    val nature: AccountNature,
    val balanceRial: Long = 0L
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val voucherNumber: Int,
    val dateTimestamp: Long,
    val description: String,
    val totalDebitRial: Long,
    val totalCreditRial: Long,
    val isBalanced: Boolean = true,
    val companyId: Long = 1
)

@Entity(
    tableName = "journal_lines",
    indices = [Index("voucherId"), Index("accountCode")]
)
data class JournalLineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val voucherId: Long,
    val accountCode: String,
    val accountName: String,
    val debitRial: Long = 0L,
    val creditRial: Long = 0L,
    val detailCode: String = "",
    val description: String = ""
)
