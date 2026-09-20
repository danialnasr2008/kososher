package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryName: String,
    val monthlyLimitRial: Long,
    val spentRial: Long = 0L,
    val iconName: String = "category",
    val monthPeriod: String = "1405-01" // Iranian Solar month format
)

@Entity(tableName = "cheques")
data class ChequeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chequeNumber: String,
    val sayadId: String, // 16-digit Sayad tracking id
    val partyName: String, // Issuer or receiver
    val bankName: String,
    val amountRial: Long,
    val dueDateTimestamp: Long,
    val isIncoming: Boolean, // true = دریافتنی, false = پرداختنی
    val isPassed: Boolean = false,
    val isBounced: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val bankName: String,
    val totalAmountRial: Long,
    val monthlyInstallmentRial: Long,
    val totalInstallments: Int,
    val paidInstallments: Int = 0,
    val nextDueTimestamp: Long,
    val interestRatePercent: Double = 18.0
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmountRial: Long,
    val currentAmountRial: Long = 0L,
    val deadlineTimestamp: Long,
    val colorHex: String = "#10B981",
    val category: String = "پس‌انداز"
)
