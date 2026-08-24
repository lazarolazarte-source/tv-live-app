package com.tulaza.tvlive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tulaza.tvlive.data.SourceType
import com.tulaza.tvlive.ui.screens.AddPlaylistScreen
import com.tulaza.tvlive.ui.screens.HomeScreen
import com.tulaza.tvlive.ui.screens.PlayerScreen
import com.tulaza.tvlive.ui.screens.SettingsScreen
import com.tulaza.tvlive.ui.theme.TVLiveTheme
import com.tulaza.tvlive.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = viewModel()
            val state by viewModel.state.collectAsState()

            TVLiveTheme(darkTheme = state.darkMode) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onChannelClick = { channel ->
                                    navController.navigate("player/${channel.id}")
                                },
                                onAddPlaylistClick = { navController.navigate("add_playlist") },
                                onSettingsClick = { navController.navigate("settings") }
                            )
                        }
                        composable(
                            route = "player/{channelId}",
                            arguments = listOf(navArgument("channelId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val channelId = backStackEntry.arguments?.getString("channelId")
                            val channel = state.allChannels.firstOrNull { it.id == channelId }
                            if (channel != null) {
                                PlayerScreen(channel = channel, onBack = { navController.popBackStack() })
                            }
                        }
                        composable("add_playlist") {
                            AddPlaylistScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("settings") {
                            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
