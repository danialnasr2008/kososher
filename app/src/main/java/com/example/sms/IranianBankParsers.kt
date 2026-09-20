package com.example.sms

import com.example.common.PersianTextNormalizer

class BluBankParser : BankParser {
    override val bankId = "blu"
    override val bankName = "بلوبانک (سامان)"
    override val colorHex = "#0EA5E9" // Sky blue

    private val identifierRegex = Regex("(بلوبانک|blubank|بلو\\s*بانک|بلو)", RegexOption.IGNORE_CASE)
    // Examples: "واریز 2,500,000 ریال به حساب ... مانده: 18,450,000 ریال" or "واریز به حساب\nمبلغ: 3,500,000 ... مانده: 41,200,000"
    private val pattern = Regex("(واریز|برداشت|خرید|انتقال).*?(?:مبلغ[:\\s]*)?([\\d,]+)\\s*(?:ریال|تومان)?.*?مانده[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)

    override fun matches(normalizedText: String, sender: String): Boolean {
        return identifierRegex.containsMatchIn(normalizedText) ||
                sender.contains("blubank", ignoreCase = true) ||
                sender.equals("blu", ignoreCase = true)
    }

    override fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms? {
        val match = pattern.find(normalizedText) ?: return null
        val (typeStr, amountStr, balanceStr) = match.destructured

        val type = if (typeStr.contains("واریز")) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
        var amount = PersianTextNormalizer.extractCleanAmount(amountStr)
        var balance = PersianTextNormalizer.extractCleanAmount(balanceStr)

        // Blu bank typically operates in Rials in SMS
        if (normalizedText.contains("تومان")) {
            amount *= 10
            balance *= 10
        }

        return ParsedBankSms(
            bankId = bankId,
            bankName = bankName,
            cardOrAccountMask = "Blu",
            transactionType = type,
            amountRial = amount,
            finalBalanceRial = balance,
            timestamp = timestamp,
            rawBody = rawBody,
            description = "$typeStr بلوبانک"
        )
    }
}

class MelliBankParser : BankParser {
    override val bankId = "melli"
    override val bankName = "بانک ملی ایران"
    override val colorHex = "#1E3A8A" // Dark royal blue

