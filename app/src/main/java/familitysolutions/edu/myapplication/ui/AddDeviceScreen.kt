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
import familitysolutions.edu.myapplication.viewmodel.DeviceViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(
    navController: NavController,
    viewModel: DeviceViewModel = hiltViewModel()
) {
    var serialNumber by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Añadir Dispositivo", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = serialNumber,
                onValueChange = { serialNumber = it },
                label = { Text("Número de Serie del Collar") },
                modifier = Modifier.fillMaxWidth(),
                isError = inputError != null
            )
            if (inputError != null) {
                Text(inputError!!, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Modelo (ej. T-Beam v1.1)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val serial = serialNumber.toLongOrNull()
                    if (serial == null) {
                        inputError = "El número de serie debe ser un número"
                        return@Button
                    }
                    if (model.isBlank()) {
                        inputError = "El modelo es obligatorio"
                        return@Button
                    }
                    inputError = null
                    viewModel.createCollar(serial, model) { success ->
                        if (success) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar")
            }
        }
    }
} 