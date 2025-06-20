package familitysolutions.edu.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import familitysolutions.edu.myapplication.model.*
import familitysolutions.edu.myapplication.network.ApiService
import familitysolutions.edu.myapplication.util.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _petsState = MutableStateFlow<PetsState>(PetsState.Initial)
    val petsState: StateFlow<PetsState> = _petsState

    fun getPetsForCurrentUser() {
        viewModelScope.launch {
            _petsState.value = PetsState.Loading
            try {
                val username = dataStoreManager.getUsername.first() ?: ""
                val response = apiService.getPetsByUsername(username)
                if (response.isSuccessful) {
                    response.body()?.let { pets ->
                        _petsState.value = PetsState.Success(pets)
                    } ?: run {
                        _petsState.value = PetsState.Error("No se encontraron mascotas")
                    }
                } else {
                    _petsState.value = PetsState.Error("Error al obtener mascotas: ${response.code()}")
                }
            } catch (e: Exception) {
                _petsState.value = PetsState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun createPet(petRequest: PetRequest) {
        viewModelScope.launch {
            _petsState.value = PetsState.Loading
            try {
                val response = apiService.createPet(petRequest)
                if (response.isSuccessful) {
                    response.body()?.let { pet ->
                        _petsState.value = PetsState.PetCreated(pet)
                    } ?: run {
                        _petsState.value = PetsState.Error("Error al crear mascota")
                    }
                } else {
                    _petsState.value = PetsState.Error("Error al crear mascota: ${response.code()}")
                }
            } catch (e: Exception) {
                _petsState.value = PetsState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun updatePet(petId: Long, updatePetRequest: UpdatePetRequest) {
        viewModelScope.launch {
            _petsState.value = PetsState.Loading
            try {
                val response = apiService.updatePet(petId, updatePetRequest)
                if (response.isSuccessful) {
                    response.body()?.let { pet ->
                        _petsState.value = PetsState.PetUpdated(pet)
                    } ?: run {
                        _petsState.value = PetsState.Error("Error al actualizar mascota")
                    }
                } else {
                    _petsState.value = PetsState.Error("Error al actualizar mascota: ${response.code()}")
                }
            } catch (e: Exception) {
                _petsState.value = PetsState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun deletePet(petId: Long) {
        viewModelScope.launch {
            _petsState.value = PetsState.Loading
            try {
                val response = apiService.deletePet(petId)
                if (response.isSuccessful) {
                    _petsState.value = PetsState.PetDeleted
                } else {
                    _petsState.value = PetsState.Error("Error al eliminar mascota: ${response.code()}")
                }
            } catch (e: Exception) {
                _petsState.value = PetsState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}

sealed class PetsState {
    object Initial : PetsState()
    object Loading : PetsState()
    data class Success(val pets: List<Pet>) : PetsState()
    data class PetCreated(val pet: Pet) : PetsState()
    data class PetUpdated(val pet: Pet) : PetsState()
    object PetDeleted : PetsState()
    data class Error(val message: String) : PetsState()
} 