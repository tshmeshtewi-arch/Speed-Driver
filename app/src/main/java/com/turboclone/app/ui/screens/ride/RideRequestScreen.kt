package com.turboclone.app.ui.screens.ride

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turboclone.app.data.model.Driver
import com.turboclone.app.data.repository.MockRepository
import com.turboclone.app.ui.theme.RedPrimary
import kotlinx.coroutines.delay

@Composable
fun RideRequestScreen(
    fromLat: Double, fromLng: Double, fromAddr: String,
    toLat: Double, toLng: Double, toAddr: String,
    onDriverFound: (Driver) -> Unit,
    onCancel: () -> Unit
) {
    var searching by remember { mutableStateOf(true) }
    val rotation = rememberInfiniteTransition(label = "spin")
    val angle by rotation.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "angle"
    )

    LaunchedEffect(Unit) {
        delay(3500)
        val driver = MockRepository.generateNearbyDrivers(fromLat, fromLng, 1).first()
        searching = false
        onDriverFound(driver)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(90.dp).rotate(angle),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                color = RedPrimary,
                strokeWidth = 4.dp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("جاري البحث عن أقرب سائق...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("من: $fromAddr", fontSize = 13.sp, color = Color.Gray)
        Text("إلى: $toAddr", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(40.dp))
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("إلغاء الطلب", color = RedPrimary)
        }
    }
}
