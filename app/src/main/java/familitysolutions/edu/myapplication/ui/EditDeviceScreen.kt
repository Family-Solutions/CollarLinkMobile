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

    var expanded by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf<Pet?>(null) }

    LaunchedEffect(Unit) {
        petViewModel.getPetsForCurrentUser()
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (collar != null) {
                Text("Editar Dispositivo", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))

                Text("Mascota asociada:", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))

                when (val state = petState) {
                    is PetsState.Success -> {
                        val unassignedPets = state.pets.filter { it.collarId == null || it.collarId == 0L }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedPet?.name ?: collar.pet?.name ?: "Seleccionar mascota",
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
                    }
                    is PetsState.Loading -> CircularProgressIndicator()
                    is PetsState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                    else -> Text("Cargando mascotas...")
                }


                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        selectedPet?.let { pet ->
                            deviceViewModel.assignPetToCollar(collar.id.toString(), pet.id) { success ->
                                if (success) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedPet != null
                ) {
                    Text("Aceptar")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        deviceViewModel.deleteCollar(collar.id.toString())
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar")
                }
            } else {
                Text("Dispositivo no encontrado.")
            }
        }
    }
} 