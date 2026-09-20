package com.example.sms

data class SampleSmsItem(
    val sender: String,
    val text: String,
    val minutesAgo: Long
)

object SampleSmsData {
    val items = listOf(
        SampleSmsItem(
            sender = "blubank",
            text = "واریز 4,500,000 ریال به حساب بلوبانک از طرف دانیال نصر مانده: 38,250,000 ریال",
            minutesAgo = 15
        ),
        SampleSmsItem(
            sender = "9820000",
            text = "برداشت: 850,000 ریال از حساب 6037***4512 بانک ملی ایران مانده: 14,320,000 ریال",
            minutesAgo = 60
        ),
        SampleSmsItem(
            sender = "mellat",
            text = "خرید 1,200,000 ریال پایانه فروشگاهی کارت 6104***9820 بانک ملت موجودی: 27,800,000 ریال",
            minutesAgo = 180
        ),
        SampleSmsItem(
            sender = "rqbank",
            text = "واریز پایا 15,000,000 ریال به حساب 5041***3341 قرض الحسنه رسالت مانده: 62,400,000 ریال",
            minutesAgo = 360
        ),
        SampleSmsItem(
            sender = "bpi.ir",
            text = "برداشت 3,400,000 ریال انتقال ساتنا بانک پاسارگاد کارت 5022***1190 مانده: 18,900,000 ریال",
            minutesAgo = 720
        ),
        SampleSmsItem(
            sender = "tejaratbank",
            text = "واریز: 6,000,000 ریال انتقال کارت به کارت بانک تجارت مانده: 45,100,000 ریال",
            minutesAgo = 1440
        ),
        SampleSmsItem(
            sender = "blubank",
            text = "خرید اینترنتی 750,000 ریال بلوبانک دیجی‌کالا مانده: 37,500,000 ریال",
            minutesAgo = 2880
        )
    )
}
