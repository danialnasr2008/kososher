package com.example.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class CurrencyDisplayMode {
    TOMAN,
    RIAL
}

object CurrencyFormatter {

    /**
     * Formats an amount in Rials into formatted string with option for Toman conversion and privacy masking.
     */
    fun formatRials(
        amountRial: Long,
        mode: CurrencyDisplayMode = CurrencyDisplayMode.TOMAN,
        isMasked: Boolean = false,
        usePersianDigits: Boolean = true
    ): String {
        if (isMasked) {
            return "••••••••"
        }

        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
        }
        val formatter = DecimalFormat("#,###", symbols)

        val (displayAmount, unitLabel) = when (mode) {
            CurrencyDisplayMode.TOMAN -> {
                val toman = amountRial / 10L
                val remainder = amountRial % 10L
                val text = if (remainder != 0L) {
                    formatter.format(amountRial / 10.0)
                } else {
                    formatter.format(toman)
                }
                text to "تومان"
            }
            CurrencyDisplayMode.RIAL -> {
                formatter.format(amountRial) to "ریال"
            }
        }

        val result = if (usePersianDigits) {
            PersianTextNormalizer.toPersianDigits(displayAmount) + " " + unitLabel
        } else {
            "$displayAmount $unitLabel"
        }

        return result
    }

    fun formatNumber(number: Long, usePersianDigits: Boolean = true): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
        }
        val formatter = DecimalFormat("#,###", symbols)
        val formatted = formatter.format(number)
        return if (usePersianDigits) PersianTextNormalizer.toPersianDigits(formatted) else formatted
    }
}
