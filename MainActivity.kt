package com.schoolmanagement.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.material3.MaterialTheme
import com.schoolmanagement.app.navigation.AppNavigation
import com.schoolmanagement.app.ui.theme.SchoolTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            // Layout direction follows the device/app locale (Urdu => RTL) via supportsRtl.
            SchoolTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { AppNavigation() }
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        (application as SchoolApp).session.touch()
    }
}
