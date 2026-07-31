package com.alvinwijaya.tvapp.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alvinwijaya.tvapp.data.repository.ShowRepository
import com.alvinwijaya.tvapp.ui.detail.ShowDetailRoute
import com.alvinwijaya.tvapp.ui.detail.ShowDetailViewModel
import com.alvinwijaya.tvapp.ui.detail.ShowDetailViewModelFactory
import com.alvinwijaya.tvapp.ui.list.ShowListRoute
import com.alvinwijaya.tvapp.ui.list.ShowListViewModel
import com.alvinwijaya.tvapp.ui.list.ShowListViewModelFactory

private const val SHOW_LIST_ROUTE = "shows"
private const val SHOW_ID_ARGUMENT = "showId"
private const val SHOW_DETAIL_ROUTE = "shows/{$SHOW_ID_ARGUMENT}"

private fun createShowDetailRoute(
    showId: Int
): String {
    return "shows/$showId"
}

@Composable
fun AppNavigation(
    repository: ShowRepository,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SHOW_LIST_ROUTE,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
        popEnterTransition = {
            EnterTransition.None
        },
        popExitTransition = {
            ExitTransition.None
        }
    ) {
        composable(
            route = SHOW_LIST_ROUTE
        ) {
            val factory = remember(repository) {
                ShowListViewModelFactory(
                    repository = repository
                )
            }

            val showListViewModel: ShowListViewModel =
                viewModel(
                    factory = factory
                )

            ShowListRoute(
                viewModel = showListViewModel,
                onShowClick = { showId ->
                    navController.navigate(
                        createShowDetailRoute(showId)
                    ) {
                        launchSingleTop = true
                    }
                },
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }

        composable(
            route = SHOW_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(SHOW_ID_ARGUMENT) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val showId = backStackEntry.arguments
                ?.getInt(SHOW_ID_ARGUMENT)
                ?: return@composable

            val factory = remember(
                repository,
                showId
            ) {
                ShowDetailViewModelFactory(
                    repository = repository,
                    showId = showId
                )
            }

            val showDetailViewModel: ShowDetailViewModel =
                viewModel(
                    factory = factory
                )

            ShowDetailRoute(
                viewModel = showDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }
    }
}