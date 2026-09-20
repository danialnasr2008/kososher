package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddLoanDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, bank: String, totalAmount: Long, monthlyInstallment: Long, installments: Int) -> Unit
) {
    var title by remember { mutableStateOf("تسهیلات خرید تجهیزات") }
    var bank by remember { mutableStateOf("بانک رسالت") }
    var totalAmountStr by remember { mutableStateOf("240000000") }
    var installmentsStr by remember { mutableStateOf("24") }
    var monthlyStr by remember { mutableStateOf("10000000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("ثبت وام و تسهیلات جدید", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان تسهیلات یا وام") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = bank,
                    onValueChange = { bank = it },
                    label = { Text("بانک یا صندوق قرض‌الحسنه") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = totalAmountStr,
                    onValueChange = { totalAmountStr = it },
                    label = { Text("مبلغ کل وام (ریال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = monthlyStr,
                        onValueChange = { monthlyStr = it },
                        label = { Text("مبلغ هر قسط (ریال)") },
                        modifier = Modifier.weight(1.2f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = installmentsStr,
                        onValueChange = { installmentsStr = it },
                        label = { Text("تعداد اقساط") },
                        modifier = Modifier.weight(0.8f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = totalAmountStr.replace(",", "").toLongOrNull() ?: 0L
                    val monthly = monthlyStr.replace(",", "").toLongOrNull() ?: 0L
                    val count = installmentsStr.toIntOrNull() ?: 12
                    if (title.isNotBlank() && total > 0) {
                        onConfirm(title, bank, total, monthly, count)
                        onDismiss()
                    }
                }
            ) {
                Text("ایجاد پرونده وام")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
