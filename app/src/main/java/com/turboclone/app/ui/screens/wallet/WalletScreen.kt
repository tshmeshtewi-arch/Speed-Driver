package com.turboclone.app.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turboclone.app.data.repository.MockRepository
import com.turboclone.app.ui.theme.RedPrimary
import com.turboclone.app.ui.theme.SurfaceDark2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(onBack: () -> Unit) {
    var showTopUp by remember { mutableStateOf(false) }
    var topUpAmount by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf(MockRepository.currentUser.walletBalance) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            Text("المحفظة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(RedPrimary)
                .padding(20.dp)
        ) {
            Text("الرصيد الحالي", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            Text("%.2f ريال".format(balance), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { showTopUp = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = RedPrimary)
            ) { Text("شحن الرصيد", fontWeight = FontWeight.Bold) }
        }

        Text("سجل العمليات", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(16.dp, 8.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(MockRepository.walletTransactions) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark2)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx.title, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text(tx.date, fontSize = 11.sp, color = Color.Gray)
                    }
                    Text(
                        (if (tx.isCredit) "+" else "-") + "%.1f ريال".format(tx.amount),
                        color = if (tx.isCredit) Color(0xFF2ECC71) else RedPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showTopUp) {
        AlertDialog(
            onDismissRequest = { showTopUp = false },
            title = { Text("شحن الرصيد") },
            text = {
                OutlinedTextField(
                    value = topUpAmount,
                    onValueChange = { topUpAmount = it.filter { c -> c.isDigit() } },
                    label = { Text("المبلغ (ريال)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = topUpAmount.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        scope.launch {
                            delay(500)
                            balance += amount
                            MockRepository.walletTransactions.add(
                                0,
                                com.turboclone.app.data.model.WalletTransaction(
                                    id = "t${MockRepository.walletTransactions.size + 1}",
                                    title = "شحن رصيد",
                                    amount = amount,
                                    isCredit = true,
                                    date = "الآن"
                                )
                            )
                            topUpAmount = ""
                            showTopUp = false
                        }
                    }
                }) { Text("تأكيد", color = RedPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showTopUp = false }) { Text("إلغاء") }
            }
        )
    }
}
