package com.turboclone.app.ui.screens.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
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
import com.turboclone.app.ui.theme.SurfaceDark
import com.turboclone.app.ui.theme.SurfaceDark2

@Composable
fun DeliveryHomeScreen(onOpenStore: (String) -> Unit, onBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val filteredStores = MockRepository.stores.filter { selectedCategory == null || it.categoryId == selectedCategory }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            Text("توصيل الطلبات", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(MockRepository.storeCategories) { cat ->
                val selected = selectedCategory == cat.id
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) RedPrimary else SurfaceDark)
                        .clickable { selectedCategory = if (selected) null else cat.id }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(cat.emoji, fontSize = 20.sp)
                    Text(cat.name, fontSize = 11.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredStores) { store ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark2)
                        .clickable { onOpenStore(store.id) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(56.dp).background(SurfaceDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(store.imageEmoji, fontSize = 26.sp) }

                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(store.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                            Text(" ${store.rating}  •  ${store.deliveryTimeMinutes} د  •  توصيل ${store.deliveryFee.toInt()} د.ل", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
