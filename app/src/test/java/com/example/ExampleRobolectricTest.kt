package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.common.CurrencyDisplayMode
import com.example.common.CurrencyFormatter
import com.example.common.PersianTextNormalizer
import com.example.sms.BankSmsEngine
import com.example.sms.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Balance & Finance", appName)
    }

    @Test
    fun `persian text normalizer converts digits correctly`() {
        val mixed = "واریز ۱,۲۵۰,۰۰۰ ریال به حساب ۶۰۳۷"
        val normalized = PersianTextNormalizer.toLatinDigits(mixed)
        assertEquals("واریز 1,250,000 ریال به حساب 6037", normalized)

        val toPersian = PersianTextNormalizer.toPersianDigits("50000")
        assertEquals("۵۰۰۰۰", toPersian)
    }

    @Test
    fun `currency formatter formats rial and toman correctly`() {
        val amountRial = 50_000_000L
        val formattedRials = CurrencyFormatter.formatRials(amountRial, CurrencyDisplayMode.RIAL, isMasked = false)
        assertEquals("۵۰,۰۰۰,۰۰۰ ریال", formattedRials)

        val formattedTomans = CurrencyFormatter.formatRials(amountRial, CurrencyDisplayMode.TOMAN, isMasked = false)
        assertEquals("۵,۰۰۰,۰۰۰ تومان", formattedTomans)

        val masked = CurrencyFormatter.formatRials(amountRial, CurrencyDisplayMode.TOMAN, isMasked = true)
        assertEquals("••••••••", masked)
    }

    @Test
    fun `sms engine parses blu and melli bank messages`() {
        val engine = BankSmsEngine()

        val bluSms = "واریز به حساب\nمبلغ: 3,500,000 ریال\nاز: شرکت توسعه نصر\nمانده: 41,200,000 ریال\nبلوبانک"
        val bluParsed = engine.processSms(bluSms, "BLU")
        assertNotNull(bluParsed)
        assertEquals("بلوبانک (سامان)", bluParsed!!.bankName)
        assertEquals(3_500_000L, bluParsed.amountRial)
        assertEquals(41_200_000L, bluParsed.finalBalanceRial)
        assertEquals(TransactionType.DEPOSIT, bluParsed.transactionType)

        val melliSms = "بانک ملی ایران\nبرداشت: 1,500,000 ریال\nاز حساب 6037***1234\nمانده: 18,200,000 ریال"
        val melliParsed = engine.processSms(melliSms, "BMI")
        assertNotNull(melliParsed)
        assertEquals("بانک ملی ایران", melliParsed!!.bankName)
        assertEquals(1_500_000L, melliParsed.amountRial)
        assertEquals(18_200_000L, melliParsed.finalBalanceRial)
        assertEquals(TransactionType.WITHDRAWAL, melliParsed.transactionType)
    }
}
