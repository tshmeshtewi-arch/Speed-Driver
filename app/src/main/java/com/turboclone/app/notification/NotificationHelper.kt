package com.turboclone.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * منبّه إشعارات محلي (Local Notifications) يحاكي وصول إشعار Push حقيقي
 * (مثل: قبول/رفض السائق، وصول السائق، انتهاء الرحلة...).
 * ملاحظة: هذا وهمي تمامًا ولا يتصل بأي خادم حقيقي — لا يوجد باك-إند فعلي في هذه النسخة.
 * عند ربط Firebase Cloud Messaging لاحقًا، يمكن استبدال نقطة الاستدعاء هذه بإشعار Push فعلي قادم من السيرفر.
 */
object NotificationHelper {

    private const val CHANNEL_ID = "speed_driver_channel"
    private const val CHANNEL_NAME = "إشعارات Speed Driver"
    private var nextId = 1000

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات الرحلات والطلبات وحالة السائق"
            }
            manager?.createNotificationChannel(channel)
        }
    }

    fun notify(context: Context, title: String, message: String) {
        ensureChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // في حال عدم منح صلاحية الإشعارات (Android 13+) نتجاهل الاستدعاء بأمان
        runCatching {
            NotificationManagerCompat.from(context).notify(nextId++, builder.build())
        }
    }

    fun notifyDriverApproved(context: Context) = notify(
        context,
        "تم قبول طلبك 🎉",
        "مبروك! تم التحقق من بياناتك ومستنداتك، ويمكنك الآن استقبال الرحلات كسائق."
    )

    fun notifyDriverRejected(context: Context) = notify(
        context,
        "لم يتم قبول الطلب",
        "نعتذر، لم يتم قبول طلب انضمامك كسائق حاليًا. تواصل مع الدعم لمزيد من التفاصيل."
    )

    fun notifyRideAccepted(context: Context, driverName: String) = notify(
        context,
        "تم قبول رحلتك",
        "السائق $driverName في طريقه إليك الآن."
    )

    fun notifyDriverArrived(context: Context) = notify(
        context,
        "السائق وصل",
        "السائق بانتظارك في نقطة الانطلاق."
    )

    fun notifyRideCompleted(context: Context) = notify(
        context,
        "انتهت الرحلة",
        "نتمنى أنك استمتعت برحلتك. لا تنسَ تقييم السائق."
    )
}
