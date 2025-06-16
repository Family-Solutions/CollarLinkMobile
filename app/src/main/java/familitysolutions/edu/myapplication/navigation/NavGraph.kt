package familitysolutions.edu.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import familitysolutions.edu.myapplication.ui.HomeScreen
import familitysolutions.edu.myapplication.ui.LoginScreen
import familitysolutions.edu.myapplication.ui.PetsScreen
import familitysolutions.edu.myapplication.ui.GeofencesScreen
import familitysolutions.edu.myapplication.ui.DevicesScreen
import familitysolutions.edu.myapplication.ui.MapScreen
import familitysolutions.edu.myapplication.ui.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("map") {
            MapScreen(navController = navController)
        }
        composable("pets") {
            PetsScreen(navController = navController)
        }
        composable("devices") {
            DevicesScreen(navController = navController)
        }
        composable("geofences") {
            GeofencesScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
} 