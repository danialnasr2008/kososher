package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.AccountNodeEntity

@Composable
fun AddJournalEntryDialog(
    accounts: List<AccountNodeEntity>,
    onDismiss: () -> Unit,
    onConfirm: (description: String, debitCode: String, debitName: String, creditCode: String, creditName: String, amount: Long) -> Unit
) {
    var description by remember { mutableStateOf("واریز حق‌الزحمه پروژه به حساب بانک") }
    var amountStr by remember { mutableStateOf("15000000") }

    val debitAccounts = accounts.filter { it.level.name != "GROUP" && it.level.name != "TOTAL" }
    var selectedDebit by remember { mutableStateOf(debitAccounts.firstOrNull { it.code == "110101" } ?: debitAccounts.firstOrNull()) }
    var selectedCredit by remember { mutableStateOf(debitAccounts.firstOrNull { it.code == "4101" } ?: debitAccounts.lastOrNull()) }

    var debitExpanded by remember { mutableStateOf(false) }
    var creditExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("ثبت سند دوبل حسابداری", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "سیستم دوطرفه خودکار: سرفصل بدهکار و بستانکار با مبلغ برابر ثبت شده و تراز سند تضمین می‌شود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("شرح آرتیکل سند") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("مبلغ سند (ریال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Debit account picker
                Box {
                    OutlinedButton(
                        onClick = { debitExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "حساب بدهکار: ${selectedDebit?.name ?: "انتخاب حساب"}",
                            maxLines = 1
                        )
                    }
                    DropdownMenu(
                        expanded = debitExpanded,
                        onDismissRequest = { debitExpanded = false }
                    ) {
                        debitAccounts.forEach { acc ->
                            DropdownMenuItem(
                                text = { Text("${acc.code} - ${acc.name}") },
                                onClick = {
                                    selectedDebit = acc
                                    debitExpanded = false
                                }
                            )
                        }
                    }
                }

                // Credit account picker
                Box {
                    OutlinedButton(
                        onClick = { creditExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "حساب بستانکار: ${selectedCredit?.name ?: "انتخاب حساب"}",
                            maxLines = 1
                        )
                    }
                    DropdownMenu(
                        expanded = creditExpanded,
                        onDismissRequest = { creditExpanded = false }
                    ) {
                        debitAccounts.forEach { acc ->
                            DropdownMenuItem(
                                text = { Text("${acc.code} - ${acc.name}") },
                                onClick = {
                                    selectedCredit = acc
                                    creditExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(",", "").toLongOrNull() ?: 0L
                    if (description.isNotBlank() && selectedDebit != null && selectedCredit != null && amount > 0) {
                        onConfirm(
                            description,
                            selectedDebit!!.code,
                            selectedDebit!!.name,
                            selectedCredit!!.code,
                            selectedCredit!!.name,
                            amount
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("ثبت سند موازنه شده")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
