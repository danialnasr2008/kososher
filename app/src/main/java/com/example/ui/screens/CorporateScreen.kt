package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.CurrencyDisplayMode
import com.example.common.CurrencyFormatter
import com.example.common.PersianTextNormalizer
import com.example.data.*
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AddJournalEntryDialog
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorporateScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("دفتر روزنامه و اسناد", "کدینگ ۴ سطحی حساب‌ها", "تراز آزمایشی و P&L")

    val companies by viewModel.companies.collectAsState()
    val accounts by viewModel.chartOfAccounts.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val isMasked by viewModel.isPrivacyMasked.collectAsState()
    val currencyMode by viewModel.currencyMode.collectAsState()

    var showAddVoucherDialog by remember { mutableStateOf(false) }

    if (showAddVoucherDialog) {
        AddJournalEntryDialog(
            accounts = accounts,
            onDismiss = { showAddVoucherDialog = false },
            onConfirm = { desc, debitCode, debitName, creditCode, creditName, amount ->
                viewModel.addJournalEntry(desc, debitCode, debitName, creditCode, creditName, amount)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Corporate Multi-Tenant Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(22.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        companies.firstOrNull()?.name ?: "شرکت توسعه سیستم (سهامی خاص)",
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1
                    )
                    Text(
                        "سال مالی ${companies.firstOrNull()?.fiscalYear ?: "1405"} | سیستم دفاتر قانونی و حسابداری دوبل",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Badge(
                    containerColor = IncomeGreen.copy(alpha = 0.2f)
                ) {
                    Text("چندشرکته", color = IncomeGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> JournalVouchersTabContent(
                entries = journalEntries,
                currencyMode = currencyMode,
                isMasked = isMasked,
                onAddVoucher = { showAddVoucherDialog = true }
            )
            1 -> ChartOfAccountsTabContent(
                accounts = accounts,
                currencyMode = currencyMode,
                isMasked = isMasked
            )
            2 -> TrialBalanceTabContent(
                accounts = accounts,
                entries = journalEntries,
                currencyMode = currencyMode,
                isMasked = isMasked
            )
        }
    }
}

@Composable
fun JournalVouchersTabContent(
    entries: List<JournalEntryEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean,
    onAddVoucher: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("دفتر روزنامه و اسناد موازنه شده", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Button(onClick = onAddVoucher, shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ثبت سند دوبل")
                }
            }
        }

        items(entries, key = { it.id }) { voucher ->
            val dateStr = remember(voucher.dateTimestamp) {
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                PersianTextNormalizer.toPersianDigits(sdf.format(Date(voucher.dateTimestamp)))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                Text(
                                    "سند #${PersianTextNormalizer.toPersianDigits(voucher.voucherNumber.toString())}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Text(dateStr, style = MaterialTheme.typography.bodySmall)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                            Text("تراز (موازنه)", color = IncomeGreen, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(voucher.description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "بدهکار: ${CurrencyFormatter.formatRials(voucher.totalDebitRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = IncomeGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "بستانکار: ${CurrencyFormatter.formatRials(voucher.totalCreditRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChartOfAccountsTabContent(
    accounts: List<AccountNodeEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text("کدینگ استاندارد حساب‌ها (گروه -> کل -> معین -> تفصیلی)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(accounts, key = { it.code }) { acc ->
            val indentPadding = when (acc.level) {
                AccountLevel.GROUP -> 0.dp
                AccountLevel.TOTAL -> 14.dp
                AccountLevel.SUBSIDIARY -> 28.dp
                AccountLevel.DETAIL -> 42.dp
            }

            val levelBadgeColor = when (acc.level) {
                AccountLevel.GROUP -> Color(0xFF6366F1)
                AccountLevel.TOTAL -> Color(0xFF0EA5E9)
                AccountLevel.SUBSIDIARY -> Color(0xFF10B981)
                AccountLevel.DETAIL -> Color(0xFFF59E0B)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = indentPadding),
                shape = RoundedCornerShape(10.dp),
                color = if (acc.level == AccountLevel.GROUP) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Badge(containerColor = levelBadgeColor.copy(alpha = 0.2f)) {
                            Text(
                                PersianTextNormalizer.toPersianDigits(acc.code),
                                color = levelBadgeColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                        Text(
                            acc.name,
                            fontWeight = if (acc.level == AccountLevel.GROUP || acc.level == AccountLevel.TOTAL) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (acc.balanceRial > 0) {
                        Text(
                            CurrencyFormatter.formatRials(acc.balanceRial, currencyMode, isMasked),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            if (acc.nature == AccountNature.DEBIT) "ماهیت: بدهکار" else "ماهیت: بستانکار",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrialBalanceTabContent(
    accounts: List<AccountNodeEntity>,
    entries: List<JournalEntryEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean
) {
    val totalDebit = remember(entries) { entries.sumOf { it.totalDebitRial } }
    val totalCredit = remember(entries) { entries.sumOf { it.totalCreditRial } }

    val revenues = remember(accounts) {
        accounts.filter { it.code.startsWith("4") }.sumOf { it.balanceRial }
    }
    val expenses = remember(accounts) {
        accounts.filter { it.code.startsWith("5") }.sumOf { it.balanceRial }
    }
    val netProfit = revenues - expenses

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text("گزارش تراز آزمایشی و صورت سود و زیان (P&L)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        // Trial Balance Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("تراز آزمایشی کل دفاتر", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مجموع گردش بدهکار:")
                        Text(CurrencyFormatter.formatRials(totalDebit, currencyMode, isMasked), fontWeight = FontWeight.Bold, color = IncomeGreen)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مجموع گردش بستانکار:")
                        Text(CurrencyFormatter.formatRials(totalCredit, currencyMode, isMasked), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("اختلاف تراز:")
                        val diff = totalDebit - totalCredit
                        if (diff == 0L) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                                Text("تراز کامل (اختلاف صفر)", color = IncomeGreen, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(CurrencyFormatter.formatRials(diff, currencyMode, isMasked), color = ExpenseRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Profit & Loss (P&L) Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("صورت سود و زیان دوره مالی", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("کل درآمدهای عملیاتی:")
                        Text(CurrencyFormatter.formatRials(revenues, currencyMode, isMasked), fontWeight = FontWeight.Bold, color = IncomeGreen)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("کل هزینه‌های جاری و حقوق:")
                        Text(CurrencyFormatter.formatRials(expenses, currencyMode, isMasked), fontWeight = FontWeight.Bold, color = ExpenseRed)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("سود خالص دوره (P&L):", fontWeight = FontWeight.Bold)
                        Text(
                            CurrencyFormatter.formatRials(netProfit, currencyMode, isMasked),
                            fontWeight = FontWeight.ExtraBold,
                            color = if (netProfit >= 0) IncomeGreen else ExpenseRed
                        )
                    }
                }
            }
        }
    }
}
