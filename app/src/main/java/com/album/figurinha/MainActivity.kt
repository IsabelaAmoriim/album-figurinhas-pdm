package com.album.figurinha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.album.figurinha.api.ApiClient
import com.album.figurinha.repository.StickerCatalog
import com.album.figurinha.ui.navigation.Routes
import com.album.figurinha.ui.screens.*
import com.album.figurinha.ui.theme.FigurinhaTheme
import com.album.figurinha.util.ConnectivityObserver
import com.album.figurinha.util.NetworkConnectivityObserver
import com.album.figurinha.viewmodel.AlbumViewModel
import com.album.figurinha.viewmodel.DataViewModel
import com.album.figurinha.viewmodel.PackViewModel
import com.album.figurinha.viewmodel.WalletViewModel

class MainActivity : ComponentActivity(), ImageLoaderFactory {

    private lateinit var connectivityObserver: ConnectivityObserver

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient { ApiClient.getCoilClient() }
            .crossfade(true)
            .allowHardware(true)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        enableEdgeToEdge()

        setContent {
            val status by connectivityObserver
                .observe()
                .collectAsState(initial = ConnectivityObserver.Status.Available)

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
    val albumViewModel: AlbumViewModel = viewModel()
    val dataViewModel: DataViewModel = viewModel()

    val dataState by dataViewModel.state.collectAsState()
    LaunchedEffect(dataState) {
        android.util.Log.d("MainActivity", "dataState: isLoading=${dataState.isLoading}, teams=${dataState.teams.size}, error=${dataState.error}")
    }
    val walletState by walletViewModel.wallet.collectAsState()
    val collectedIds by albumViewModel.collectedIds.collectAsState()
    val raritiesMap by albumViewModel.raritiesMap.collectAsState()
    val albumProgress by albumViewModel.progress.collectAsState()

    // Quando o carregamento da API termina, notifica o AlbumViewModel
    LaunchedEffect(dataState.isLoading) {
        if (!dataState.isLoading && StickerCatalog.getTotalCount() > 0) {
            albumViewModel.onCatalogLoaded()
        }
    }

    // Tela de carregamento inicial enquanto a API carrega os dados
    if (dataState.isLoading) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Carregando figurinhas...")
                }
            }
        }
        return
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            composable(Routes.Home.route) {
                HomeScreen(
                    balance = walletState.moedas,
                    progress = albumProgress,
                    selections = albumViewModel.catalogSelections,
                    recompensasDisponiveis = walletState.recompensasDisponiveis,
                    onClaimReward = { walletViewModel.claimDailyReward() },
                    networkStatus = networkStatus,
                    onSelectionClick = { teamId ->
                        navController.navigate(Routes.SelectionDetail.createRoute(teamId))
                    },
                    onStoreClick = { navController.navigate(Routes.Store.route) },
                    onAlbumClick = { navController.navigate(Routes.Album.route) }
                )
            }

            composable(Routes.Album.route) {
                AlbumScreen(
                    albumViewModel = albumViewModel,
                    walletViewModel = walletViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.SelectionDetail.route,
                arguments = listOf(navArgument("teamId") { type = NavType.IntType })
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getInt("teamId") ?: 1

                val team = dataViewModel.getTeamById(teamId)

                SelectionDetailScreen(
                    team = team,
                    teamId = teamId,
                    collectedIds = collectedIds,
                    raritiesMap = raritiesMap,
                    dataViewModel = dataViewModel,
                    onBack = { navController.popBackStack() },
                    onCountryClick = { id ->
                        navController.navigate(Routes.CountryDetail.createRoute(id))
                    },
                    onPlayerClick = { id ->
                        navController.navigate(Routes.PlayerDetail.createRoute(teamId, id))
                    },
                    onCoachClick = { id ->
                        navController.navigate(Routes.CoachDetail.createRoute(id))
                    }
                )
            }

            composable(
                route = Routes.PlayerDetail.route,
                arguments = listOf(
                    navArgument("teamId") { type = NavType.IntType },
                    navArgument("playerId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
                val playerId = backStackEntry.arguments?.getInt("playerId") ?: 0

                PlayerDetailScreen(
                    teamId = teamId,
                    playerId = playerId,
                    dataViewModel = dataViewModel,
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
                    dataViewModel = dataViewModel,
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
                    dataViewModel = dataViewModel,
                    albumViewModel = albumViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.Store.route) {
                StoreScreen(
                    walletViewModel = walletViewModel,
                    packViewModel = packViewModel,
                    albumViewModel = albumViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun Spacer(modifier: Modifier) {
    androidx.compose.foundation.layout.Spacer(modifier = modifier)
}
