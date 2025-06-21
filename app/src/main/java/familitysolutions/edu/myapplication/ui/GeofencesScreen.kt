package familitysolutions.edu.myapplication.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import familitysolutions.edu.myapplication.model.Geofence
import familitysolutions.edu.myapplication.viewmodel.GeofenceViewModel
import familitysolutions.edu.myapplication.viewmodel.GeofencesState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofencesScreen(
    navController: NavController,
    viewModel: GeofenceViewModel = hiltViewModel()
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Pair<Boolean, Geofence?>>(false to null) }

    LaunchedEffect(Unit) {
        viewModel.getGeofencesForCurrentUser()
    }

    val geofencesState by viewModel.geofencesState.collectAsState()

    Scaffold(
        floatingActionButton = {
            Button(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(56.dp)
            ) {
                Text("+ Crear Geocerca", fontSize = 16.sp)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Geocercas", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            when (geofencesState) {
                is GeofencesState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is GeofencesState.Error -> {
                    Text((geofencesState as GeofencesState.Error).message, color = Color.Red)
                }
                is GeofencesState.Success -> {
                    val geofences = (geofencesState as GeofencesState.Success).geofences
                    if (geofences.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No hay geocercas creadas", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Crea tu primera geocerca para comenzar", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(geofences) { geofence ->
                                GeofenceCard(
                                    geofence = geofence,
                                    onEdit = { showEditDialog = true to geofence },
                                    onDelete = {
                                        viewModel.deleteGeofence(geofence.id)
                                    }
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
            
            // Spacer to push FAB to bottom if content is short
            Spacer(modifier = Modifier.height(64.dp))
        }
    }

    // Diálogo para crear nueva geocerca
    if (showCreateDialog) {
        CreateEditGeofenceDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { name, latitude, longitude, radius ->
                viewModel.createGeofence(name, latitude, longitude, radius)
                showCreateDialog = false
            }
        )
    }

    // Diálogo para editar geocerca
    if (showEditDialog.first && showEditDialog.second != null) {
        CreateEditGeofenceDialog(
            geofence = showEditDialog.second,
            onDismiss = { showEditDialog = false to null },
            onSave = { name, latitude, longitude, radius ->
                viewModel.updateGeofence(showEditDialog.second!!.id, name, latitude, longitude, radius)
                showEditDialog = false to null
            }
        )
    }
}

@Composable
fun GeofenceCard(geofence: Geofence, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${geofence.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Latitud: ${geofence.latitude}")
            Text("Longitud: ${geofence.longitude}")
            Text("Radio: ${geofence.radius} metros")
            Text("Usuario: ${geofence.username}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditGeofenceDialog(
    geofence: Geofence? = null,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf(geofence?.name ?: "") }
    var latitude by remember { mutableStateOf(geofence?.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(geofence?.longitude?.toString() ?: "") }
    var radius by remember { mutableStateOf(geofence?.radius?.toString() ?: "500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (geofence == null) "Crear Nueva Geocerca" else "Editar Geocerca") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Nombre de la Geocerca") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = latitude, 
                    onValueChange = { latitude = it }, 
                    label = { Text("Latitud") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = longitude, 
                    onValueChange = { longitude = it }, 
                    label = { Text("Longitud") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = radius, 
                    onValueChange = { radius = it }, 
                    label = { Text("Radio (metros)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (geofence == null) {
                    Text(
                        "Nota: Para obtener las coordenadas exactas, puedes usar Google Maps o cualquier aplicación de mapas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lat = latitude.toDoubleOrNull() ?: 0.0
                    val lng = longitude.toDoubleOrNull() ?: 0.0
                    val rad = radius.toIntOrNull() ?: 500
                    onSave(name, lat, lng, rad)
                },
                enabled = name.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank() && radius.isNotBlank()
            ) {
                Text(if (geofence == null) "Crear" else "Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
} 