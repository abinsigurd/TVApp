package com.alvinwijaya.tvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.alvinwijaya.tvapp.data.remote.RetrofitClient
import com.alvinwijaya.tvapp.data.repository.ShowRepositoryImpl
import com.alvinwijaya.tvapp.ui.navigation.AppNavigation
import com.alvinwijaya.tvapp.ui.theme.TVAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TVAppTheme {
                val repository = remember {
                    ShowRepositoryImpl(
                        api = RetrofitClient.tvMazeApi
                    )
                }

                AppNavigation(
                    repository = repository
                )
            }
        }
    }
}