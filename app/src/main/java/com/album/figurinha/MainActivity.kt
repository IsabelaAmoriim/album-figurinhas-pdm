package com.album.figurinha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.album.figurinha.api.ApiClient
import com.album.figurinha.ui.navigation.Routes
import com.album.figurinha.ui.screens.*
import com.album.figurinha.ui.theme.FigurinhaTheme
import com.album.figurinha.ui.viewmodel.PackViewModel
import com.album.figurinha.ui.viewmodel.WalletViewModel
import com.album.figurinha.util.ConnectivityObserver
import com.album.figurinha.util.NetworkConnectivityObserver

class MainActivity : ComponentActivity(), ImageLoaderFactory {
    private lateinit var connectivityObserver: ConnectivityObserver

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient { ApiClient.getCoilClient() } // Explicitly use our browser-agent client
            .crossfade(true)
            .allowHardware(true) // Ensure performance
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        enableEdgeToEdge()
        setContent {
            val status by connectivityObserver.observe().collectAsState(
                initial = ConnectivityObserver.Status.Available // Assume available for faster initial load
            )
            FigurinhaTheme {
                MainNavigation(status)
            }
        }
    }
}

@Composable
fun MainNavigation(networkStatus: ConnectivityObserver.Status) {
    val navController = rememberNavController()
    val walletViewModel: WalletViewModel = viewModel()
    val packViewModel: PackViewModel = viewModel()
    val walletState by walletViewModel.wallet.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            }
        ) {
            composable(Routes.Home.route) {
                HomeScreen(
                    balance = walletState.moedas,
                    networkStatus = networkStatus,
                    onSelectionClick = { teamId ->
                        navController.navigate(Routes.SelectionDetail.createRoute(teamId))
                    },
                    onStoreClick = {
                        navController.navigate(Routes.Store.route)
                    }
                )
            }
            composable(
                route = Routes.SelectionDetail.route,
                arguments = listOf(navArgument("teamId") { type = NavType.IntType })
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getInt("teamId") ?: 1
                SelectionDetailScreen(
                    teamId = teamId,
                    onBack = { navController.popBackStack() },
                    onCountryClick = { id -> navController.navigate(Routes.CountryDetail.createRoute(id)) },
                    onPlayerClick = { id -> navController.navigate(Routes.PlayerDetail.createRoute(id)) },
                    onCoachClick = { id -> navController.navigate(Routes.CoachDetail.createRoute(id)) }
                )
            }
            composable(
                route = Routes.PlayerDetail.route,
                arguments = listOf(navArgument("playerId") { type = NavType.IntType })
            ) { backStackEntry ->
                val playerId = backStackEntry.arguments?.getInt("playerId") ?: 0
                PlayerDetailScreen(
                    playerId = playerId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.CoachDetail.route,
                arguments = listOf(navArgument("coachId") { type = NavType.IntType })
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getInt("coachId") ?: 0
                CoachDetailScreen(
                    coachId = coachId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.CountryDetail.route,
                arguments = listOf(navArgument("teamId") { type = NavType.IntType })
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
                CountryDetailScreen(
                    teamId = teamId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.Store.route) {
                StoreScreen(
                    walletViewModel = walletViewModel,
                    packViewModel = packViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}