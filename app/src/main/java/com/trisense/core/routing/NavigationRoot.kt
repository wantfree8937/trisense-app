package com.trisense.core.routing

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trisense.domain.model.GameType
import com.trisense.presentation.home.HomeScreen
import com.trisense.presentation.reflex.ReflexScreen
import com.trisense.presentation.timing.TimingScreen
import com.trisense.presentation.number.NumberScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeScreenRoute) {
        composable<HomeScreenRoute> {
            HomeScreen(
                onNavigateToGame = { gameType ->
                    val route = when(gameType) {
                        GameType.REFLEX -> ReflexScreenRoute
                        GameType.TIMING -> TimingScreenRoute
                        GameType.NUMBER -> NumberScreenRoute
                    }
                    navController.navigate(route)
                }
            )
        }
        
        composable<ReflexScreenRoute> {
           ReflexScreen(onBack = { navController.popBackStack() })
        }
        
        composable<TimingScreenRoute> {
            TimingScreen(onBack = { navController.popBackStack() })
        }
        
        composable<NumberScreenRoute> {
             NumberScreen(onBack = { navController.popBackStack() })
        }
    }
}

// Routes
@Serializable
object HomeScreenRoute

@Serializable
object ReflexScreenRoute

@Serializable
object TimingScreenRoute

@Serializable
object NumberScreenRoute
