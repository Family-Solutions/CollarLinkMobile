package familitysolutions.edu.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import familitysolutions.edu.myapplication.ui.*

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
            PetsScreen()
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
        composable("add_device") {
            AddDeviceScreen(navController = navController)
        }
        composable(
            route = "edit_device/{collarId}",
            arguments = listOf(navArgument("collarId") { type = NavType.LongType })
        ) { backStackEntry ->
            val collarId = backStackEntry.arguments?.getLong("collarId")
            if (collarId != null && collarId != 0L) {
                EditDeviceScreen(navController = navController, collarId = collarId)
            }
        }
    }
} 