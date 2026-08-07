package com.turboclone.app.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val RIDE_REQUEST = "ride_request/{fromLat}/{fromLng}/{fromAddr}/{toLat}/{toLng}/{toAddr}"
    const val RIDE_TRACKING = "ride_tracking"
    const val RIDE_RATING = "ride_rating"
    const val DELIVERY_HOME = "delivery_home"
    const val STORE_DETAIL = "store_detail/{storeId}"
    const val CART = "cart"
    const val WALLET = "wallet"
    const val PROFILE = "profile"
    const val DRIVER_APPLICATION = "driver_application"
    const val ADMIN_DRIVERS = "admin_drivers"

    fun rideRequest(fromLat: Double, fromLng: Double, fromAddr: String, toLat: Double, toLng: Double, toAddr: String): String {
        fun enc(s: String) = java.net.URLEncoder.encode(s, "UTF-8")
        return "ride_request/$fromLat/$fromLng/${enc(fromAddr)}/$toLat/$toLng/${enc(toAddr)}"
    }

    fun storeDetail(storeId: String) = "store_detail/$storeId"
}
