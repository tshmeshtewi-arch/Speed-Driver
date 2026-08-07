package com.turboclone.app.ui.screens.driver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turboclone.app.data.model.DriverApplication
import com.turboclone.app.data.model.VehicleType
import com.turboclone.app.data.repository.MockRepository
import com.turboclone.app.ui.theme.BlackBg
import com.turboclone.app.ui.theme.RedPrimary
import com.turboclone.app.ui.theme.SurfaceDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * شاشة تقديم طلب انضمام كسائق (تجريبية بالكامل).
 * يملأ السائق بياناته الشخصية، يختار نوع المركبة، ويرفع صورة الهوية ورخصة القيادة (وهميًا فقط،
 * لا يتم رفع أي ملف لخادم حقيقي — الصور تُحفظ فقط كمرجع محلي داخل الجلسة).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverApplicationScreen(onBack: () -> Unit, onSubmitted: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf(VehicleType.CAR) }
    var idUri by remember { mutableStateOf<Uri?>(null) }
    var licenseUri by remember { mutableStateOf<Uri?>(null) }
    var submitting by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val idPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { idUri = it }
    val licensePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { licenseUri = it }

    val formValid = fullName.isNotBlank() && age.isNotBlank() && address.isNotBlank() &&
        phone.isNotBlank() && idUri != null && licenseUri != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.width(4.dp))
            Text("التسجيل كسائق", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (submitted) {
            Spacer(Modifier.height(60.dp))
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2ECC71), modifier = Modifier.size(72.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "تم إرسال طلبك بنجاح",
                fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "سيقوم فريق الإدارة بمراجعة بياناتك ومستنداتك، وستصلك رسالة إشعار فور قبول أو رفض الطلب.",
                color = Color(0xFFA0A0A0), fontSize = 13.sp
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onSubmitted,
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("تم") }
            return@Column
        }

        Spacer(Modifier.height(16.dp))
        Text("البيانات الشخصية", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(fullName, { fullName = it }, label = { Text("الاسم الكامل") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            age, { age = it.filter { c -> c.isDigit() } }, label = { Text("العمر") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(address, { address = it }, label = { Text("محل الإقامة (المدينة / الحي)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            phone, { phone = it.filter { c -> c.isDigit() } }, label = { Text("رقم الهاتف") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(22.dp))
        Text("نوع المركبة", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            VehicleTypeCard(
                title = "سيارة", icon = Icons.Default.DirectionsCar,
                selected = vehicleType == VehicleType.CAR,
                modifier = Modifier.weight(1f)
            ) { vehicleType = VehicleType.CAR }
            VehicleTypeCard(
                title = "دراجة نارية", icon = Icons.Default.DirectionsBike,
                selected = vehicleType == VehicleType.BIKE,
                modifier = Modifier.weight(1f)
            ) { vehicleType = VehicleType.BIKE }
        }

        Spacer(Modifier.height(22.dp))
        Text("المستندات المطلوبة", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        DocumentPickerRow(
            title = "صورة البطاقة الشخصية",
            picked = idUri != null,
            onClick = { idPicker.launch("image/*") }
        )
        Spacer(Modifier.height(10.dp))
        DocumentPickerRow(
            title = "صورة رخصة القيادة",
            picked = licenseUri != null,
            onClick = { licensePicker.launch("image/*") }
        )

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = {
                submitting = true
                scope.launch {
                    delay(900) // محاكاة إرسال الطلب
                    MockRepository.submitDriverApplication(
                        DriverApplication(
                            id = "app-${System.currentTimeMillis()}",
                            fullName = fullName,
                            age = age,
                            address = address,
                            phone = phone,
                            vehicleType = vehicleType,
                            idDocumentUri = idUri.toString(),
                            licenseDocumentUri = licenseUri.toString(),
                            submittedAt = "الآن"
                        )
                    )
                    submitting = false
                    submitted = true
                }
            },
            enabled = formValid && !submitting,
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (submitting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            else Text("إرسال الطلب", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun VehicleTypeCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(if (selected) RedPrimary.copy(alpha = 0.15f) else SurfaceDark, RoundedCornerShape(14.dp))
            .border(1.5.dp, if (selected) RedPrimary else Color.Transparent, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) RedPrimary else Color.White)
        Spacer(Modifier.height(6.dp))
        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DocumentPickerRow(title: String, picked: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (picked) Icons.Default.CheckCircle else Icons.Default.UploadFile,
            contentDescription = null,
            tint = if (picked) Color(0xFF2ECC71) else RedPrimary
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 14.sp)
            Text(
                if (picked) "تم اختيار الصورة" else "اضغط لاختيار صورة من المعرض",
                color = Color(0xFFA0A0A0), fontSize = 11.sp
            )
        }
    }
}