    private val identifierRegex = Regex("(بانک ملی|ملی ایران|bmi\\.ir)", RegexOption.IGNORE_CASE)
    // Pattern: واریز/برداشت 500,000 ریال از حساب 6037***1234 مانده: 12,350,000 ریال
    private val pattern = Regex("(واریز|برداشت|خرید|پایا|ساتنا|انتقال)\\s*[:\\-]?\\s*([\\d,]+)\\s*(?:ریال|تومان)?.*?(?:کارت|حساب)[:\\s]*([\\d\\*]+).*?مانده[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)
    private val simplePattern = Regex("(واریز|برداشت|خرید|پایا|ساتنا|انتقال)\\s*[:\\-]?\\s*([\\d,]+)\\s*(?:ریال|تومان)?.*?مانده[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)

    override fun matches(normalizedText: String, sender: String): Boolean {
        return identifierRegex.containsMatchIn(normalizedText) || sender.contains("20000") || sender.contains("9820000")
    }

    override fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms? {
        var cardMask = "6037"
        var typeStr = ""
        var amountStr = ""
        var balanceStr = ""

        val match = pattern.find(normalizedText)
        if (match != null) {
            typeStr = match.groupValues[1]
            amountStr = match.groupValues[2]
            cardMask = match.groupValues[3].takeLast(4)
            balanceStr = match.groupValues[4]
        } else {
            val simpleMatch = simplePattern.find(normalizedText) ?: return null
            typeStr = simpleMatch.groupValues[1]
            amountStr = simpleMatch.groupValues[2]
            balanceStr = simpleMatch.groupValues[3]
        }

        val type = if (typeStr.contains("واریز") || typeStr.contains("پایا")) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
        var amount = PersianTextNormalizer.extractCleanAmount(amountStr)
        var balance = PersianTextNormalizer.extractCleanAmount(balanceStr)

        if (normalizedText.contains("تومان")) {
            amount *= 10
            balance *= 10
        }

        return ParsedBankSms(
            bankId = bankId,
            bankName = bankName,
            cardOrAccountMask = cardMask,
            transactionType = type,
            amountRial = amount,
            finalBalanceRial = balance,
            timestamp = timestamp,
            rawBody = rawBody,
            description = "$typeStr بانک ملی"
        )
    }
}

class MellatBankParser : BankParser {
    override val bankId = "mellat"
    override val bankName = "بانک ملت"
    override val colorHex = "#BE123C" // Ruby red

    private val identifierRegex = Regex("(بانک ملت|ملت|mellat)", RegexOption.IGNORE_CASE)
    private val pattern = Regex("(واریز|برداشت|خرید|انتقال)\\s*[:\\-]?\\s*([\\d,]+).*?(?:موجودی|مانده)[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)

    override fun matches(normalizedText: String, sender: String): Boolean {
        return identifierRegex.containsMatchIn(normalizedText) || sender.contains("2000")
    }

    override fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms? {
        val match = pattern.find(normalizedText) ?: return null
        val (typeStr, amountStr, balanceStr) = match.destructured

        val type = if (typeStr.contains("واریز")) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
        var amount = PersianTextNormalizer.extractCleanAmount(amountStr)
        var balance = PersianTextNormalizer.extractCleanAmount(balanceStr)

        if (normalizedText.contains("تومان")) {
            amount *= 10
            balance *= 10
        }

        return ParsedBankSms(
            bankId = bankId,
            bankName = bankName,
            cardOrAccountMask = "6104",
            transactionType = type,
            amountRial = amount,
            finalBalanceRial = balance,
            timestamp = timestamp,
            rawBody = rawBody,
            description = "$typeStr بانک ملت"
        )
    }
}

class SaderatBankParser : BankParser {
    override val bankId = "saderat"
    override val bankName = "بانک صادرات ایران"
    override val colorHex = "#1E293B" // Slate indigo

    private val identifierRegex = Regex("(بانک صادرات|صادرات|bsi\\.ir)", RegexOption.IGNORE_CASE)
    private val pattern = Regex("(واریز|برداشت|خرید|انتقال)\\s*[:\\-]?\\s*([\\d,]+).*?مانده[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)

    override fun matches(normalizedText: String, sender: String): Boolean {
        return identifierRegex.containsMatchIn(normalizedText)
    }

    override fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms? {
        val match = pattern.find(normalizedText) ?: return null
        val (typeStr, amountStr, balanceStr) = match.destructured

        val type = if (typeStr.contains("واریز")) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
        val amount = PersianTextNormalizer.extractCleanAmount(amountStr)
        val balance = PersianTextNormalizer.extractCleanAmount(balanceStr)

        return ParsedBankSms(
            bankId = bankId,
            bankName = bankName,
            cardOrAccountMask = "6037",
            transactionType = type,
            amountRial = amount,
            finalBalanceRial = balance,
            timestamp = timestamp,
            rawBody = rawBody,
            description = "$typeStr بانک صادرات"
        )
    }
}

class ResalatBankParser : BankParser {
    override val bankId = "resalat"
    override val bankName = "بانک قرض‌الحسنه رسالت"
    override val colorHex = "#059669" // Emerald green

    private val identifierRegex = Regex("(رسالت|قرض\\s*الحسنه\\s*رسالت|rqbank)", RegexOption.IGNORE_CASE)
    private val pattern = Regex("(واریز|برداشت|پایا|انتقال)\\s*[:\\-]?\\s*([\\d,]+).*?مانده[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)

    override fun matches(normalizedText: String, sender: String): Boolean {
        return identifierRegex.containsMatchIn(normalizedText)
    }

    override fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms? {
        val match = pattern.find(normalizedText) ?: return null
        val (typeStr, amountStr, balanceStr) = match.destructured

        val type = if (typeStr.contains("واریز") || typeStr.contains("پایا")) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
        val amount = PersianTextNormalizer.extractCleanAmount(amountStr)
        val balance = PersianTextNormalizer.extractCleanAmount(balanceStr)

        return ParsedBankSms(
            bankId = bankId,
            bankName = bankName,
            cardOrAccountMask = "5041",
            transactionType = type,
            amountRial = amount,
            finalBalanceRial = balance,
            timestamp = timestamp,
            rawBody = rawBody,
            description = "$typeStr رسالت"
        )
    }
}

class PasargadBankParser : BankParser {
    override val bankId = "pasargad"
    override val bankName = "بانک پاسارگاد"
    override val colorHex = "#D97706" // Amber gold

    private val identifierRegex = Regex("(پاسارگاد|bpi\\.ir)", RegexOption.IGNORE_CASE)
    private val pattern = Regex("(واریز|برداشت|خرید|انتقال)\\s*[:\\-]?\\s*([\\d,]+).*?(?:موجودی|مانده)[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)

    override fun matches(normalizedText: String, sender: String): Boolean {
        return identifierRegex.containsMatchIn(normalizedText)
    }

    override fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms? {
        val match = pattern.find(normalizedText) ?: return null
        val (typeStr, amountStr, balanceStr) = match.destructured

        val type = if (typeStr.contains("واریز")) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
        val amount = PersianTextNormalizer.extractCleanAmount(amountStr)
        val balance = PersianTextNormalizer.extractCleanAmount(balanceStr)

        return ParsedBankSms(
            bankId = bankId,
            bankName = bankName,
            cardOrAccountMask = "5022",
            transactionType = type,
            amountRial = amount,
            finalBalanceRial = balance,
            timestamp = timestamp,
            rawBody = rawBody,
            description = "$typeStr پاسارگاد"
        )
    }
}

class TejaratBankParser : BankParser {
    override val bankId = "tejarat"
    override val bankName = "بانک تجارت"
    override val colorHex = "#0284C7" // Cyan blue

    private val identifierRegex = Regex("(بانک تجارت|تجارت|tejaratbank)", RegexOption.IGNORE_CASE)
    private val pattern = Regex("(واریز|برداشت|خرید|انتقال)\\s*[:\\-]?\\s*([\\d,]+).*?مانده[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)

    override fun matches(normalizedText: String, sender: String): Boolean {
        return identifierRegex.containsMatchIn(normalizedText)
    }

    override fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms? {
        val match = pattern.find(normalizedText) ?: return null
        val (typeStr, amountStr, balanceStr) = match.destructured

        val type = if (typeStr.contains("واریز")) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
        val amount = PersianTextNormalizer.extractCleanAmount(amountStr)
        val balance = PersianTextNormalizer.extractCleanAmount(balanceStr)

        return ParsedBankSms(
            bankId = bankId,
            bankName = bankName,
            cardOrAccountMask = "5859",
            transactionType = type,
            amountRial = amount,
            finalBalanceRial = balance,
            timestamp = timestamp,
            rawBody = rawBody,
            description = "$typeStr بانک تجارت"
        )
    }
}

/**
 * Fallback parser for any Iranian banking SMS that contains standard withdrawal/deposit and balance formats
 */
class GenericIranianBankParser : BankParser {
    override val bankId = "generic_bank"
    override val bankName = "سایر بانک‌ها"
    override val colorHex = "#475569" // Slate grey

    private val pattern = Regex("(واریز|برداشت|خرید|انتقال|پایا|ساتنا)\\s*[:\\-]?\\s*([\\d,]+).*?(?:مانده|موجودی)[:\\s]*([\\d,]+)", RegexOption.IGNORE_CASE)

    override fun matches(normalizedText: String, sender: String): Boolean {
        return pattern.containsMatchIn(normalizedText) && (normalizedText.contains("ریال") || normalizedText.contains("تومان") || normalizedText.contains("مانده") || normalizedText.contains("موجودی"))
    }

    override fun parse(normalizedText: String, timestamp: Long, rawBody: String): ParsedBankSms? {
        val match = pattern.find(normalizedText) ?: return null
        val (typeStr, amountStr, balanceStr) = match.destructured

        val type = if (typeStr.contains("واریز") || typeStr.contains("پایا")) TransactionType.DEPOSIT else TransactionType.WITHDRAWAL
        var amount = PersianTextNormalizer.extractCleanAmount(amountStr)
        var balance = PersianTextNormalizer.extractCleanAmount(balanceStr)

        if (normalizedText.contains("تومان")) {
            amount *= 10
            balance *= 10
        }

        // Try extracting bank name keyword if mentioned
        val detectedName = when {
            normalizedText.contains("سامان") -> "بانک سامان"
            normalizedText.contains("سپه") -> "بانک سپه"
            normalizedText.contains("آینده") -> "بانک آینده"
            normalizedText.contains("کشاورزی") -> "بانک کشاورزی"
            normalizedText.contains("مسکن") -> "بانک مسکن"
            normalizedText.contains("سینا") -> "بانک سینا"
            normalizedText.contains("پارسیان") -> "بانک پارسیان"
            normalizedText.contains("شهر") -> "بانک شهر"
            normalizedText.contains("رفاه") -> "بانک رفاه کارگران"
            else -> "کارت بانکی"
        }

        val cardMatch = Regex("(?:کارت|حساب)[:\\s]*([\\d\\*]+)").find(normalizedText)
        val cardMask = cardMatch?.groupValues?.get(1)?.takeLast(4) ?: "کارت"

        return ParsedBankSms(
            bankId = "bank_" + detectedName.hashCode().toString().takeLast(4),
            bankName = detectedName,
            cardOrAccountMask = cardMask,
            transactionType = type,
            amountRial = amount,
            finalBalanceRial = balance,
            timestamp = timestamp,
            rawBody = rawBody,
            description = "$typeStr $detectedName"
        )
    }
}
