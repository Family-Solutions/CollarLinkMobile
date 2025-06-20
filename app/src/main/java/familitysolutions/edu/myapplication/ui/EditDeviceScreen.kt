package familitysolutions.edu.myapplication.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import familitysolutions.edu.myapplication.model.Pet
import familitysolutions.edu.myapplication.viewmodel.DeviceViewModel
import familitysolutions.edu.myapplication.viewmodel.PetViewModel
import familitysolutions.edu.myapplication.viewmodel.PetsState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeviceScreen(
    navController: NavController,
    collarId: Long,
    deviceViewModel: DeviceViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val petState by petViewModel.petsState.collectAsState()
    val collars by deviceViewModel.collars.collectAsState()
    val collar = collars.find { it.id == collarId }
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf<familitysolutions.edu.myapplication.model.Pet?>(null) }
    var showCreatePetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        petViewModel.getPetsForCurrentUser()
        deviceViewModel.getCollars()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (collar != null) {
                Text("Editar Dispositivo", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Información del Dispositivo", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ID: ${collar.id}")
                        Text("Usuario: ${collar.username}")
                        Text("Serial: ${collar.serialNumber}")
                        Text("Modelo: ${collar.model}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mascota asociada actualmente
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mascota Asociada", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        when (val state = petState) {
                            is familitysolutions.edu.myapplication.viewmodel.PetsState.Success -> {
                                val associatedPet = state.pets.find { it.collarId == collar.id }
                                if (associatedPet != null) {
                                    Text("Nombre: ${associatedPet.name}")
                                    Text("Especie: ${associatedPet.species}")
                                    Text("Raza: ${associatedPet.breed}")
                                    Text("Género: ${associatedPet.gender}")
                                    Text("Edad: ${associatedPet.age} años")
                                } else {
                                    Text("Sin mascota asociada", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                                }
                            }
                            is familitysolutions.edu.myapplication.viewmodel.PetsState.Loading -> CircularProgressIndicator()
                            is familitysolutions.edu.myapplication.viewmodel.PetsState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                            else -> Text("Cargando mascotas...")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Asociar nueva mascota
                when (val state = petState) {
                    is familitysolutions.edu.myapplication.viewmodel.PetsState.Success -> {
                        val unassignedPets = state.pets.filter { it.collarId == null || it.collarId == 0L }
                        if (unassignedPets.isNotEmpty()) {
                            Text("Asociar a una mascota:", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedPet?.name ?: "Seleccionar mascota",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    unassignedPets.forEach { pet ->
                                        DropdownMenuItem(
                                            text = { Text(pet.name) },
                                            onClick = {
                                                selectedPet = pet
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    selectedPet?.let { pet ->
                                        petViewModel.updatePetCollar(pet.id, collar.id) { success ->
                                            if (success) {
                                                showSuccess = true
                                                showError = null
                                                petViewModel.getPetsForCurrentUser()
                                            } else {
                                                showError = "Error al asociar mascota. ¿Sesión expirada?"
                                                showSuccess = false
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = selectedPet != null
                            ) {
                                Text("Asociar Mascota")
                            }
                        } else {
                            Text("No hay mascotas disponibles para asociar", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showCreatePetDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Crear Nueva Mascota")
                            }
                        }
                    }
                    is familitysolutions.edu.myapplication.viewmodel.PetsState.Loading -> CircularProgressIndicator()
                    is familitysolutions.edu.myapplication.viewmodel.PetsState.Error -> {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showCreatePetDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Crear Nueva Mascota")
                        }
                    }
                    else -> Text("Cargando mascotas...")
                }

                if (showSuccess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¡Mascota asociada correctamente!", color = MaterialTheme.colorScheme.primary)
                }
                if (showError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(showError!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        deviceViewModel.deleteCollar(collar.id.toString())
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar Dispositivo")
                }
            } else {
                Text("Dispositivo no encontrado.")
            }
        }
    }

    // Diálogo para crear nueva mascota
    if (showCreatePetDialog) {
        CreatePetDialog(
            collarId = collarId,
            petViewModel = petViewModel,
            onDismiss = { showCreatePetDialog = false },
            onPetCreated = { 
                showCreatePetDialog = false
                showSuccess = true
                showError = null
                petViewModel.getPetsForCurrentUser()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePetDialog(
    collarId: Long,
    petViewModel: PetViewModel,
    onDismiss: () -> Unit,
    onPetCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Macho") }
    var age by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Nueva Mascota") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = species, 
                    onValueChange = { species = it }, 
                    label = { Text("Especie (ej. Perro)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = breed, 
                    onValueChange = { breed = it }, 
                    label = { Text("Raza") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = it }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Género") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Macho") },
                            onClick = { 
                                gender = "Macho"
                                genderExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hembra") },
                            onClick = { 
                                gender = "Hembra"
                                genderExpanded = false
                            }
                        )
                    }
                }
                
                OutlinedTextField(
                    value = age, 
                    onValueChange = { age = it }, 
                    label = { Text("Edad") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    petViewModel.createPet(name, species, breed, gender, age.toIntOrNull() ?: 0, collarId)
                    onPetCreated()
                },
                enabled = name.isNotBlank() && species.isNotBlank() && breed.isNotBlank() && age.isNotBlank()
            ) {
                Text("Crear Mascota")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
} 