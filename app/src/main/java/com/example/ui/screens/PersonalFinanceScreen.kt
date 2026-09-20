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
import com.example.ui.dialogs.AddChequeDialog
import com.example.ui.dialogs.AddGoalDialog
import com.example.ui.dialogs.AddLoanDialog
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalFinanceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("بودجه‌بندی", "چک‌های صیادی", "وام و اقساط", "اهداف پس‌انداز")

    val budgets by viewModel.budgets.collectAsState()
    val cheques by viewModel.cheques.collectAsState()
    val loans by viewModel.loans.collectAsState()
    val goals by viewModel.savingsGoals.collectAsState()
    val isMasked by viewModel.isPrivacyMasked.collectAsState()
    val currencyMode by viewModel.currencyMode.collectAsState()

    var showAddChequeDialog by remember { mutableStateOf(false) }
    var showAddLoanDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }

    // Dialogs
    if (showAddChequeDialog) {
        AddChequeDialog(
            onDismiss = { showAddChequeDialog = false },
            onConfirm = { number, sayadId, party, bank, amount, dueDays, isIncoming ->
                val now = System.currentTimeMillis()
                val due = now + (dueDays * 86_400_000L)
                viewModel.addCheque(number, sayadId, party, bank, amount, due, isIncoming)
            }
        )
    }

    if (showAddLoanDialog) {
        AddLoanDialog(
            onDismiss = { showAddLoanDialog = false },
            onConfirm = { title, bank, total, monthly, count ->
                viewModel.addLoan(title, bank, total, monthly, count)
            }
        )
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, target, current, days ->
                viewModel.addSavingsGoal(title, target, current, days)
            }
        )
    }

    if (showAddBudgetDialog) {
        var category by remember { mutableStateOf("پوشاک و تجهیزات") }
        var limitStr by remember { mutableStateOf("25000000") }

        AlertDialog(
            onDismissRequest = { showAddBudgetDialog = false },
            title = { Text("تعریف سقف بودجه جدید", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("عنوان دسته‌بندی") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = limitStr,
                        onValueChange = { limitStr = it },
                        label = { Text("سقف ماهانه (ریال)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = limitStr.replace(",", "").toLongOrNull() ?: 0L
                        if (category.isNotBlank() && limit > 0) {
                            viewModel.addBudget(category, limit)
                            showAddBudgetDialog = false
                        }
                    }
                ) {
                    Text("ثبت بودجه")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBudgetDialog = false }) { Text("انصراف") }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Personal Finance Header Banner
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
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text("مدیریت امور مالی شخصی", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleSmall)
                    Text("بودجه‌بندی دوره‌ای، ثبت چک‌های صیادی و تسهیلات بانکی", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

        // Tab Contents
        when (selectedTab) {
            0 -> BudgetsTabContent(
                budgets = budgets,
                currencyMode = currencyMode,
                isMasked = isMasked,
                onAddBudget = { showAddBudgetDialog = true }
            )
            1 -> ChequesTabContent(
                cheques = cheques,
                currencyMode = currencyMode,
                isMasked = isMasked,
                onAddCheque = { showAddChequeDialog = true },
                onUpdateStatus = { cheque, passed, bounced ->
                    viewModel.updateChequeStatus(cheque, passed, bounced)
                }
            )
            2 -> LoansTabContent(
                loans = loans,
                currencyMode = currencyMode,
                isMasked = isMasked,
                onAddLoan = { showAddLoanDialog = true },
                onPayInstallment = { viewModel.payLoanInstallment(it) }
            )
            3 -> GoalsTabContent(
                goals = goals,
                currencyMode = currencyMode,
                isMasked = isMasked,
                onAddGoal = { showAddGoalDialog = true },
                onDeposit = { goal, amount -> viewModel.depositToSavingsGoal(goal, amount) }
            )
        }
    }
}

@Composable
fun BudgetsTabContent(
    budgets: List<BudgetEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean,
    onAddBudget: () -> Unit
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
                Text("هشدارهای سقف بودجه ماهانه (۷۰٪، ۹۰٪، ۱۰۰٪)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = onAddBudget) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("سقف جدید")
                }
            }
        }

        items(budgets, key = { it.id }) { budget ->
            val ratio = if (budget.monthlyLimitRial > 0) {
                (budget.spentRial.toFloat() / budget.monthlyLimitRial).coerceIn(0f, 1.2f)
            } else 0f

            val progressColor = when {
                ratio >= 1.0f -> ExpenseRed
                ratio >= 0.7f -> GoldAccent
                else -> IncomeGreen
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
                        Text(budget.categoryName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        val percent = (ratio * 100).toInt()
                        Text(
                            "${PersianTextNormalizer.toPersianDigits(percent.toString())}% مصرف شده",
                            color = progressColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    LinearProgressIndicator(
                        progress = { ratio.coerceAtMost(1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "خرج شده: ${CurrencyFormatter.formatRials(budget.spentRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "سقف: ${CurrencyFormatter.formatRials(budget.monthlyLimitRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChequesTabContent(
    cheques: List<ChequeEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean,
    onAddCheque: () -> Unit,
    onUpdateStatus: (cheque: ChequeEntity, passed: Boolean, bounced: Boolean) -> Unit
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
                Text("مدیریت و استعلام چک‌های صیادی", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = onAddCheque) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ثبت چک صیاد")
                }
            }
        }

        items(cheques, key = { it.id }) { ch ->
            val dueFormatted = remember(ch.dueDateTimestamp) {
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                PersianTextNormalizer.toPersianDigits(sdf.format(Date(ch.dueDateTimestamp)))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Badge(
                                containerColor = if (ch.isIncoming) IncomeGreen.copy(alpha = 0.2f) else ExpenseRed.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    if (ch.isIncoming) "دریافتنی" else "پرداختنی",
                                    color = if (ch.isIncoming) IncomeGreen else ExpenseRed,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Text(ch.partyName, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            CurrencyFormatter.formatRials(ch.amountRial, currencyMode, isMasked),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        "شناسه صیاد: ${PersianTextNormalizer.toPersianDigits(ch.sayadId)} | سریال: ${PersianTextNormalizer.toPersianDigits(ch.chequeNumber)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "سررسید: $dueFormatted (${ch.bankName})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (!ch.isPassed && !ch.isBounced) {
                                Button(
                                    onClick = { onUpdateStatus(ch, true, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("پاس شد", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = { onUpdateStatus(ch, false, true) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("برگشت", style = MaterialTheme.typography.labelSmall)
                                }
                            } else if (ch.isPassed) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("پاس شده", color = IncomeGreen) },
                                    leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = IncomeGreen) }
                                )
                            } else {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("برگشت خورده", color = ExpenseRed) },
                                    leadingIcon = { Icon(Icons.Default.Close, contentDescription = null, tint = ExpenseRed) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoansTabContent(
    loans: List<LoanEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean,
    onAddLoan: () -> Unit,
    onPayInstallment: (LoanEntity) -> Unit
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
                Text("تسهیلات بانکی و جدول استهلاک اقساط", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = onAddLoan) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("وام جدید")
                }
            }
        }

        items(loans, key = { it.id }) { loan ->
            val progress = if (loan.totalInstallments > 0) {
                loan.paidInstallments.toFloat() / loan.totalInstallments
            } else 0f

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
                        Text(loan.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(
                            "${loan.paidInstallments} از ${loan.totalInstallments} قسط",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "مبلغ قسط: ${CurrencyFormatter.formatRials(loan.monthlyInstallmentRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "مبلغ کل: ${CurrencyFormatter.formatRials(loan.totalAmountRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "بانک: ${loan.bankName} (سود ${PersianTextNormalizer.toPersianDigits(loan.interestRatePercent.toInt().toString())}٪)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (loan.paidInstallments < loan.totalInstallments) {
                            Button(
                                onClick = { onPayInstallment(loan) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("پرداخت قسط ماه", style = MaterialTheme.typography.labelSmall)
                            }
                        } else {
                            Text("تسویه کامل وام", color = IncomeGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalsTabContent(
    goals: List<SavingsGoalEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean,
    onAddGoal: () -> Unit,
    onDeposit: (SavingsGoalEntity, Long) -> Unit
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
                Text("صندوق‌های پس‌انداز هدفمند", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = onAddGoal) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("صندوق جدید")
                }
            }
        }

        items(goals, key = { it.id }) { goal ->
            val ratio = if (goal.targetAmountRial > 0) {
                (goal.currentAmountRial.toFloat() / goal.targetAmountRial).coerceIn(0f, 1f)
            } else 0f

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
                        Text(goal.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        val percent = (ratio * 100).toInt()
                        Text(
                            "${PersianTextNormalizer.toPersianDigits(percent.toString())}%",
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                    }

                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = IncomeGreen
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "موجود: ${CurrencyFormatter.formatRials(goal.currentAmountRial, currencyMode, isMasked)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "هدف: ${CurrencyFormatter.formatRials(goal.targetAmountRial, currencyMode, isMasked)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = { onDeposit(goal, 5_000_000L) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ ۵ میلیون ریال", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
