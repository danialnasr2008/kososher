package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.common.CurrencyFormatter

@Composable
fun AddInvoiceDialog(
    onDismiss: () -> Unit,
    onConfirm: (contactName: String, subtotal: Long, discount: Long, vatPercent: Int, isProforma: Boolean, summary: String) -> Unit
) {
    var contactName by remember { mutableStateOf("") }
    var subtotalStr by remember { mutableStateOf("50000000") }
    var discountStr by remember { mutableStateOf("0") }
    var vatPercentStr by remember { mutableStateOf("10") }
    var isProforma by remember { mutableStateOf(false) }
    var itemsSummary by remember { mutableStateOf("خدمات برنامه‌نویسی و طراحی رابط کاربری") }

    val subtotal = subtotalStr.replace(",", "").toLongOrNull() ?: 0L
    val discount = discountStr.replace(",", "").toLongOrNull() ?: 0L
    val vatPercent = vatPercentStr.toIntOrNull() ?: 10
    val taxableAmount = (subtotal - discount).coerceAtLeast(0L)
    val vatTax = (taxableAmount * vatPercent) / 100L
    val grandTotal = taxableAmount + vatTax

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("صدور فاکتور / پیش‌فاکتور رسمی", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isProforma,
                        onClick = { isProforma = false },
                        label = { Text("فاکتور فروش رسمی") }
                    )
                    FilterChip(
                        selected = isProforma,
                        onClick = { isProforma = true },
                        label = { Text("پیش‌فاکتور (Proforma)") }
                    )
                }

                OutlinedTextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("نام مشتری / خریدار") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = itemsSummary,
                    onValueChange = { itemsSummary = it },
                    label = { Text("شرح کالا یا خدمات فاکتور") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = subtotalStr,
                        onValueChange = { subtotalStr = it },
                        label = { Text("مبلغ پایه (ریال)") },
                        modifier = Modifier.weight(1.2f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = discountStr,
                        onValueChange = { discountStr = it },
                        label = { Text("تخفیف (ریال)") },
                        modifier = Modifier.weight(0.8f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Live calculation box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("مالیات بر ارزش افزوده ($vatPercent%):", style = MaterialTheme.typography.bodySmall)
                            Text(CurrencyFormatter.formatRials(vatTax), style = MaterialTheme.typography.bodySmall)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("مبلغ نهایی قابل پرداخت:", fontWeight = FontWeight.Bold)
                            Text(
                                CurrencyFormatter.formatRials(grandTotal),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (contactName.isNotBlank() && subtotal > 0) {
                        onConfirm(contactName, subtotal, discount, vatPercent, isProforma, itemsSummary)
                        onDismiss()
                    }
                }
            ) {
                Text("تولید و ثبت فاکتور")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
