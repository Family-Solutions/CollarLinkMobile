package familitysolutions.edu.myapplication.ui

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import familitysolutions.edu.myapplication.model.Pet
import familitysolutions.edu.myapplication.viewmodel.PetViewModel
import familitysolutions.edu.myapplication.viewmodel.PetsState

@Composable
fun PetsScreen(viewModel: PetViewModel = hiltViewModel()) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Pair<Boolean, Pet?>>(false to null) }

    LaunchedEffect(Unit) {
        viewModel.getPetsForCurrentUser()
    }

    val petsState by viewModel.petsState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mascotas", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        when (petsState) {
            is PetsState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PetsState.Error -> {
                Text((petsState as PetsState.Error).message, color = Color.Red)
            }
            is PetsState.Success -> {
                val pets = (petsState as PetsState.Success).pets
                if (pets.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay mascotas. Agregue una.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(Modifier.weight(1f)) {
                        items(pets) { pet ->
                            PetCardFigma(
                                pet = pet,
                                onEdit = { showEditDialog = true to pet },
                                onDelete = {
                                    viewModel.deletePet(pet.id)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
            else -> {}
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
            ) {
                Text("+", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            }
        }
    }

    if (showAddDialog) {
        AddEditPetDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, species, breed, gender, age, collarId ->
                viewModel.createPet(name, species, breed, gender, age, collarId)
                showAddDialog = false
            }
        )
    }
    
    if (showEditDialog.first && showEditDialog.second != null) {
        AddEditPetDialog(
            pet = showEditDialog.second,
            onDismiss = { showEditDialog = false to null },
            onSave = { name, species, breed, gender, age, collarId ->
                viewModel.updatePet(showEditDialog.second!!.id, name, species, breed, gender, age)
                // Si hay un collarId diferente, actualizar la asociación
                if (collarId != showEditDialog.second!!.collarId) {
                    viewModel.updatePetCollar(showEditDialog.second!!.id, collarId)
                }
                showEditDialog = false to null
            }
        )
    }
}

@Composable
fun PetCardFigma(pet: Pet, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("${pet.name}", style = MaterialTheme.typography.titleMedium)
            Text("Especie: ${pet.species}")
            Text("Raza: ${pet.breed}")
            Text("Género: ${pet.gender}")
            Text("Edad: ${pet.age} años")
            Text("Dispositivo: ${if (pet.collarId != null && pet.collarId != 0L) "ID ${pet.collarId}" else "Sin dispositivo"}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPetDialog(
    pet: Pet? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int, Long?) -> Unit
) {
    var name by remember { mutableStateOf(pet?.name ?: "") }
    var species by remember { mutableStateOf(pet?.species ?: "") }
    var breed by remember { mutableStateOf(pet?.breed ?: "") }
    var gender by remember { mutableStateOf(pet?.gender ?: "Macho") }
    var age by remember { mutableStateOf(pet?.age?.toString() ?: "") }
    var collarId by remember { mutableStateOf(pet?.collarId?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (pet == null) "Añadir Mascota" else "Editar Mascota") },
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
                
                // Dropdown para género
                var genderExpanded by remember { mutableStateOf(false) }
                
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
                OutlinedTextField(
                    value = collarId, 
                    onValueChange = { collarId = it }, 
                    label = { Text("ID del Dispositivo (Opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val collarIdLong = if (collarId.isNotBlank()) collarId.toLongOrNull() else null
                    onSave(
                        name,
                        species,
                        breed,
                        gender,
                        age.toIntOrNull() ?: 0,
                        collarIdLong
                    )
                },
                enabled = name.isNotBlank() && species.isNotBlank() && breed.isNotBlank() && age.isNotBlank()
            ) {
                Text(if (pet == null) "Agregar" else "Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
} 