package familitysolutions.edu.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import familitysolutions.edu.myapplication.model.*
import familitysolutions.edu.myapplication.network.ApiService
import familitysolutions.edu.myapplication.network.PetRequest
import familitysolutions.edu.myapplication.network.UpdatePetRequest
import familitysolutions.edu.myapplication.network.UpdatePetCollarRequest
import familitysolutions.edu.myapplication.network.RetrofitInstance
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
                val token = dataStoreManager.getToken.first() ?: ""
                
                // Crear una instancia temporal de Retrofit con el token
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val response = apiService.getPetsByUsername(username)
                if (response.isSuccessful) {
                    response.body()?.let { pets ->
                        _petsState.value = PetsState.Success(pets)
                    } ?: run {
                        _petsState.value = PetsState.Error("No se encontraron mascotas")
                    }
                } else {
                    _petsState.value = PetsState.Error("Error al obtener mascotas: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _petsState.value = PetsState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun createPet(name: String, species: String, breed: String, gender: String, age: Int, collarId: Long?) {
        viewModelScope.launch {
            _petsState.value = PetsState.Loading
            try {
                val username = dataStoreManager.getUsername.first() ?: ""
                val token = dataStoreManager.getToken.first() ?: ""
                
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val petRequest = PetRequest(
                    username = username,
                    collarId = collarId,
                    name = name,
                    species = species,
                    breed = breed,
                    gender = gender,
                    age = age
                )
                
                val response = apiService.createPet(petRequest)
                if (response.isSuccessful) {
                    response.body()?.let { pet ->
                        _petsState.value = PetsState.PetCreated(pet)
                        // Recargar la lista de mascotas
                        getPetsForCurrentUser()
                    } ?: run {
                        _petsState.value = PetsState.Error("Error al crear mascota")
                    }
                } else {
                    _petsState.value = PetsState.Error("Error al crear mascota: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _petsState.value = PetsState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun updatePet(petId: Long, name: String, species: String, breed: String, gender: String, age: Int) {
        viewModelScope.launch {
            _petsState.value = PetsState.Loading
            try {
                val token = dataStoreManager.getToken.first() ?: ""
                
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val updatePetRequest = UpdatePetRequest(
                    name = name,
                    species = species,
                    breed = breed,
                    gender = gender,
                    age = age
                )
                
                val response = apiService.updatePet(petId, updatePetRequest)
                if (response.isSuccessful) {
                    response.body()?.let { pet ->
                        _petsState.value = PetsState.PetUpdated(pet)
                        // Recargar la lista de mascotas
                        getPetsForCurrentUser()
                    } ?: run {
                        _petsState.value = PetsState.Error("Error al actualizar mascota")
                    }
                } else {
                    _petsState.value = PetsState.Error("Error al actualizar mascota: ${response.code()} - ${response.errorBody()?.string()}")
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
                val token = dataStoreManager.getToken.first() ?: ""
                
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val response = apiService.deletePet(petId)
                if (response.isSuccessful) {
                    _petsState.value = PetsState.PetDeleted
                    // Recargar la lista de mascotas
                    getPetsForCurrentUser()
                } else {
                    _petsState.value = PetsState.Error("Error al eliminar mascota: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _petsState.value = PetsState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun updatePetCollar(petId: Long, collarId: Long?, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _petsState.value = PetsState.Loading
            try {
                val token = dataStoreManager.getToken.first() ?: ""
                
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val updatePetCollarRequest = UpdatePetCollarRequest(
                    collarId = collarId ?: 0L
                )
                
                val response = apiService.updatePetCollar(petId, updatePetCollarRequest)
                if (response.isSuccessful) {
                    response.body()?.let { pet ->
                        _petsState.value = PetsState.PetUpdated(pet)
                        // Recargar la lista de mascotas
                        getPetsForCurrentUser()
                        callback(true)
                    } ?: run {
                        _petsState.value = PetsState.Error("Error al actualizar collar de mascota")
                        callback(false)
                    }
                } else {
                    _petsState.value = PetsState.Error("Error al actualizar collar de mascota: ${response.code()} - ${response.errorBody()?.string()}")
                    callback(false)
                }
            } catch (e: Exception) {
                _petsState.value = PetsState.Error("Error de conexión: ${e.message}")
                callback(false)
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