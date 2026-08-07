package com.turboclone.app.data.model

data class AppUser(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val walletBalance: Double
)

data class Driver(
    val id: String,
    val name: String,
    val rating: Double,
    val carModel: String,
    val plateNumber: String,
    var lat: Double,
    var lng: Double,
    val etaMinutes: Int
)

enum class RideStatus {
    SEARCHING, ACCEPTED, DRIVER_ARRIVING, IN_PROGRESS, COMPLETED, CANCELLED
}

data class RideRequest(
    val id: String,
    val fromLat: Double,
    val fromLng: Double,
    val fromAddress: String,
    val toLat: Double,
    val toLng: Double,
    val toAddress: String,
    val distanceKm: Double,
    val etaMinutes: Int,
    val price: Double,
    var status: RideStatus = RideStatus.SEARCHING,
    var driver: Driver? = null
)

enum class VehicleType { CAR, BIKE }

enum class DriverApplicationStatus { PENDING, APPROVED, REJECTED }

data class DriverApplication(
    val id: String,
    var fullName: String = "",
    var age: String = "",
    var address: String = "",
    var phone: String = "",
    var vehicleType: VehicleType = VehicleType.CAR,
    var idDocumentUri: String? = null,
    var licenseDocumentUri: String? = null,
    var status: DriverApplicationStatus = DriverApplicationStatus.PENDING,
    val submittedAt: String = ""
)

data class WalletTransaction(
    val id: String,
    val title: String,
    val amount: Double,
    val isCredit: Boolean,
    val date: String
)

data class StoreCategory(
    val id: String,
    val name: String,
    val emoji: String
)

data class Store(
    val id: String,
    val name: String,
    val categoryId: String,
    val rating: Double,
    val deliveryTimeMinutes: Int,
    val deliveryFee: Double,
    val imageEmoji: String
)

data class Product(
    val id: String,
    val storeId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageEmoji: String
)

data class CartItem(
    val product: Product,
    var quantity: Int
)
