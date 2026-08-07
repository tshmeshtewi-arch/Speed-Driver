package com.turboclone.app.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.turboclone.app.data.model.Driver
import com.turboclone.app.ui.screens.auth.LoginScreen
import com.turboclone.app.ui.screens.auth.SignupScreen
import com.turboclone.app.ui.screens.delivery.CartScreen
import com.turboclone.app.ui.screens.delivery.DeliveryHomeScreen
import com.turboclone.app.ui.screens.delivery.StoreDetailScreen
import com.turboclone.app.ui.screens.home.HomeMapScreen
import com.turboclone.app.ui.screens.profile.ProfileScreen
import com.turboclone.app.ui.screens.ride.DriverTrackingScreen
import com.turboclone.app.ui.screens.ride.RatingScreen
import com.turboclone.app.ui.screens.ride.RideRequestScreen
import com.turboclone.app.ui.screens.splash.SplashScreen
import com.turboclone.app.ui.screens.wallet.WalletScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    // نحتفظ بالسائق ونقطة الوصول الحالية في الذاكرة أثناء الرحلة
    var activeDriver by remember { mutableStateOf<Driver?>(null) }
    var activeDestLat by remember { mutableStateOf(0.0) }
    var activeDestLng by remember { mutableStateOf(0.0) }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } }
            })
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onGoToSignup = { navController.navigate(Routes.SIGNUP) }
            )
        }

        composable(Routes.SIGNUP) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeMapScreen(
                onRequestRide = { fromLat, fromLng, fromAddr, toLat, toLng, toAddr ->
                    activeDestLat = toLat
                    activeDestLng = toLng
                    navController.navigate(Routes.rideRequest(fromLat, fromLng, fromAddr, toLat, toLng, toAddr))
                },
                onOpenDelivery = { navController.navigate(Routes.DELIVERY_HOME) },
                onOpenWallet = { navController.navigate(Routes.WALLET) },
                onOpenProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(
            route = Routes.RIDE_REQUEST,
            arguments = listOf(
                navArgument("fromLat") { type = NavType.StringType },
                navArgument("fromLng") { type = NavType.StringType },
                navArgument("fromAddr") { type = NavType.StringType },
                navArgument("toLat") { type = NavType.StringType },
                navArgument("toLng") { type = NavType.StringType },
                navArgument("toAddr") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val fromLat = backStackEntry.arguments?.getString("fromLat")?.toDoubleOrNull() ?: 0.0
            val fromLng = backStackEntry.arguments?.getString("fromLng")?.toDoubleOrNull() ?: 0.0
            val fromAddr = backStackEntry.arguments?.getString("fromAddr") ?: ""
            val toLat = backStackEntry.arguments?.getString("toLat")?.toDoubleOrNull() ?: 0.0
            val toLng = backStackEntry.arguments?.getString("toLng")?.toDoubleOrNull() ?: 0.0
            val toAddr = backStackEntry.arguments?.getString("toAddr") ?: ""

            RideRequestScreen(
                fromLat = fromLat, fromLng = fromLng, fromAddr = fromAddr,
                toLat = toLat, toLng = toLng, toAddr = toAddr,
                onDriverFound = { driver ->
                    activeDriver = driver
                    navController.navigate(Routes.RIDE_TRACKING) {
                        popUpTo(Routes.HOME)
                    }
                },
                onCancel = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }

        composable(Routes.RIDE_TRACKING) {
            activeDriver?.let { driver ->
                DriverTrackingScreen(
                    driver = driver,
                    destLat = activeDestLat,
                    destLng = activeDestLng,
                    onRideCompleted = { navController.navigate(Routes.RIDE_RATING) }
                )
            }
        }

        composable(Routes.RIDE_RATING) {
            activeDriver?.let { driver ->
                RatingScreen(
                    driver = driver,
                    onDone = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                    }
                )
            }
        }

        composable(Routes.DELIVERY_HOME) {
            DeliveryHomeScreen(
                onOpenStore = { storeId -> navController.navigate(Routes.storeDetail(storeId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.STORE_DETAIL,
            arguments = listOf(navArgument("storeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId") ?: ""
            StoreDetailScreen(
                storeId = storeId,
                onBack = { navController.popBackStack() },
                onOpenCart = { navController.navigate(Routes.CART) }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                onBack = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                }
            )
        }

        composable(Routes.WALLET) {
            WalletScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } }
                }
            )
        }
    }
}
