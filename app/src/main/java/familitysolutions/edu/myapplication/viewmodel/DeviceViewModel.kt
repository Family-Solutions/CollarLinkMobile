package familitysolutions.edu.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import familitysolutions.edu.myapplication.model.AssignPetToCollarRequest
import familitysolutions.edu.myapplication.model.Collar
import familitysolutions.edu.myapplication.model.CreateCollarRequest
import familitysolutions.edu.myapplication.network.ApiService
import familitysolutions.edu.myapplication.network.RetrofitInstance
import familitysolutions.edu.myapplication.util.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _collars = MutableStateFlow<List<Collar>>(emptyList())
    val collars: StateFlow<List<Collar>> = _collars

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    fun getCollars() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = dataStoreManager.getUsername.first()
                val token = dataStoreManager.getToken.first()
                if (username != null && !token.isNullOrEmpty()) {
                    val apiService = RetrofitInstance.getRetrofit(token).create(ApiService::class.java)
                    val response = apiService.getCollarsByUsername(username)
                    if (response.isSuccessful) {
                        _collars.value = response.body() ?: emptyList()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorMessage.value = "Error al obtener los collares: ${response.code()} - $errorBody"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Excepción al obtener collares: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createCollar(serialNumber: Long, model: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val username = dataStoreManager.getUsername.first()
                val token = dataStoreManager.getToken.first()
                if (username != null && !token.isNullOrEmpty()) {
                    val apiService = RetrofitInstance.getRetrofit(token).create(ApiService::class.java)
                    val request = CreateCollarRequest(serialNumber, model, username)
                    val response = apiService.createCollar(request)
                    if (response.isSuccessful) {
                        getCollars()
                        callback(true)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorMessage.value = "Error al crear el collar: ${response.code()} - $errorBody"
                        callback(false)
                    }
                } else {
                    _errorMessage.value = "No hay usuario o token"
                    callback(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Excepción al crear collar: ${e.message}"
                callback(false)
            }
        }
    }

    fun deleteCollar(collarId: String) {
        viewModelScope.launch {
            try {
                val token = dataStoreManager.getToken.first()
                if (!token.isNullOrEmpty()) {
                    val apiService = RetrofitInstance.getRetrofit(token).create(ApiService::class.java)
                    val response = apiService.deleteCollar(collarId)
                    if (response.isSuccessful) {
                        getCollars()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorMessage.value = "Error al eliminar el collar: ${response.code()} - $errorBody"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Excepción al eliminar collar: ${e.message}"
            }
        }
    }

    fun assignPetToCollar(collarId: String, petId: Long, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val token = dataStoreManager.getToken.first()
                if (!token.isNullOrEmpty()) {
                    val apiService = RetrofitInstance.getRetrofit(token).create(ApiService::class.java)
                    val request = AssignPetToCollarRequest(petId)
                    val response = apiService.assignPetToCollar(collarId, request)
                    if (response.isSuccessful) {
                        getCollars()
                        callback(true)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorMessage.value = "Error al asignar mascota: ${response.code()} - $errorBody"
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Excepción al asignar mascota: ${e.message}"
                callback(false)
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
} 