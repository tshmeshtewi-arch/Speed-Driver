package com.turboclone.app.data.repository

import com.turboclone.app.data.model.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

/**
 * مستودع بيانات وهمي (Mock) لمحاكاة الباك-إند بالكامل.
 * كل شيء هنا وهمي عدا حسابات المسافة/الوقت التي تُبنى على إحداثيات حقيقية من الخريطة.
 */
object MockRepository {

    var currentUser: AppUser = AppUser(
        id = "u1",
        name = "خالد العتيبي",
        phone = "0555555555",
        email = "khaled@example.com",
        walletBalance = 125.50
    )

    val walletTransactions = mutableListOf(
        WalletTransaction("t1", "شحن رصيد", 100.0, true, "اليوم 10:12 ص"),
        WalletTransaction("t2", "رحلة - حي النخيل", 18.50, false, "أمس 6:40 م"),
        WalletTransaction("t3", "طلب من مطعم البيك", 44.0, false, "أمس 1:15 م")
    )

    val storeCategories = listOf(
        StoreCategory("c1", "مطاعم", "🍔"),
        StoreCategory("c2", "حلويات", "🍰"),
        StoreCategory("c3", "صيدليات", "💊"),
        StoreCategory("c4", "سوبرماركت", "🛒"),
        StoreCategory("c5", "ورد وهدايا", "🌹")
    )

    val stores = listOf(
        Store("s1", "مطعم البيك", "c1", 4.7, 25, 6.0, "🍗"),
        Store("s2", "بيتزا هت", "c1", 4.4, 30, 8.0, "🍕"),
        Store("s3", "حلويات الحلواني", "c2", 4.8, 20, 5.0, "🧁"),
        Store("s4", "صيدلية النهدي", "c3", 4.6, 15, 4.0, "💊"),
        Store("s5", "بنده سوبرماركت", "c4", 4.3, 35, 7.0, "🛒")
    )

    val products = listOf(
        Product("p1", "s1", "وجبة بروست", "٤ قطع دجاج + بطاطس + مشروب", 32.0, "🍗"),
        Product("p2", "s1", "برجر لحم", "برجر لحم مع جبن وصوص خاص", 22.0, "🍔"),
        Product("p3", "s2", "بيتزا خضار وسط", "عجينة إيطالية طازجة", 38.0, "🍕"),
        Product("p4", "s2", "بيتزا بيبروني عائلي", "حجم عائلي مقرمش", 55.0, "🍕"),
        Product("p5", "s3", "كيكة شوكولاتة", "قطعة كيكة شوكولاتة فاخرة", 18.0, "🍰"),
        Product("p6", "s3", "تشيز كيك فراولة", "تشيز كيك طازج", 20.0, "🍰"),
        Product("p7", "s4", "فيتامين سي", "علبة ٣٠ قرص", 25.0, "💊"),
        Product("p8", "s5", "سلة خضار وفواكه", "تشكيلة طازجة", 60.0, "🥬")
    )

    val cart = mutableListOf<CartItem>()

    fun addToCart(product: Product) {
        val existing = cart.find { it.product.id == product.id }
        if (existing != null) existing.quantity++ else cart.add(CartItem(product, 1))
    }

    fun removeFromCart(product: Product) {
        val existing = cart.find { it.product.id == product.id } ?: return
        if (existing.quantity > 1) existing.quantity-- else cart.remove(existing)
    }

    fun cartTotal(): Double = cart.sumOf { it.product.price * it.quantity }

    /** حساب مسافة حقيقية بين نقطتين حقيقيتين على الخريطة (معادلة Haversine) */
    fun calculateDistanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun estimateEtaMinutes(distanceKm: Double): Int = max(3, (distanceKm / 0.5).roundToInt()) // ~30كم/س متوسط داخل المدينة

    fun estimatePrice(distanceKm: Double): Double {
        val baseFare = 5.0
        val perKm = 1.8
        return baseFare + (distanceKm * perKm)
    }

    /** توليد سائقين وهميين حول موقع معين */
    fun generateNearbyDrivers(centerLat: Double, centerLng: Double, count: Int = 5): List<Driver> {
        val names = listOf("عبدالله محمد", "سعود فهد", "ناصر علي", "تركي سعد", "فيصل خالد", "بندر عمر")
        val cars = listOf("تويوتا كامري", "هيونداي إلنترا", "كيا سيراتو", "نيسان صني", "شيفروليه أفيو")
        return (0 until count).map { i ->
            val offsetLat = (Random.nextDouble(-1.0, 1.0)) * 0.01
            val offsetLng = (Random.nextDouble(-1.0, 1.0)) * 0.01
            Driver(
                id = "d$i",
                name = names.random(),
                rating = String.format("%.1f", Random.nextDouble(4.3, 5.0)).toDouble(),
                carModel = cars.random(),
                plateNumber = "${Random.nextInt(1000, 9999)} ع ب ج",
                lat = centerLat + offsetLat,
                lng = centerLng + offsetLng,
                etaMinutes = Random.nextInt(2, 9)
            )
        }
    }

    suspend fun simulateNetworkDelay() {
        delay(700)
    }
}
