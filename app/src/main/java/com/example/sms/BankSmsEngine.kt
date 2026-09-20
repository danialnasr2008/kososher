package com.example.sms

import com.example.common.PersianTextNormalizer

class BankSmsEngine(
    private val parsers: List<BankParser> = listOf(
        BluBankParser(),
        MelliBankParser(),
        MellatBankParser(),
        SaderatBankParser(),
        ResalatBankParser(),
        PasargadBankParser(),
        TejaratBankParser(),
        GenericIranianBankParser()
    )
) {
    fun processSms(rawBody: String, sender: String = "", timestamp: Long = System.currentTimeMillis()): ParsedBankSms? {
        val normalized = PersianTextNormalizer.normalize(rawBody)

        for (parser in parsers) {
            if (parser.matches(normalized, sender)) {
                val result = parser.parse(normalized, timestamp, rawBody)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    fun getBankColor(bankId: String): String {
        return parsers.firstOrNull { it.bankId == bankId }?.colorHex ?: "#3B82F6"
    }
}
