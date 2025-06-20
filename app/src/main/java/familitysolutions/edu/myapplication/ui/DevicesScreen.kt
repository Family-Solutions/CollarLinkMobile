package familitysolutions.edu.myapplication.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import familitysolutions.edu.myapplication.model.Collar
import familitysolutions.edu.myapplication.viewmodel.DeviceViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DevicesScreen(
    navController: NavController,
    viewModel: DeviceViewModel = hiltViewModel()
) {
    val collars by viewModel.collars.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCollars()
    }

    Scaffold(
        floatingActionButton = {
            Button(
                onClick = { navController.navigate("add_device") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(56.dp)
            ) {
                Text("+", fontSize = 24.sp)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (collars.isEmpty() && !isLoading) {
                 Text("No hay dispositivos. Agregue uno.", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(collars) { collar ->
                        DeviceItem(collar = collar, onClick = {
                            navController.navigate("edit_device/${collar.id}")
                        })
                    }
                }
            }
             // Spacer to push FAB to bottom if content is short
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun DeviceItem(collar: Collar, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ID: ${collar.id}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Usuario: ${collar.username}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Serial: ${collar.serialNumber}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Modelo: ${collar.model}")
        }
    }
} 