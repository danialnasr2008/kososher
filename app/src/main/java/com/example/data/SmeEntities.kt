package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val company: String = "",
    val phone: String = "",
    val balanceRial: Long = 0L, // Positive: owes us, Negative: we owe them
    val creditLimitRial: Long = 500_000_000L,
    val isCustomer: Boolean = true,
    val isSupplier: Boolean = false,
    val economicCode: String = ""
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val contactName: String,
    val contactPhone: String = "",
    val issueDateTimestamp: Long,
    val subtotalRial: Long,
    val discountRial: Long = 0L,
    val vatTaxRial: Long = 0L, // 9% or 10% VAT
    val totalRial: Long,
    val isProforma: Boolean = false, // پیش‌فاکتور vs فاکتور رسمی
    val isPaid: Boolean = false,
    val itemsSummary: String = "",
    val notes: String = ""
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sku: String,
    val name: String,
    val unit: String = "عدد", // عدد، بسته، متر، کیلوگرم
    val unitPriceRial: Long,
    val purchasePriceRial: Long,
    val quantity: Int,
    val reorderPoint: Int = 5 // هشدار نقطه سفارش
)

@Entity(tableName = "petty_cash")
data class PettyCashEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fundName: String,
    val custodianName: String,
    val initialAmountRial: Long,
    val currentAmountRial: Long,
    val lastSettledTimestamp: Long
)
