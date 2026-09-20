package com.example.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.data.AppDatabase
import com.example.data.BankCardEntity
import com.example.data.BankTransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    private val engine = BankSmsEngine()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        val sb = StringBuilder()
        var sender = ""
        var timestamp = System.currentTimeMillis()

        for (sms in messages) {
            sender = sms.displayOriginatingAddress ?: ""
            sb.append(sms.displayMessageBody)
            timestamp = sms.timestampMillis
        }

        val fullBody = sb.toString()
        val parsed = engine.processSms(fullBody, sender, timestamp) ?: return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val cardId = "${parsed.bankId}_${parsed.cardOrAccountMask}"

                val existingCard = db.bankDao().getCardById(cardId)
                val isExcluded = existingCard?.isExcludedFromTotal ?: false
                val displayOrder = existingCard?.displayOrder ?: 10

                val card = BankCardEntity(
                    id = cardId,
                    bankId = parsed.bankId,
                    bankName = parsed.bankName,
                    cardMask = parsed.cardOrAccountMask,
                    latestBalanceRial = parsed.finalBalanceRial,
                    lastUpdatedTimestamp = parsed.timestamp,
                    isExcludedFromTotal = isExcluded,
                    displayOrder = displayOrder,
                    colorHex = engine.getBankColor(parsed.bankId)
                )
                db.bankDao().upsertCard(card)

                val tx = BankTransactionEntity(
                    cardId = cardId,
                    bankName = parsed.bankName,
                    type = parsed.transactionType.name,
                    amountRial = parsed.amountRial,
                    balanceAfterRial = parsed.finalBalanceRial,
                    timestamp = parsed.timestamp,
                    rawSms = parsed.rawBody,
                    description = parsed.description,
                    category = if (parsed.transactionType == TransactionType.DEPOSIT) "واریز بانکی" else "برداشت / خرید"
                )
                db.bankDao().insertTransaction(tx)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
