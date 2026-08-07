package com.turboclone.app.ui.screens.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ShoppingCart
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

@Composable
fun StoreDetailScreen(storeId: String, onBack: () -> Unit, onOpenCart: () -> Unit) {
    val store = MockRepository.stores.find { it.id == storeId } ?: return
    val products = MockRepository.products.filter { it.storeId == storeId }
    var cartVersion by remember { mutableStateOf(0) } // لإجبار إعادة الرسم عند تغيير السلة

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(store.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("⭐ ${store.rating} • ${store.deliveryTimeMinutes} دقيقة", fontSize = 12.sp, color = Color.Gray)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    val inCart = MockRepository.cart.find { it.product.id == product.id }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark2)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(50.dp).background(RedPrimary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text(product.imageEmoji, fontSize = 24.sp) }

                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(product.description, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                            Text("${product.price} ريال", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (inCart == null) {
                            IconButton(onClick = {
                                MockRepository.addToCart(product)
                                cartVersion++
                            }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = RedPrimary)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { MockRepository.removeFromCart(product); cartVersion++ }) {
                                    Text("−", color = Color.White, fontSize = 18.sp)
                                }
                                Text("${inCart.quantity}", color = Color.White)
                                IconButton(onClick = { MockRepository.addToCart(product); cartVersion++ }) {
                                    Text("+", color = Color.White, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (MockRepository.cart.isNotEmpty()) {
            Button(
                onClick = onOpenCart,
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp)
            ) {
                Text("عرض السلة • ${"%.1f".format(MockRepository.cartTotal())} ريال", fontWeight = FontWeight.Bold)
            }
        }
    }
}
