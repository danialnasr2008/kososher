package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.CurrencyDisplayMode
import com.example.common.CurrencyFormatter
import com.example.common.PersianTextNormalizer
import com.example.data.BankCardEntity
import com.example.data.BankTransactionEntity
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankBalanceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val cards by viewModel.bankCards.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val combinedTotal by viewModel.combinedTotalRial.collectAsState()
    val isMasked by viewModel.isPrivacyMasked.collectAsState()
    val currencyMode by viewModel.currencyMode.collectAsState()

    var showAddSmsDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, DEPOSIT, WITHDRAWAL
    var inspectedTx by remember { mutableStateOf<BankTransactionEntity?>(null) }

    val filteredTx = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            "DEPOSIT" -> transactions.filter { it.type == "DEPOSIT" }
            "WITHDRAWAL" -> transactions.filter { it.type == "WITHDRAWAL" }
            else -> transactions
        }
    }

    if (showAddSmsDialog) {
        AddTransactionDialog(
            onDismiss = { showAddSmsDialog = false },
            onSubmitSms = { rawText ->
                viewModel.processRawSms(rawText)
            }
        )
    }

    // Inspect Raw SMS Dialog
    inspectedTx?.let { tx ->
        AlertDialog(
            onDismissRequest = { inspectedTx = null },
            title = {
                Text("متن پیامک خام بانک: ${tx.bankName}", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        tx.rawSms,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        "مانده اعلامی پس از تراکنش: ${CurrencyFormatter.formatRials(tx.balanceAfterRial, currencyMode, isMasked)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { inspectedTx = null }) {
                    Text("بستن")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // 1. Total Net Worth Header Card (M3 Expressive)
        item {
            TotalNetWorthCard(
                combinedTotal = combinedTotal,
                isMasked = isMasked,
                currencyMode = currencyMode,
                onToggleMask = { viewModel.togglePrivacyMask() },
                onToggleCurrency = { viewModel.toggleCurrencyMode() }
            )
        }

        // 2. Action Buttons (SMS Ingestion)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.importSampleBankSms() },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("بروزرسانی پیامک‌ها", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showAddSmsDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ورود پیامک", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 3. Bank Cards Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "حساب‌ها و کارت‌های متصل (${cards.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "بر پایه آخرین مانده بانک",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (cards.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("هنوز کارت بانکی ثبت نشده است", fontWeight = FontWeight.Bold)
                            Text(
                                "پیامک‌های بانکی به صورت خودکار شناسایی و اضافه می‌شوند",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(cards, key = { it.id }) { card ->
                            BankCardItem(
                                card = card,
                                isMasked = isMasked,
                                currencyMode = currencyMode,
                                onToggleExclude = { viewModel.toggleCardExcluded(card) }
                            )
                        }
                    }
                }
            }
        }

        // 4. Transactions List Header & Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "گردش حساب و تاریخچه تراکنش‌ها",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("همه تراکنش‌ها") }
                    )
                    FilterChip(
                        selected = selectedFilter == "DEPOSIT",
                        onClick = { selectedFilter = "DEPOSIT" },
                        label = { Text("واریزی‌ها") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                            selectedLabelColor = IncomeGreen
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "WITHDRAWAL",
                        onClick = { selectedFilter = "WITHDRAWAL" },
                        label = { Text("برداشت و خرید") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                            selectedLabelColor = ExpenseRed
                        )
                    )
                }
            }
        }

        // 5. Transactions Items
        if (filteredTx.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("تراکنشی در این دسته‌بندی یافت نشد", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredTx, key = { it.id }) { tx ->
                TransactionCardItem(
                    tx = tx,
                    isMasked = isMasked,
                    currencyMode = currencyMode,
                    onInspectSms = { inspectedTx = tx },
                    onExportToLedger = { viewModel.exportTransactionToLedger(tx) }
                )
            }
        }
    }
}

@Composable
fun TotalNetWorthCard(
    combinedTotal: Long,
    isMasked: Boolean,
    currencyMode: CurrencyDisplayMode,
    onToggleMask: () -> Unit,
    onToggleCurrency: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Text(
                            "مجموع موجودی نقد و بانک",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Currency mode switch
                        FilledTonalIconButton(
                            onClick = onToggleCurrency,
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.22f),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                if (currencyMode == CurrencyDisplayMode.TOMAN) "تومان" else "ریال",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }

                        // Privacy Mask toggle
                        FilledTonalIconButton(
                            onClick = onToggleMask,
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.22f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                if (isMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "تغییر حالت حریم خصوصی",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Amount display
                Text(
                    text = CurrencyFormatter.formatRials(combinedTotal, currencyMode, isMasked),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "امنیت آفلاین و حریم خصوصی ۱۰۰٪ محفوظ در دستگاه",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BankCardItem(
    card: BankCardEntity,
    isMasked: Boolean,
    currencyMode: CurrencyDisplayMode,
    onToggleExclude: () -> Unit
) {
    val baseColor = try {
        Color(android.graphics.Color.parseColor(card.colorHex))
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .width(268.dp)
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            baseColor,
                            baseColor.copy(alpha = 0.85f),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        card.bankName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = onToggleExclude,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            if (card.isExcludedFromTotal) Icons.Default.Block else Icons.Default.CheckCircle,
                            contentDescription = "محاسبه در موجودی کل",
                            tint = if (card.isExcludedFromTotal) Color.Red else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                    Text(
                        "کارت: •••• ${card.cardMask}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Column {
                    Text(
                        "مانده نهایی بانک:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = CurrencyFormatter.formatRials(card.latestBalanceRial, currencyMode, isMasked),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCardItem(
    tx: BankTransactionEntity,
    isMasked: Boolean,
    currencyMode: CurrencyDisplayMode,
    onInspectSms: () -> Unit,
    onExportToLedger: () -> Unit
) {
    val isDeposit = tx.type == "DEPOSIT"
    val timeFormatted = remember(tx.timestamp) {
        val sdf = SimpleDateFormat("HH:mm - MM/dd", Locale.getDefault())
        PersianTextNormalizer.toPersianDigits(sdf.format(Date(tx.timestamp)))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDeposit) IncomeGreen.copy(alpha = 0.15f) else ExpenseRed.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isDeposit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isDeposit) IncomeGreen else ExpenseRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            tx.description.ifBlank { tx.bankName },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${tx.bankName} | $timeFormatted",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Amount
                Text(
                    text = (if (isDeposit) "+ " else "- ") + CurrencyFormatter.formatRials(tx.amountRial, currencyMode, isMasked),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDeposit) IncomeGreen else ExpenseRed
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button to see raw SMS
                TextButton(
                    onClick = onInspectSms,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاهده پیامک", style = MaterialTheme.typography.labelSmall)
                }

                // Bridge to Double-entry accounting
                if (!tx.isExportedToLedger) {
                    OutlinedButton(
                        onClick = onExportToLedger,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ثبت در سند دوبل", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                        Text(
                            "در دفتر کل ثبت شد",
                            style = MaterialTheme.typography.labelSmall,
                            color = IncomeGreen
                        )
                    }
                }
            }
        }
    }
}
