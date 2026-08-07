package com.turboclone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.turboclone.app.navigation.AppNavGraph
import com.turboclone.app.ui.theme.BlackBg
import com.turboclone.app.ui.theme.TurboCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurboCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().background(BlackBg)
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}
