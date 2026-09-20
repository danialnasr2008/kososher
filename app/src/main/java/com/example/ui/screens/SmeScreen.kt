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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.CurrencyDisplayMode
import com.example.common.CurrencyFormatter
import com.example.common.PersianTextNormalizer
import com.example.data.*
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AddInvoiceDialog
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("صدور فاکتور رسمی", "طرف‌های حساب (CRM)", "انبارداری کالا", "صندوق تنخواه")

    val contacts by viewModel.contacts.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val inventory by viewModel.inventoryItems.collectAsState()
    val pettyCash by viewModel.pettyCashList.collectAsState()
    val isMasked by viewModel.isPrivacyMasked.collectAsState()
    val currencyMode by viewModel.currencyMode.collectAsState()

    var showAddInvoiceDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var showAddInventoryDialog by remember { mutableStateOf(false) }

    if (showAddInvoiceDialog) {
        AddInvoiceDialog(
            onDismiss = { showAddInvoiceDialog = false },
            onConfirm = { contactName, subtotal, discount, vatPercent, isProforma, summary ->
                viewModel.addInvoice(contactName, subtotal, discount, vatPercent, isProforma, summary)
            }
        )
    }

    if (showAddContactDialog) {
        var name by remember { mutableStateOf("") }
        var company by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var creditLimitStr by remember { mutableStateOf("200000000") }
        var isCustomer by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("تعریف طرف‌حساب جدید", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = isCustomer,
                            onClick = { isCustomer = true },
                            label = { Text("مشتری") }
                        )
                        FilterChip(
                            selected = !isCustomer,
                            onClick = { isCustomer = false },
                            label = { Text("تامین‌کننده") }
                        )
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام شخص یا رابط") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = { Text("نام شرکت یا فروشگاه") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("شماره تماس") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = creditLimitStr,
                        onValueChange = { creditLimitStr = it },
                        label = { Text("سقف اعتبار (ریال)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = creditLimitStr.replace(",", "").toLongOrNull() ?: 0L
                        if (name.isNotBlank()) {
                            viewModel.addContact(name, company, phone, limit, isCustomer)
                            showAddContactDialog = false
                        }
                    }
                ) {
                    Text("ثبت طرف‌حساب")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) { Text("انصراف") }
            }
        )
    }

    if (showAddInventoryDialog) {
        var name by remember { mutableStateOf("") }
        var sku by remember { mutableStateOf("PRD-" + (100..999).random()) }
        var unit by remember { mutableStateOf("عدد") }
        var priceStr by remember { mutableStateOf("15000000") }
        var qtyStr by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showAddInventoryDialog = false },
            title = { Text("افزودن کالا به انبار", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام کالا یا خدمت") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sku,
                            onValueChange = { sku = it },
                            label = { Text("کد کالا (SKU)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("واحد شمارش") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("قیمت فروش (ریال)") },
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = qtyStr,
                            onValueChange = { qtyStr = it },
                            label = { Text("تعداد موجودی") },
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = priceStr.replace(",", "").toLongOrNull() ?: 0L
                        val qty = qtyStr.toIntOrNull() ?: 1
                        if (name.isNotBlank()) {
                            viewModel.addInventoryItem(name, sku, unit, price, qty)
                            showAddInventoryDialog = false
                        }
                    }
                ) {
                    Text("ثبت در کاردکس انبار")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddInventoryDialog = false }) { Text("انصراف") }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // SME & Business Accounting Header Banner
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
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = GoldAccent)
                }
                Column {
                    Text("حسابداری کسب‌وکار و بازرگانی", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleSmall)
                    Text("صدور فاکتور رسمی و ارزش‌افزوده، طرف‌های حساب و مدیریت انبار", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            0 -> InvoicesTabContent(
                invoices = invoices,
                currencyMode = currencyMode,
                isMasked = isMasked,
                onAddInvoice = { showAddInvoiceDialog = true },
                onPayInvoice = { viewModel.markInvoicePaid(it) }
            )
            1 -> ContactsTabContent(
                contacts = contacts,
                currencyMode = currencyMode,
                isMasked = isMasked,
                onAddContact = { showAddContactDialog = true }
            )
            2 -> InventoryTabContent(
                inventory = inventory,
                currencyMode = currencyMode,
                isMasked = isMasked,
                onAddItem = { showAddInventoryDialog = true }
            )
            3 -> PettyCashTabContent(
                pettyCashList = pettyCash,
                currencyMode = currencyMode,
                isMasked = isMasked
            )
        }
    }
}

