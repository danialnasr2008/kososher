package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.CurrencyDisplayMode
import com.example.data.*
import com.example.sms.BankSmsEngine
import com.example.sms.SampleSmsData
import com.example.sms.TransactionType
import com.example.tts.OfflineAudioReaderManager
import android.content.Context
import com.example.ui.theme.ColorPaletteOption
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("arvand_accounting_prefs", Context.MODE_PRIVATE)

    private val db = AppDatabase.getDatabase(application)
    private val bankDao = db.bankDao()
    private val personalDao = db.personalFinanceDao()
    private val smeDao = db.smeDao()
    private val corpDao = db.corporateDao()
    private val articleDao = db.articleDao()

    val smsEngine = BankSmsEngine()
    val audioReader = OfflineAudioReaderManager(application)

    // Material 3 Expressive & Theme Preferences
    private val _themeMode = MutableStateFlow(
        try {
            ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name)
        } catch (e: Exception) {
            ThemeMode.DARK
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _colorPalette = MutableStateFlow(
        try {
            ColorPaletteOption.valueOf(prefs.getString("color_palette", ColorPaletteOption.EMERALD.name) ?: ColorPaletteOption.EMERALD.name)
        } catch (e: Exception) {
            ColorPaletteOption.EMERALD
        }
    )
    val colorPalette: StateFlow<ColorPaletteOption> = _colorPalette.asStateFlow()

    private val _dynamicColor = MutableStateFlow(prefs.getBoolean("dynamic_color", false))
    val dynamicColor: StateFlow<Boolean> = _dynamicColor.asStateFlow()

    // UI Preferences
    private val _isPrivacyMasked = MutableStateFlow(false)
    val isPrivacyMasked: StateFlow<Boolean> = _isPrivacyMasked.asStateFlow()

    private val _currencyMode = MutableStateFlow(CurrencyDisplayMode.TOMAN)
    val currencyMode: StateFlow<CurrencyDisplayMode> = _currencyMode.asStateFlow()

    // 1. Bank Accounts & Transactions
    val bankCards: StateFlow<List<BankCardEntity>> = bankDao.getAllCardsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<BankTransactionEntity>> = bankDao.getAllTransactionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val combinedTotalRial: StateFlow<Long> = bankDao.getCombinedTotalRialFlow()
        .map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // 2. Personal Finance
    val budgets: StateFlow<List<BudgetEntity>> = personalDao.getAllBudgetsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cheques: StateFlow<List<ChequeEntity>> = personalDao.getAllChequesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanEntity>> = personalDao.getAllLoansFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savingsGoals: StateFlow<List<SavingsGoalEntity>> = personalDao.getAllGoalsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 3. SME & Freelancers
    val contacts: StateFlow<List<ContactEntity>> = smeDao.getAllContactsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<InvoiceEntity>> = smeDao.getAllInvoicesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryItems: StateFlow<List<InventoryItemEntity>> = smeDao.getAllInventoryItemsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pettyCashList: StateFlow<List<PettyCashEntity>> = smeDao.getAllPettyCashFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. Corporate & Double-Entry
    val companies: StateFlow<List<CompanyEntity>> = corpDao.getAllCompaniesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chartOfAccounts: StateFlow<List<AccountNodeEntity>> = corpDao.getAllAccountsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntryEntity>> = corpDao.getAllJournalEntriesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. Articles
    val articles: StateFlow<List<ArticleEntity>> = articleDao.getAllArticlesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Status snackbar message
    private val _statusMessage = MutableSharedFlow<String>()
    val statusMessage: SharedFlow<String> = _statusMessage.asSharedFlow()

    init {
        // Ensure initial data seeded on first run
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.populateInitialData(db)
        }
    }

    fun togglePrivacyMask() {
        _isPrivacyMasked.value = !_isPrivacyMasked.value
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun setColorPalette(palette: ColorPaletteOption) {
        _colorPalette.value = palette
        prefs.edit().putString("color_palette", palette.name).apply()
    }

    fun setDynamicColor(enabled: Boolean) {
        _dynamicColor.value = enabled
        prefs.edit().putBoolean("dynamic_color", enabled).apply()
    }

    fun toggleCurrencyMode() {
        _currencyMode.value = if (_currencyMode.value == CurrencyDisplayMode.TOMAN) {
            CurrencyDisplayMode.RIAL
        } else {
            CurrencyDisplayMode.TOMAN
        }
    }

    fun toggleCardExcluded(card: BankCardEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            bankDao.updateCardExcluded(card.id, !card.isExcludedFromTotal)
        }
    }

    /**
     * Simulates receiving or scanning a bank SMS in real-time
     */
    fun processRawSms(rawText: String, sender: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val parsed = smsEngine.processSms(rawText, sender)
            if (parsed != null) {
                val cardId = "${parsed.bankId}_${parsed.cardOrAccountMask}"
                val existing = bankDao.getCardById(cardId)
                val card = BankCardEntity(
                    id = cardId,
                    bankId = parsed.bankId,
                    bankName = parsed.bankName,
                    cardMask = parsed.cardOrAccountMask,
                    latestBalanceRial = parsed.finalBalanceRial,
                    lastUpdatedTimestamp = parsed.timestamp,
                    isExcludedFromTotal = existing?.isExcludedFromTotal ?: false,
                    displayOrder = existing?.displayOrder ?: 1,
                    colorHex = smsEngine.getBankColor(parsed.bankId)
                )
                bankDao.upsertCard(card)

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
                bankDao.insertTransaction(tx)
                _statusMessage.emit("پیامک ${parsed.bankName} پردازش شد: مانده جدید به‌روزرسانی شد.")
            } else {
                _statusMessage.emit("متن پیامک به عنوان تراکنش بانکی معتبر شناخته نشد.")
            }
        }
    }

    /**
     * One-click importer for realistic Iranian bank SMS test records
     */
    fun importSampleBankSms() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            var count = 0
            for (sample in SampleSmsData.items) {
                val time = now - (sample.minutesAgo * 60 * 1000L)
                val parsed = smsEngine.processSms(sample.text, sample.sender, time)
                if (parsed != null) {
                    val cardId = "${parsed.bankId}_${parsed.cardOrAccountMask}"
                    val existing = bankDao.getCardById(cardId)
                    val card = BankCardEntity(
                        id = cardId,
                        bankId = parsed.bankId,
                        bankName = parsed.bankName,
                        cardMask = parsed.cardOrAccountMask,
                        latestBalanceRial = parsed.finalBalanceRial,
                        lastUpdatedTimestamp = parsed.timestamp,
                        isExcludedFromTotal = existing?.isExcludedFromTotal ?: false,
                        displayOrder = existing?.displayOrder ?: 5,
                        colorHex = smsEngine.getBankColor(parsed.bankId)
                    )
                    bankDao.upsertCard(card)

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
                    bankDao.insertTransaction(tx)
                    count++
                }
            }
            _statusMessage.emit("$count پیامک بانکی با موفقیت پردازش و مانده‌ها به‌روز شدند.")
        }
    }

    /**
     * Exports a bank transaction directly into a double-entry journal voucher (Level 3 integration)
     */
    fun exportTransactionToLedger(tx: BankTransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxVoucher = corpDao.getMaxVoucherNumber() ?: 100
            val nextVoucher = maxVoucher + 1

            val isDeposit = tx.type == "DEPOSIT"
            val voucher = JournalEntryEntity(
                voucherNumber = nextVoucher,
                dateTimestamp = tx.timestamp,
                description = "ثبت خودکار تراکنش بانکی: ${tx.bankName} - ${tx.description}",
                totalDebitRial = tx.amountRial,
                totalCreditRial = tx.amountRial,
                isBalanced = true
            )

            // Double entry debit and credit
            val lines = if (isDeposit) {
                listOf(
                    JournalLineEntity(
                        voucherId = 0,
                        accountCode = "110101",
                        accountName = "بانک و موجودی نقد",
                        debitRial = tx.amountRial,
                        creditRial = 0L,
                        description = tx.description
                    ),
                    JournalLineEntity(
                        voucherId = 0,
                        accountCode = "4101",
                        accountName = "درآمد حاصل از فروش / خدمات",
                        debitRial = 0L,
                        creditRial = tx.amountRial,
                        description = "واریز وجه به حساب"
                    )
                )
            } else {
                listOf(
                    JournalLineEntity(
                        voucherId = 0,
                        accountCode = "5101",
                        accountName = "هزینه‌های جاری / خرید",
                        debitRial = tx.amountRial,
                        creditRial = 0L,
                        description = tx.description
                    ),
                    JournalLineEntity(
                        voucherId = 0,
                        accountCode = "110101",
                        accountName = "بانک و موجودی نقد",
                        debitRial = 0L,
                        creditRial = tx.amountRial,
                        description = "برداشت از حساب بانکی"
                    )
                )
            }

            corpDao.insertCompleteVoucher(voucher, lines)
            bankDao.markTransactionExported(tx.id)
            _statusMessage.emit("سند حسابداری شماره $nextVoucher برای این تراکنش ثبت شد.")
        }
    }

    // Level 1: Personal Finance actions
    fun addBudget(category: String, limitRial: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            personalDao.insertBudget(
                BudgetEntity(
                    categoryName = category,
                    monthlyLimitRial = limitRial,
                    spentRial = 0L
                )
            )
            _statusMessage.emit("بودجه $category ثبت شد.")
        }
    }

    fun addCheque(
        number: String,
        sayadId: String,
        party: String,
        bank: String,
        amount: Long,
        dueTimestamp: Long,
        isIncoming: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            personalDao.insertCheque(
                ChequeEntity(
                    chequeNumber = number,
                    sayadId = sayadId,
                    partyName = party,
                    bankName = bank,
                    amountRial = amount,
                    dueDateTimestamp = dueTimestamp,
                    isIncoming = isIncoming
                )
            )
            _statusMessage.emit("چک صیادی شماره $number ثبت گردید.")
        }
    }

    fun updateChequeStatus(cheque: ChequeEntity, isPassed: Boolean, isBounced: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            personalDao.updateCheque(cheque.copy(isPassed = isPassed, isBounced = isBounced))
            _statusMessage.emit("وضعیت چک به‌روزرسانی شد.")
        }
    }

    fun addLoan(title: String, bank: String, totalAmount: Long, monthlyInstallment: Long, installments: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            personalDao.insertLoan(
                LoanEntity(
                    title = title,
                    bankName = bank,
                    totalAmountRial = totalAmount,
                    monthlyInstallmentRial = monthlyInstallment,
                    totalInstallments = installments,
                    paidInstallments = 0,
                    nextDueTimestamp = now + 30L * 86_400_000L
                )
            )
            _statusMessage.emit("وام $title به لیست اقساط اضافه شد.")
        }
    }

    fun payLoanInstallment(loan: LoanEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (loan.paidInstallments < loan.totalInstallments) {
                val updated = loan.copy(
                    paidInstallments = loan.paidInstallments + 1,
                    nextDueTimestamp = loan.nextDueTimestamp + 30L * 86_400_000L
                )
                personalDao.updateLoan(updated)
                _statusMessage.emit("قسط شماره ${updated.paidInstallments} پرداخت شد.")
            }
        }
    }

    fun addSavingsGoal(title: String, targetAmount: Long, currentAmount: Long, daysToTarget: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            personalDao.insertGoal(
                SavingsGoalEntity(
                    title = title,
                    targetAmountRial = targetAmount,
                    currentAmountRial = currentAmount,
                    deadlineTimestamp = now + (daysToTarget.toLong() * 86_400_000L)
                )
            )
            _statusMessage.emit("هدف پس‌انداز $title ایجاد گردید.")
        }
    }

    fun depositToSavingsGoal(goal: SavingsGoalEntity, addAmount: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            personalDao.updateGoal(goal.copy(currentAmountRial = goal.currentAmountRial + addAmount))
            _statusMessage.emit("مبلغ به صندوق پس‌انداز اضافه شد.")
        }
    }

    // Level 2: SME Actions
    fun addContact(name: String, company: String, phone: String, creditLimit: Long, isCustomer: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            smeDao.insertContact(
                ContactEntity(
                    name = name,
                    company = company,
                    phone = phone,
                    creditLimitRial = creditLimit,
                    isCustomer = isCustomer,
                    isSupplier = !isCustomer
                )
            )
            _statusMessage.emit("طرف‌حساب $name افزوده شد.")
        }
    }

    fun addInvoice(
        contactName: String,
        subtotal: Long,
        discount: Long,
        vatPercent: Int = 10,
        isProforma: Boolean,
        itemsSummary: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val afterDiscount = (subtotal - discount).coerceAtLeast(0L)
            val vat = (afterDiscount * vatPercent) / 100L
            val total = afterDiscount + vat
            val count = (100..999).random()
            val invNumber = (if (isProforma) "PI-" else "INV-") + "1405-$count"

            smeDao.insertInvoice(
                InvoiceEntity(
                    invoiceNumber = invNumber,
                    contactName = contactName,
                    issueDateTimestamp = System.currentTimeMillis(),
                    subtotalRial = subtotal,
                    discountRial = discount,
                    vatTaxRial = vat,
                    totalRial = total,
                    isProforma = isProforma,
                    itemsSummary = itemsSummary
                )
            )
            _statusMessage.emit("فاکتور $invNumber با ارزش افزوده صادر گردید.")
        }
    }

    fun markInvoicePaid(invoice: InvoiceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            smeDao.updateInvoice(invoice.copy(isPaid = true))
            _statusMessage.emit("فاکتور ${invoice.invoiceNumber} تسویه شد.")
        }
    }

    fun addInventoryItem(name: String, sku: String, unit: String, price: Long, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            smeDao.insertInventoryItem(
                InventoryItemEntity(
                    name = name,
                    sku = sku,
                    unit = unit,
                    unitPriceRial = price,
                    purchasePriceRial = (price * 0.75).toLong(),
                    quantity = quantity
                )
            )
            _statusMessage.emit("کالای $name به انبار اضافه شد.")
        }
    }

    // Level 3: Corporate Double-Entry
    fun addJournalEntry(
        description: String,
        debitAccountCode: String,
        debitAccountName: String,
        creditAccountCode: String,
        creditAccountName: String,
        amountRial: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxVoucher = corpDao.getMaxVoucherNumber() ?: 100
            val nextVoucher = maxVoucher + 1

            val voucher = JournalEntryEntity(
                voucherNumber = nextVoucher,
                dateTimestamp = System.currentTimeMillis(),
                description = description,
                totalDebitRial = amountRial,
                totalCreditRial = amountRial,
                isBalanced = true
            )

            val lines = listOf(
                JournalLineEntity(
                    voucherId = 0,
                    accountCode = debitAccountCode,
                    accountName = debitAccountName,
                    debitRial = amountRial,
                    creditRial = 0L,
                    description = description
                ),
                JournalLineEntity(
                    voucherId = 0,
                    accountCode = creditAccountCode,
                    accountName = creditAccountName,
                    debitRial = 0L,
                    creditRial = amountRial,
                    description = description
                )
            )

            corpDao.insertCompleteVoucher(voucher, lines)
            _statusMessage.emit("سند دوطرفه موازنه شده شماره $nextVoucher ثبت شد.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioReader.shutdown()
    }
}
