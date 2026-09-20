package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onSubmitSms: (String) -> Unit
) {
    var rawText by remember {
        mutableStateOf("واریز 3,200,000 ریال به حساب بلوبانک از طرف شرکت پویا مانده: 41,450,000 ریال")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("ثبت و پردازش پیامک بانکی", fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "متن کامل پیامک بانکی را وارد نمایید. سیستم هوشمند اروند به صورت خودکار مانده نهایی، مبلغ، نام بانک و نوع تراکنش را استخراج و ذخیره می‌کند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    label = { Text("متن پیامک بانک") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(16.dp)
                )

                Text(
                    "نمونه‌های سریع تراکنش بانکی:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AssistChip(
                        onClick = {
                            rawText = "واریز: 7,500,000 ریال به حساب 6037***8891 بانک ملی ایران مانده: 21,820,000 ریال"
                        },
                        shape = RoundedCornerShape(14.dp),
                        label = { Text("بانک ملی", style = MaterialTheme.typography.labelSmall) }
                    )
                    AssistChip(
                        onClick = {
                            rawText = "خرید 850,000 ریال پایانه کارت 6104***1144 بانک ملت موجودی: 18,300,000 ریال"
                        },
                        shape = RoundedCornerShape(14.dp),
                        label = { Text("بانک ملت", style = MaterialTheme.typography.labelSmall) }
                    )
                    AssistChip(
                        onClick = {
                            rawText = "واریز پایا 12,000,000 ریال به حساب 5041***9921 قرض الحسنه رسالت مانده: 74,400,000 ریال"
                        },
                        shape = RoundedCornerShape(14.dp),
                        label = { Text("رسالت", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (rawText.isNotBlank()) {
                        onSubmitSms(rawText)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("پردازش و ثبت در حسابداری")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
