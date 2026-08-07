package com.turboclone.app.ui.screens.driver

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turboclone.app.data.model.DriverApplication
import com.turboclone.app.data.model.DriverApplicationStatus
import com.turboclone.app.data.model.VehicleType
import com.turboclone.app.data.repository.MockRepository
import com.turboclone.app.notification.NotificationHelper
import com.turboclone.app.ui.theme.BlackBg
import com.turboclone.app.ui.theme.RedPrimary
import com.turboclone.app.ui.theme.SurfaceDark

/**
 * لوحة إدارة مبسّطة داخل نفس التطبيق (نسخة تجريبية) لمراجعة طلبات انضمام السائقين.
 * القبول/الرفض هنا وهمي بالكامل ويعمل محليًا داخل الجلسة فقط، لكنه يُطلق إشعارًا محليًا فعليًا
 * لمحاكاة إشعار push حقيقي يصل للسائق فور اتخاذ القرار.
 * ملاحظة: هذه ليست لوحة إدارة منفصلة فعليًا بعد — راجع الرسالة المرفقة مع المشروع لتفاصيل الخطوة القادمة.
 */
@Composable
fun AdminDriversScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val applications = MockRepository.driverApplications

    Column(modifier = Modifier.fillMaxSize().background(BlackBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("لوحة الإدارة • طلبات السائقين", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (applications.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد طلبات انضمام حاليًا", color = Color(0xFFA0A0A0))
            }
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(applications, key = { it.id }) { app ->
                DriverApplicationCard(app, context)
            }
        }
    }
}

@Composable
private fun DriverApplicationCard(app: DriverApplication, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (app.vehicleType == VehicleType.CAR) Icons.Default.DirectionsCar else Icons.Default.DirectionsBike,
                contentDescription = null, tint = RedPrimary
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(app.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${app.age} سنة • ${app.address}", color = Color(0xFFA0A0A0), fontSize = 12.sp)
            }
            StatusBadge(app.status)
        }

        Spacer(Modifier.height(10.dp))
        Text("الهاتف: ${app.phone}", color = Color(0xFFA0A0A0), fontSize = 12.sp)
        Text(
            "نوع المركبة: ${if (app.vehicleType == VehicleType.CAR) "سيارة" else "دراجة نارية"}",
            color = Color(0xFFA0A0A0), fontSize = 12.sp
        )
        Text("✔ تم إرفاق صورة البطاقة الشخصية ورخصة القيادة", color = Color(0xFFA0A0A0), fontSize = 12.sp)

        if (app.status == DriverApplicationStatus.PENDING) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        MockRepository.approveDriver(app.id)
                        NotificationHelper.notifyDriverApproved(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                    modifier = Modifier.weight(1f)
                ) { Text("قبول") }
                OutlinedButton(
                    onClick = {
                        MockRepository.rejectDriver(app.id)
                        NotificationHelper.notifyDriverRejected(context)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary),
                    modifier = Modifier.weight(1f)
                ) { Text("رفض") }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: DriverApplicationStatus) {
    val (label, color) = when (status) {
        DriverApplicationStatus.PENDING -> "قيد المراجعة" to Color(0xFFFFC107)
        DriverApplicationStatus.APPROVED -> "مقبول" to Color(0xFF2ECC71)
        DriverApplicationStatus.REJECTED -> "مرفوض" to Color(0xFFE4111A)
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
