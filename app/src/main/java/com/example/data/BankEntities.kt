package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bank_cards")
data class BankCardEntity(
    @PrimaryKey val id: String, // e.g. "blu_Blu" or "melli_4512"
    val bankId: String,
    val bankName: String,
    val cardMask: String,
    val latestBalanceRial: Long,
    val lastUpdatedTimestamp: Long,
    val isExcludedFromTotal: Boolean = false,
    val displayOrder: Int = 0,
    val colorHex: String = "#3B82F6"
)

@Entity(
    tableName = "bank_transactions",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["cardId"])
    ]
)
data class BankTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: String,
    val bankName: String,
    val type: String, // DEPOSIT, WITHDRAWAL, TRANSFER, FEE
    val amountRial: Long,
    val balanceAfterRial: Long,
    val timestamp: Long,
    val rawSms: String,
    val description: String = "",
    val category: String = "عمومی",
    val isExportedToLedger: Boolean = false
)
