package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddChequeDialog(
    onDismiss: () -> Unit,
    onConfirm: (number: String, sayadId: String, party: String, bank: String, amount: Long, dueDays: Int, isIncoming: Boolean) -> Unit
) {
    var number by remember { mutableStateOf("") }
    var sayadId by remember { mutableStateOf("") }
    var party by remember { mutableStateOf("") }
    var bank by remember { mutableStateOf("بانک ملت") }
    var amountStr by remember { mutableStateOf("") }
    var dueDaysStr by remember { mutableStateOf("15") }
    var isIncoming by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("ثبت چک جدید صیادی", fontWeight = FontWeight.Bold)
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
                        selected = isIncoming,
                        onClick = { isIncoming = true },
                        label = { Text("چک دریافتی (ورودی)") }
                    )
                    FilterChip(
                        selected = !isIncoming,
                        onClick = { isIncoming = false },
                        label = { Text("چک صادره (پرداختی)") }
                    )
                }

                OutlinedTextField(
                    value = sayadId,
                    onValueChange = { if (it.length <= 16) sayadId = it },
                    label = { Text("شناسه ۱۶ رقمی صیاد") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = party,
                    onValueChange = { party = it },
                    label = { Text(if (isIncoming) "نام صادرکننده چک" else "نام در وجه گیرنده") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = { Text("شماره سریال چک") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = bank,
                        onValueChange = { bank = it },
                        label = { Text("بانک صادرکننده") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("مبلغ (ریال)") },
                        modifier = Modifier.weight(1.2f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = dueDaysStr,
                        onValueChange = { dueDaysStr = it },
                        label = { Text("موعد (چند روز بعد)") },
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
                    val amount = amountStr.replace(",", "").toLongOrNull() ?: 0L
                    val days = dueDaysStr.toIntOrNull() ?: 15
                    if (party.isNotBlank() && amount > 0) {
                        onConfirm(number, sayadId, party, bank, amount, days, isIncoming)
                        onDismiss()
                    }
                }
            ) {
                Text("ثبت در سامانه چک")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
