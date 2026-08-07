package com.turboclone.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.turboclone.app.ui.theme.SurfaceDark2

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenDriverApplication: () -> Unit = {},
    onOpenAdmin: () -> Unit = {}
) {
    val user = MockRepository.currentUser

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            Text("الملف الشخصي", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Box(
                modifier = Modifier.size(90.dp).background(RedPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(user.phone, color = Color.Gray, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            Triple(Icons.Default.History, "سجل الرحلات والطلبات", {}),
            Triple(Icons.Default.AccountBalanceWallet, "المحفظة", {}),
            Triple(Icons.Default.Notifications, "الإشعارات", {}),
            Triple(Icons.Default.DirectionsCar, "التسجيل كسائق", onOpenDriverApplication),
            Triple(Icons.Default.AdminPanelSettings, "لوحة الإدارة (تجريبي)", onOpenAdmin),
            Triple(Icons.Default.Settings, "الإعدادات", {}),
            Triple(Icons.Default.HelpOutline, "المساعدة والدعم", {})
        ).forEach { (icon, label, action) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark2)
                    .clickable { action() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(label, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(48.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("تسجيل الخروج", color = RedPrimary)
        }
    }
}
