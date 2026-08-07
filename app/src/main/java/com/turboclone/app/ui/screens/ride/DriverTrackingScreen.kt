package com.turboclone.app.ui.screens.ride

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turboclone.app.data.model.Driver
import com.turboclone.app.data.model.RideStatus
import com.turboclone.app.ui.screens.home.OsmMapView
import com.turboclone.app.ui.theme.RedPrimary
import com.turboclone.app.ui.theme.SurfaceDark
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint

@Composable
fun DriverTrackingScreen(
    driver: Driver,
    destLat: Double,
    destLng: Double,
    onRideCompleted: () -> Unit
) {
    var status by remember { mutableStateOf(RideStatus.DRIVER_ARRIVING) }
    var driverLat by remember { mutableStateOf(driver.lat) }
    var driverLng by remember { mutableStateOf(driver.lng) }

    // محاكاة حركة السائق نحو نقطة الوصول
    LaunchedEffect(status) {
        when (status) {
            RideStatus.DRIVER_ARRIVING -> {
                delay(4000)
                status = RideStatus.IN_PROGRESS
            }
            RideStatus.IN_PROGRESS -> {
                repeat(10) {
                    delay(400)
                    driverLat += (destLat - driverLat) * 0.15
                    driverLng += (destLng - driverLng) * 0.15
                }
                status = RideStatus.COMPLETED
            }
            RideStatus.COMPLETED -> {
                delay(600)
                onRideCompleted()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OsmMapView(
            modifier = Modifier.fillMaxSize(),
            centerLat = driverLat,
            centerLng = driverLng,
            toPoint = GeoPoint(destLat, destLng),
            driverPoints = listOf(GeoPoint(driverLat, driverLng))
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(SurfaceDark, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(20.dp)
        ) {
            Text(
                when (status) {
                    RideStatus.DRIVER_ARRIVING -> "السائق في الطريق إليك (${driver.etaMinutes} دقيقة)"
                    RideStatus.IN_PROGRESS -> "الرحلة جارية الآن 🚗"
                    RideStatus.COMPLETED -> "وصلت! انتهت الرحلة 🎉"
                    else -> ""
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).background(RedPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(driver.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("${driver.carModel} - ${driver.plateNumber}", fontSize = 12.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Text(" ${driver.rating}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                IconButton(onClick = { /* اتصال وهمي */ }) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = RedPrimary)
                }
            }
        }
    }
}
