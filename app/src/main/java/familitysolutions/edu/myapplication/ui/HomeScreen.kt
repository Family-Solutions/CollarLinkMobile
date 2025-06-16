package familitysolutions.edu.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Menu", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            MenuItem(
                icon = Icons.Filled.LocationOn,
                text = "Localización",
                onClick = { navController.navigate("map") }
            )
            Spacer(modifier = Modifier.height(24.dp))
            MenuItem(
                icon = Icons.Filled.Person,
                text = "Mascotas",
                onClick = { navController.navigate("pets") }
            )
            Spacer(modifier = Modifier.height(24.dp))
            MenuItem(
                icon = Icons.Filled.Settings,
                text = "Dispositivos",
                onClick = { navController.navigate("devices") }
            )
            Spacer(modifier = Modifier.height(24.dp))
            MenuItem(
                icon = Icons.Filled.Place,
                text = "GeoCercas",
                onClick = { navController.navigate("geofences") }
            )
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.titleLarge)
    }
} 