@Composable
fun InvoicesTabContent(
    invoices: List<InvoiceEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean,
    onAddInvoice: () -> Unit,
    onPayInvoice: (InvoiceEntity) -> Unit
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
                Text("فهرست فاکتورها و پیش‌فاکتورها", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = onAddInvoice) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("صدور فاکتور")
                }
            }
        }

        items(invoices, key = { it.id }) { inv ->
            val dateStr = remember(inv.issueDateTimestamp) {
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                PersianTextNormalizer.toPersianDigits(sdf.format(Date(inv.issueDateTimestamp)))
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
                            Badge(
                                containerColor = if (inv.isProforma) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    if (inv.isProforma) "پیش‌فاکتور" else "فاکتور رسمی",
                                    color = if (inv.isProforma) GoldAccent else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Text(inv.invoiceNumber, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            CurrencyFormatter.formatRials(inv.totalRial, currencyMode, isMasked),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text("خریدار: ${inv.contactName} | تاریخ: $dateStr", style = MaterialTheme.typography.bodySmall)
                    Text("شرح: ${inv.itemsSummary}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ارزش افزوده (VAT): ${CurrencyFormatter.formatRials(inv.vatTaxRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (!inv.isPaid) {
                            Button(
                                onClick = { onPayInvoice(inv) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("ثبت تسویه وجه", style = MaterialTheme.typography.labelSmall)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                                Text("تسویه شده", color = IncomeGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactsTabContent(
    contacts: List<ContactEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean,
    onAddContact: () -> Unit
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
                Text("پرونده و گردش حساب طرف‌های تجاری", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = onAddContact) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("طرف‌حساب جدید")
                }
            }
        }

        items(contacts, key = { it.id }) { c ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(c.name, fontWeight = FontWeight.Bold)
                        val isPositive = c.balanceRial >= 0
                        Text(
                            text = (if (isPositive) "بدهکار به ما: " else "طلبکار از ما: ") +
                                    CurrencyFormatter.formatRials(Math.abs(c.balanceRial), currencyMode, isMasked),
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) IncomeGreen else ExpenseRed,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (c.company.isNotBlank()) {
                        Text(c.company, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("تماس: ${c.phone.ifBlank { "ثبت نشده" }}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "سقف اعتبار: ${CurrencyFormatter.formatRials(c.creditLimitRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryTabContent(
    inventory: List<InventoryItemEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean,
    onAddItem: () -> Unit
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
                Text("کاردکس کالا و انبارداری با هشدار نقطه سفارش", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FilledTonalButton(onClick = onAddItem) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("کالای جدید")
                }
            }
        }

        items(inventory, key = { it.id }) { item ->
            val isLowStock = item.quantity <= item.reorderPoint

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(item.name, fontWeight = FontWeight.Bold)
                            if (isLowStock) {
                                Badge(containerColor = ExpenseRed) {
                                    Text("نقطه سفارش", color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.padding(2.dp))
                                }
                            }
                        }

                        Text(
                            CurrencyFormatter.formatRials(item.unitPriceRial, currencyMode, isMasked),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("کد کالا: ${item.sku}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "موجودی: ${PersianTextNormalizer.toPersianDigits(item.quantity.toString())} ${item.unit}",
                            fontWeight = FontWeight.Bold,
                            color = if (isLowStock) ExpenseRed else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PettyCashTabContent(
    pettyCashList: List<PettyCashEntity>,
    currencyMode: CurrencyDisplayMode,
    isMasked: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text("مدیریت صندوق تنخواه‌گردان و فاکتورهای خرد", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(pettyCashList, key = { it.id }) { fund ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(fund.fundName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("مسئول تنخواه: ${fund.custodianName}", style = MaterialTheme.typography.bodySmall)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "موجودی باقیمانده: ${CurrencyFormatter.formatRials(fund.currentAmountRial, currencyMode, isMasked)}",
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                        Text(
                            "سقف تنخواه: ${CurrencyFormatter.formatRials(fund.initialAmountRial, currencyMode, isMasked)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
