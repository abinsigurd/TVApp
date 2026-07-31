package com.alvinwijaya.tvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alvinwijaya.tvapp.data.remote.RetrofitClient
import com.alvinwijaya.tvapp.data.repository.ShowRepositoryImpl
import com.alvinwijaya.tvapp.ui.navigation.AppNavigation
import com.alvinwijaya.tvapp.ui.theme.TVAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()

            var isDarkTheme by rememberSaveable {
                mutableStateOf(systemDarkTheme)
            }

            TVAppTheme(
                darkTheme = isDarkTheme
            ) {
                val repository = remember {
                    ShowRepositoryImpl(
                        api = RetrofitClient.tvMazeApi
                    )
                }

                AppNavigation(
                    repository = repository,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { enabled ->
                        isDarkTheme = enabled
                    }
                )
            }
        }
    }
}