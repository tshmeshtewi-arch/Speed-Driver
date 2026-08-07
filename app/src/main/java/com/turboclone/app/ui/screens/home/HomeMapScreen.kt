package com.turboclone.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import org.osmdroid.util.GeoPoint

private const val TRIPOLI_LAT = 32.8872
private const val TRIPOLI_LNG = 13.1913

@Composable
fun HomeMapScreen(
    onRequestRide: (fromLat: Double, fromLng: Double, fromAddr: String, toLat: Double, toLng: Double, toAddr: String) -> Unit,
    onOpenDelivery: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenProfile: () -> Unit
) {
    var fromPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var toPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var selectingFrom by remember { mutableStateOf(true) }

    val nearbyDrivers = remember { MockRepository.generateNearbyDrivers(TRIPOLI_LAT, TRIPOLI_LNG, 6) }

    Box(modifier = Modifier.fillMaxSize()) {
        OsmMapView(
            modifier = Modifier.fillMaxSize(),
            centerLat = TRIPOLI_LAT,
            centerLng = TRIPOLI_LNG,
            fromPoint = fromPoint,
            toPoint = toPoint,
            driverPoints = nearbyDrivers.map { GeoPoint(it.lat, it.lng) },
            onMapClick = { point ->
                if (selectingFrom) {
                    fromPoint = point
                    selectingFrom = false
                } else {
                    toPoint = point
                }
            }
        )

        // شريط علوي: بروفايل + محفظة
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPaddingCompat()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleIconButton(icon = Icons.Default.Person, onClick = onOpenProfile)
            CircleIconButton(icon = Icons.Default.AccountBalanceWallet, onClick = onOpenWallet)
        }

        // بطاقة سفلية: اختيار من/إلى + السعر + زر الطلب
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(SurfaceDark, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(20.dp)
        ) {
            Text(
                if (selectingFrom) "اضغط على الخريطة لتحديد نقطة الانطلاق"
                else if (toPoint == null) "اضغط على الخريطة لتحديد نقطة الوصول"
                else "جاهز لحساب الرحلة",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            LocationRow(icon = Icons.Default.MyLocation, label = "من", value = fromPoint?.let { "%.4f, %.4f".format(it.latitude, it.longitude) } ?: "لم يُحدد بعد")
            Spacer(modifier = Modifier.height(8.dp))
            LocationRow(icon = Icons.Default.LocationOn, label = "إلى", value = toPoint?.let { "%.4f, %.4f".format(it.latitude, it.longitude) } ?: "لم يُحدد بعد")

            if (fromPoint != null && toPoint != null) {
                val distance = MockRepository.calculateDistanceKm(fromPoint!!.latitude, fromPoint!!.longitude, toPoint!!.latitude, toPoint!!.longitude)
                val eta = MockRepository.estimateEtaMinutes(distance)
                val price = MockRepository.estimatePrice(distance)

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip("المسافة", "%.1f كم".format(distance))
                    InfoChip("الوقت", "$eta دقيقة")
                    InfoChip("السعر", "%.1f د.ل".format(price))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onRequestRide(fromPoint!!.latitude, fromPoint!!.longitude, "نقطة الانطلاق", toPoint!!.latitude, toPoint!!.longitude, "نقطة الوصول")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("اطلب سيارة الآن", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { fromPoint = null; toPoint = null; selectingFrom = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("إعادة تحديد النقاط", color = Color.Gray) }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onOpenDelivery,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark.copy(alpha = 0.0f), contentColor = RedPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, RedPrimary),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("توصيل الطلبات (مطاعم ومتاجر)")
            }
        }
    }
}

@Composable
private fun CircleIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(SurfaceDark, shape = androidx.compose.foundation.shape.CircleShape)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .then(Modifier),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
private fun LocationRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label: ", color = Color.Gray, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
private fun Modifier.statusBarsPaddingCompat(): Modifier = this
