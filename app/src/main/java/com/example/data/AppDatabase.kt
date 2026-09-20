package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BankCardEntity::class,
        BankTransactionEntity::class,
        BudgetEntity::class,
        ChequeEntity::class,
        LoanEntity::class,
        SavingsGoalEntity::class,
        ContactEntity::class,
        InvoiceEntity::class,
        InventoryItemEntity::class,
        PettyCashEntity::class,
        CompanyEntity::class,
        AccountNodeEntity::class,
        JournalEntryEntity::class,
        JournalLineEntity::class,
        ArticleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bankDao(): BankDao
    abstract fun personalFinanceDao(): PersonalFinanceDao
    abstract fun smeDao(): SmeDao
    abstract fun corporateDao(): CorporateDao
    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "balance_finance_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val now = System.currentTimeMillis()
            val dayMs = 86_400_000L

            // 1. Initial Bank Cards & Transactions (Balance Engine Baseline)
            val bankDao = database.bankDao()
            val cards = listOf(
                BankCardEntity(
                    id = "blu_Blu",
                    bankId = "blu",
                    bankName = "بلوبانک (سامان)",
                    cardMask = "Blu",
                    latestBalanceRial = 38_250_000L,
                    lastUpdatedTimestamp = now - 15 * 60 * 1000L,
                    colorHex = "#0EA5E9",
                    displayOrder = 1
                ),
                BankCardEntity(
                    id = "melli_4512",
                    bankId = "melli",
                    bankName = "بانک ملی ایران",
                    cardMask = "4512",
                    latestBalanceRial = 14_320_000L,
                    lastUpdatedTimestamp = now - 60 * 60 * 1000L,
                    colorHex = "#1E3A8A",
                    displayOrder = 2
                ),
                BankCardEntity(
                    id = "mellat_9820",
                    bankId = "mellat",
                    bankName = "بانک ملت",
                    cardMask = "9820",
                    latestBalanceRial = 27_800_000L,
                    lastUpdatedTimestamp = now - 3 * 3600 * 1000L,
                    colorHex = "#BE123C",
                    displayOrder = 3
                ),
                BankCardEntity(
                    id = "resalat_3341",
                    bankId = "resalat",
                    bankName = "بانک قرض‌الحسنه رسالت",
                    cardMask = "3341",
                    latestBalanceRial = 62_400_000L,
                    lastUpdatedTimestamp = now - 6 * 3600 * 1000L,
                    colorHex = "#059669",
                    displayOrder = 4
                )
            )
            cards.forEach { bankDao.upsertCard(it) }

            val initialTx = listOf(
                BankTransactionEntity(
                    cardId = "blu_Blu",
                    bankName = "بلوبانک (سامان)",
                    type = "DEPOSIT",
                    amountRial = 4_500_000L,
                    balanceAfterRial = 38_250_000L,
                    timestamp = now - 15 * 60 * 1000L,
                    rawSms = "واریز 4,500,000 ریال به حساب بلوبانک از طرف دانیال نصر مانده: 38,250,000 ریال",
                    description = "واریز بلوبانک",
                    category = "درآمد / دستمزد"
                ),
                BankTransactionEntity(
                    cardId = "melli_4512",
                    bankName = "بانک ملی ایران",
                    type = "WITHDRAWAL",
                    amountRial = 850_000L,
                    balanceAfterRial = 14_320_000L,
                    timestamp = now - 60 * 60 * 1000L,
                    rawSms = "برداشت: 850,000 ریال از حساب 6037***4512 بانک ملی ایران مانده: 14,320,000 ریال",
                    description = "برداشت بانک ملی",
                    category = "خوراک و خرید روزمره"
                ),
                BankTransactionEntity(
                    cardId = "mellat_9820",
                    bankName = "بانک ملت",
                    type = "WITHDRAWAL",
                    amountRial = 1_200_000L,
                    balanceAfterRial = 27_800_000L,
                    timestamp = now - 3 * 3600 * 1000L,
                    rawSms = "خرید 1,200,000 ریال پایانه فروشگاهی کارت 6104***9820 بانک ملت موجودی: 27,800,000 ریال",
                    description = "خرید پایانه بانک ملت",
                    category = "کافی‌شاپ و رستوران"
                ),
                BankTransactionEntity(
                    cardId = "resalat_3341",
                    bankName = "بانک قرض‌الحسنه رسالت",
                    type = "DEPOSIT",
                    amountRial = 15_000_000L,
                    balanceAfterRial = 62_400_000L,
                    timestamp = now - 6 * 3600 * 1000L,
                    rawSms = "واریز پایا 15,000,000 ریال به حساب 5041***3341 قرض الحسنه رسالت مانده: 62,400,000 ریال",
                    description = "واریز پایا رسالت",
                    category = "پروژه و فریلنسری"
                )
            )
            initialTx.forEach { bankDao.insertTransaction(it) }

            // 2. Personal Finance Initial Data
            val personalDao = database.personalFinanceDao()
            val budgets = listOf(
                BudgetEntity(categoryName = "خوراک و سوپرمارکت", monthlyLimitRial = 45_000_000L, spentRial = 32_500_000L, iconName = "restaurant"),
                BudgetEntity(categoryName = "اجاره و قبوض مسکن", monthlyLimitRial = 90_000_000L, spentRial = 90_000_000L, iconName = "home"),
                BudgetEntity(categoryName = "حمل و نقل و سوخت", monthlyLimitRial = 20_000_000L, spentRial = 18_400_000L, iconName = "directions_car"),
                BudgetEntity(categoryName = "پوشاک و تجهیزات", monthlyLimitRial = 30_000_000L, spentRial = 12_000_000L, iconName = "shopping_bag")
            )
            budgets.forEach { personalDao.insertBudget(it) }

            val cheques = listOf(
                ChequeEntity(
                    chequeNumber = "481029",
                    sayadId = "7820194850192841",
                    partyName = "شرکت پایا فناوری نوین",
                    bankName = "بانک ملت",
                    amountRial = 85_000_000L,
                    dueDateTimestamp = now + 4 * dayMs,
                    isIncoming = true,
                    notes = "بابت تسویه فاز اول پروژه پورتال"
                ),
                ChequeEntity(
                    chequeNumber = "201934",
                    sayadId = "9012384719203918",
                    partyName = "امیرحسین رضایی (تامین قطعات)",
                    bankName = "بانک ملی",
                    amountRial = 42_000_000L,
                    dueDateTimestamp = now + 12 * dayMs,
                    isIncoming = false,
                    notes = "خرید سرور و رک شبکه"
                )
            )
            cheques.forEach { personalDao.insertCheque(it) }

            val loans = listOf(
                LoanEntity(
                    title = "تسهیلات خرید کالا و مسکن",
                    bankName = "بانک رسالت",
                    totalAmountRial = 300_000_000L,
                    monthlyInstallmentRial = 11_200_000L,
                    totalInstallments = 36,
                    paidInstallments = 14,
                    nextDueTimestamp = now + 6 * dayMs,
                    interestRatePercent = 4.0
                ),
                LoanEntity(
                    title = "وام تجهیز دفتر کار و فناوری",
                    bankName = "بانک ملی",
                    totalAmountRial = 500_000_000L,
                    monthlyInstallmentRial = 19_500_000L,
                    totalInstallments = 24,
                    paidInstallments = 8,
                    nextDueTimestamp = now + 18 * dayMs,
                    interestRatePercent = 18.0
                )
            )
            loans.forEach { personalDao.insertLoan(it) }

            val goals = listOf(
                SavingsGoalEntity(
                    title = "صندوق اضطراری ۶ ماهه",
                    targetAmountRial = 250_000_000L,
                    currentAmountRial = 185_000_000L,
                    deadlineTimestamp = now + 90 * dayMs,
                    colorHex = "#10B981"
                ),
                SavingsGoalEntity(
                    title = "ارتقای تجهیزات کامپیوتری",
                    targetAmountRial = 120_000_000L,
                    currentAmountRial = 72_000_000L,
                    deadlineTimestamp = now + 45 * dayMs,
                    colorHex = "#0EA5E9"
                )
            )
            goals.forEach { personalDao.insertGoal(it) }

            // 3. SME & Freelancer Initial Data
            val smeDao = database.smeDao()
            val contacts = listOf(
                ContactEntity(
                    name = "مهندس سهراب پوریا",
                    company = "شرکت دانش‌بنیان افق",
                    phone = "09121234567",
                    balanceRial = 65_000_000L,
                    creditLimitRial = 200_000_000L,
                    isCustomer = true
                ),
                ContactEntity(
                    name = "فروشگاه مرکزی شبکه آریا",
                    company = "بازرگانی آریا سیستم",
                    phone = "02188776655",
                    balanceRial = -28_000_000L, // We owe them
                    creditLimitRial = 150_000_000L,
                    isSupplier = true
                )
            )
            contacts.forEach { smeDao.insertContact(it) }

            val invoices = listOf(
                InvoiceEntity(
                    invoiceNumber = "INV-1405-104",
                    contactName = "شرکت دانش‌بنیان افق",
                    contactPhone = "09121234567",
                    issueDateTimestamp = now - 2 * dayMs,
                    subtotalRial = 100_000_000L,
                    discountRial = 5_000_000L,
                    vatTaxRial = 9_500_000L, // 10% on 95m
                    totalRial = 104_500_000L,
                    isProforma = false,
                    isPaid = false,
                    itemsSummary = "توسعه ماژول حسابداری بومی + پیکربندی پایگاه‌داده"
                ),
                InvoiceEntity(
                    invoiceNumber = "PI-1405-021",
                    contactName = "استارتاپ آوانگار",
                    contactPhone = "09355554433",
                    issueDateTimestamp = now - 1 * dayMs,
                    subtotalRial = 45_000_000L,
                    discountRial = 0L,
                    vatTaxRial = 4_500_000L,
                    totalRial = 49_500_000L,
                    isProforma = true,
                    isPaid = false,
                    itemsSummary = "مشاوره زیرساخت لوکال‌فرست و رمزنگاری داده"
                )
            )
            invoices.forEach { smeDao.insertInvoice(it) }

            val inventory = listOf(
                InventoryItemEntity(sku = "PRD-101", name = "لایسنس نرم‌افزار سازمانی", unit = "کاربر", unitPriceRial = 18_000_000L, purchasePriceRial = 12_000_000L, quantity = 25, reorderPoint = 5),
                InventoryItemEntity(sku = "HW-204", name = "توکن امنیتی سخت‌افزاری", unit = "دستگاه", unitPriceRial = 6_500_000L, purchasePriceRial = 4_800_000L, quantity = 3, reorderPoint = 5),
                InventoryItemEntity(sku = "SRV-301", name = "پکیج پشتیبانی طلایی یکساله", unit = "قرارداد", unitPriceRial = 45_000_000L, purchasePriceRial = 20_000_000L, quantity = 12, reorderPoint = 2)
            )
            inventory.forEach { smeDao.insertInventoryItem(it) }

            val pettyCash = listOf(
                PettyCashEntity(fundName = "صندوق تنخواه جاری دفتر مرکزی", custodianName = "مهرداد ناصری", initialAmountRial = 20_000_000L, currentAmountRial = 14_350_000L, lastSettledTimestamp = now - 5 * dayMs)
            )
            pettyCash.forEach { smeDao.insertPettyCash(it) }

            // 4. Corporate & Double-Entry Accounting Initial Data
            val corpDao = database.corporateDao()
            val companyId = corpDao.insertCompany(
                CompanyEntity(name = "توسعه نرم‌افزار نصر سیستم (سهامی خاص)", nationalId = "10103456789", fiscalYear = "1405", isSelected = true)
            )

            // Standard Iranian 4-level Chart of Accounts
            val standardAccounts = listOf(
                // گروه ۱: دارایی‌ها
                AccountNodeEntity(code = "1", name = "دارایی‌ها", level = AccountLevel.GROUP, parentCode = "", nature = AccountNature.DEBIT),
                AccountNodeEntity(code = "11", name = "دارایی‌های جاری", level = AccountLevel.TOTAL, parentCode = "1", nature = AccountNature.DEBIT),
                AccountNodeEntity(code = "1101", name = "موجودی نقد و بانک", level = AccountLevel.SUBSIDIARY, parentCode = "11", nature = AccountNature.DEBIT),
                AccountNodeEntity(code = "110101", name = "بانک ملی - حساب جاری 6037", level = AccountLevel.DETAIL, parentCode = "1101", nature = AccountNature.DEBIT, balanceRial = 14_320_000L),
                AccountNodeEntity(code = "110102", name = "بلوبانک سامان", level = AccountLevel.DETAIL, parentCode = "1101", nature = AccountNature.DEBIT, balanceRial = 38_250_000L),
                AccountNodeEntity(code = "1102", name = "اسناد و حساب‌های دریافتنی", level = AccountLevel.SUBSIDIARY, parentCode = "11", nature = AccountNature.DEBIT),
                AccountNodeEntity(code = "110201", name = "مشتریان تجاری", level = AccountLevel.DETAIL, parentCode = "1102", nature = AccountNature.DEBIT, balanceRial = 65_000_000L),

                // گروه ۲: بدهی‌ها
                AccountNodeEntity(code = "2", name = "بدهی‌ها", level = AccountLevel.GROUP, parentCode = "", nature = AccountNature.CREDIT),
                AccountNodeEntity(code = "21", name = "بدهی‌های جاری", level = AccountLevel.TOTAL, parentCode = "2", nature = AccountNature.CREDIT),
                AccountNodeEntity(code = "2101", name = "اسناد و حساب‌های پرداختنی", level = AccountLevel.SUBSIDIARY, parentCode = "21", nature = AccountNature.CREDIT),
                AccountNodeEntity(code = "210101", name = "بستانکاران تجاری و تامین‌کنندگان", level = AccountLevel.DETAIL, parentCode = "2101", nature = AccountNature.CREDIT, balanceRial = 28_000_000L),
                AccountNodeEntity(code = "2102", name = "تسهیلات مالی دریافتی", level = AccountLevel.SUBSIDIARY, parentCode = "21", nature = AccountNature.CREDIT, balanceRial = 420_000_000L),

                // گروه ۳: حقوق صاحبان سهام / سرمایه
                AccountNodeEntity(code = "3", name = "حقوق صاحبان سرمایه", level = AccountLevel.GROUP, parentCode = "", nature = AccountNature.CREDIT),
                AccountNodeEntity(code = "31", name = "سرمایه پرداخت شده", level = AccountLevel.TOTAL, parentCode = "3", nature = AccountNature.CREDIT, balanceRial = 200_000_000L),

                // گروه ۴: درآمدها
                AccountNodeEntity(code = "4", name = "درآمدها", level = AccountLevel.GROUP, parentCode = "", nature = AccountNature.CREDIT),
                AccountNodeEntity(code = "41", name = "درآمد حاصل از خدمات نرم‌افزاری", level = AccountLevel.TOTAL, parentCode = "4", nature = AccountNature.CREDIT),
                AccountNodeEntity(code = "4101", name = "فروش پروژه‌ها و لایسنس", level = AccountLevel.SUBSIDIARY, parentCode = "41", nature = AccountNature.CREDIT, balanceRial = 180_000_000L),

                // گروه ۵: هزینه‌ها
                AccountNodeEntity(code = "5", name = "هزینه‌ها", level = AccountLevel.GROUP, parentCode = "", nature = AccountNature.DEBIT),
                AccountNodeEntity(code = "51", name = "هزینه‌های عمومی و اداری", level = AccountLevel.TOTAL, parentCode = "5", nature = AccountNature.DEBIT),
                AccountNodeEntity(code = "5101", name = "هزینه حقوق و دستمزد پرسنل", level = AccountLevel.SUBSIDIARY, parentCode = "51", nature = AccountNature.DEBIT, balanceRial = 95_000_000L),
                AccountNodeEntity(code = "5102", name = "هزینه اینترنت و زیرساخت ابری", level = AccountLevel.SUBSIDIARY, parentCode = "51", nature = AccountNature.DEBIT, balanceRial = 14_500_000L)
            )
            corpDao.insertAccounts(standardAccounts)

            // Balanced Initial Voucher (سند افتتاحیه / سند ثبت تراکنش)
            val voucher1 = JournalEntryEntity(
                voucherNumber = 101,
                dateTimestamp = now - 3 * dayMs,
                description = "سند تسویه فاکتور و واریز به حساب بانک ملت",
                totalDebitRial = 25_000_000L,
                totalCreditRial = 25_000_000L,
                isBalanced = true,
                companyId = companyId
            )
            val voucherLines1 = listOf(
                JournalLineEntity(voucherId = 0, accountCode = "110101", accountName = "بانک ملی - حساب جاری", debitRial = 25_000_000L, creditRial = 0L, description = "دریافت وجه از مشتری"),
                JournalLineEntity(voucherId = 0, accountCode = "110201", accountName = "مشتریان تجاری", debitRial = 0L, creditRial = 25_000_000L, description = "بستانکار شدن حساب مشتری")
            )
            corpDao.insertCompleteVoucher(voucher1, voucherLines1)

            // 5. Articles & Audio Reader Initial Data
            val articleDao = database.articleDao()
            val articles = listOf(
                ArticleEntity(
                    title = "راهنمای جامع مدیریت نقدینگی و تطبیق صورت‌حساب‌های بانکی در حسابداری اروند",
                    category = "مدیریت مالی و خزانه داری",
                    readTimeMinutes = 4,
                    summary = "اصول استخراج دقیق گردش وجوه نقد، کنترل مغایرت‌های بانکی و اعتبارسنجی مانده نهایی حساب‌ها به صورت امن و آفلاین.",
                    content = "مدیریت جریان نقدینگی شریان حیاتی هر بنگاه اقتصادی و زندگی مالی است. در سامانه حسابداری اروند، پردازش مستقیم پیامک‌های رسمی بانکی و استخراج خودکار مانده و شناسه پیگیری، نیاز به ثبت دستی تراکنش‌ها را برطرف می‌سازد. با ثبت بی‌درنگ واریزها و برداشت‌ها و امکان انتقال خودکار به اسناد دفاتر کل، حسابدار و مدیر مالی می‌توانند وضعیت لحظه‌ای نقدینگی و حساب‌های متصل را بدون نیاز به اینترنت و در بالاترین سطح حریم خصوصی نظارت کنند.",
                    publishDate = "۱۴۰۵/۰۱/۱۵"
                ),
                ArticleEntity(
                    title = "راهنمای قانون مالیات بر ارزش افزوده و صدور فاکتور رسمی الکترونیک برای فریلنسرها",
                    category = "قوانین مالیاتی و کسب‌وکار",
                    readTimeMinutes = 5,
                    summary = "نحوه اعمال نرخ قانونی مالیات، کسورات بیمه و ساختار شناسه یکتای صورتحساب سامانه مودیان برای صاحبان کسب‌وکارهای خرد.",
                    content = "صدور پیش‌فاکتور شفاف و صورتحساب رسمی استاندارد، سنگ‌بنای اعتماد میان مشتری و ارائه‌دهنده خدمات است. محاسبه صحیح درصد مالیات بر ارزش افزوده (VAT) بر روی مانده پس از تخفیف انجام می‌شود. با نگهداری دقیق کاردکس کالا و طرف‌های حساب، در پایان هر فصل گزارش اظهارنامه فصلی بدون مغایرت و با استناد به اسناد ثبت‌شده تهیه می‌گردد.",
                    publishDate = "۱۴۰۵/۰۱/۱۰"
                ),
                ArticleEntity(
                    title = "کدینگ ۴ سطحی حسابداری دوبل: از گروه و کل تا معین و تفصیلی شناور",
                    category = "آموزش حسابداری شرکتی",
                    readTimeMinutes = 6,
                    summary = "اصول نگارش دفاتر روزنامه و کل، اعتبارسنجی تراز بدهکار و بستانکار و آماده‌سازی تراز آزمایشی و صورت سود و زیان.",
                    content = "در سیستم حسابداری دوطرفه یا دوبل، هر رویداد مالی دارای حداقل دو اثر هم‌وزن است. بر اساس معادله اساسی حسابداری، دارایی‌ها همواره برابرند با مجموع بدهی‌ها و حقوق صاحبان سهام. کدینگ استاندارد حساب‌ها با اختصاص ارقام سلسله‌مراتبی از گروه (یک رقم)، کل (دو رقم)، معین (چهار رقم) و تفصیلی (شش رقم یا شناور) به حسابدار امکان می‌دهد گزارش‌های تفکیکی از جریان نقدینگی و وضعیت سود و زیان را در هر لحظه استخراج کند.",
                    publishDate = "۱۴۰۵/۰۱/۰۵"
                )
            )
            articleDao.insertArticles(articles)
        }
    }
}
