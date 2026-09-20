package com.example.common

object PersianTextNormalizer {
    private val PERSIAN_ARABIC_DIGITS = mapOf(
        '۰' to '0', '۱' to '1', '۲' to '2', '۳' to '3', '۴' to '4',
        '۵' to '5', '۶' to '6', '۷' to '7', '۸' to '8', '۹' to '9',
        '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
        '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
    )

    private val ENGLISH_TO_PERSIAN_DIGITS = mapOf(
        '0' to '۰', '1' to '۱', '2' to '۲', '3' to '۳', '4' to '۴',
        '5' to '۵', '6' to '۶', '7' to '۷', '8' to '۸', '9' to '۹'
    )

    /**
     * Converts Persian and Arabic digits to ASCII 0-9
     */
    fun toLatinDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            sb.append(PERSIAN_ARABIC_DIGITS[ch] ?: ch)
        }
        return sb.toString()
    }

    fun normalize(rawText: String): String {
        var text = rawText
        // 1. Convert Persian/Arabic digits to ASCII 0-9 for regex parsing
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(PERSIAN_ARABIC_DIGITS[ch] ?: ch)
        }
        text = sb.toString()

        // 2. Normalize ZWNJ, zero-width spaces, BOM, Arabic Yeh/Kaf
        text = text.replace('\u200C', ' ') // Zero-Width Non-Joiner
            .replace('\u200B', ' ') // Zero-Width Space
            .replace('\uFEFF', ' ') // BOM
            .replace('ي', 'ی')
            .replace('ك', 'ک')
            .replace('ة', 'ه')
            .replace(Regex("[\\r\\n]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        return text
    }

    /**
     * Extracts pure numeric digits from a string (e.g. "۱,۲۵۰,۰۰۰ ریال" -> 1250000L)
     */
    fun extractCleanAmount(rawAmountStr: String): Long {
        val normalized = normalize(rawAmountStr)
        val digitsOnly = normalized.replace(Regex("[^0-9]"), "")
        return digitsOnly.toLongOrNull() ?: 0L
    }

    /**
     * Converts ASCII digits to Persian digits for display
     */
    fun toPersianDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            sb.append(ENGLISH_TO_PERSIAN_DIGITS[ch] ?: ch)
        }
        return sb.toString()
    }
}
