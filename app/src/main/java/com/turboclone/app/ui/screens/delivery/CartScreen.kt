package com.turboclone.app.ui.screens.delivery

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

enum class PaymentMethod { CASH, WALLET }

@Composable
fun CartScreen(onBack: () -> Unit, onOrderPlaced: () -> Unit) {
    var payment by remember { mutableStateOf(PaymentMethod.CASH) }
    var placing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var version by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            Text("سلة المشتريات", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (MockRepository.cart.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("السلة فارغة", color = Color.Gray)
            }
            return
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(MockRepository.cart) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark2)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.product.imageEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("الكمية: ${item.quantity}", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text("${item.product.price * item.quantity} ريال", color = RedPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("طريقة الدفع", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                FilterChip(
                    selected = payment == PaymentMethod.CASH,
                    onClick = { payment = PaymentMethod.CASH },
                    label = { Text("نقدًا") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = payment == PaymentMethod.WALLET,
                    onClick = { payment = PaymentMethod.WALLET },
                    label = { Text("المحفظة (${MockRepository.currentUser.walletBalance} ريال)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("الإجمالي", color = Color.Gray)
                Text("${"%.1f".format(MockRepository.cartTotal())} ريال", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    placing = true
                    scope.launch {
                        delay(1200)
                        MockRepository.cart.clear()
                        placing = false
                        onOrderPlaced()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !placing
            ) {
                if (placing) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("تأكيد الطلب", fontWeight = FontWeight.Bold)
            }
        }
    }
}
