package com.example.sms

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER,
    FEE,
    UNKNOWN
}

data class ParsedBankSms(
    val bankId: String,
    val bankName: String,
    val cardOrAccountMask: String,
    val transactionType: TransactionType,
    val amountRial: Long,
    val finalBalanceRial: Long,
    val timestamp: Long,
    val rawBody: String,
    val description: String = ""
)

interface BankParser {
    val bankId: String
    val bankName: String
    val colorHex: String

    fun matches(normalizedText: String, sender: String): Boolean
    fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms?
}
