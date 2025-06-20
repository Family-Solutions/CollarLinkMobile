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
import familitysolutions.edu.myapplication.model.PetRequest
import familitysolutions.edu.myapplication.model.UpdatePetRequest
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
                LazyColumn(Modifier.weight(1f)) {
                    items(pets) { pet ->
                        PetCardFigma(
                            pet = pet,
                            onEdit = { showEditDialog = true to pet },
                            onDelete = {
                                viewModel.deletePet(pet.id)
                                viewModel.getPetsForCurrentUser()
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
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
                viewModel.createPet(
                    PetRequest(
                        username = "",
                        collarId = collarId,
                        name = name,
                        species = species,
                        breed = breed,
                        gender = gender,
                        age = age
                    )
                )
                showAddDialog = false
                viewModel.getPetsForCurrentUser()
            }
        )
    }
    if (showEditDialog.first && showEditDialog.second != null) {
        AddEditPetDialog(
            pet = showEditDialog.second,
            onDismiss = { showEditDialog = false to null },
            onSave = { name, species, breed, gender, age, _ ->
                viewModel.updatePet(
                    showEditDialog.second!!.id,
                    UpdatePetRequest(name, species, breed, gender, age)
                )
                showEditDialog = false to null
                viewModel.getPetsForCurrentUser()
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
            Text("Edad: ${pet.age} años")
            Text("Dispositivo: ${pet.collarId}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}

@Composable
fun AddEditPetDialog(
    pet: Pet? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int, Long) -> Unit
) {
    var name by remember { mutableStateOf(pet?.name ?: "") }
    var species by remember { mutableStateOf(pet?.species ?: "") }
    var breed by remember { mutableStateOf(pet?.breed ?: "") }
    var gender by remember { mutableStateOf(pet?.gender ?: "") }
    var age by remember { mutableStateOf(pet?.age?.toString() ?: "") }
    var collarId by remember { mutableStateOf(pet?.collarId?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (pet == null) "Añadir Mascota" else "Editar Mascota") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = species, onValueChange = { species = it }, label = { Text("Especie") })
                OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Raza") })
                OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Género") })
                OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Edad") })
                OutlinedTextField(value = collarId, onValueChange = { collarId = it }, label = { Text("Collar") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        name,
                        species,
                        breed,
                        gender,
                        age.toIntOrNull() ?: 0,
                        collarId.toLongOrNull() ?: 0L
                    )
                }
            ) {
                Text(if (pet == null) "Agregar" else "Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
} 