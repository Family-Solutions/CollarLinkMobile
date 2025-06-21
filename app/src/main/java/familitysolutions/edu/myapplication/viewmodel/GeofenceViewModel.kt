package familitysolutions.edu.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import familitysolutions.edu.myapplication.model.*
import familitysolutions.edu.myapplication.network.ApiService
import familitysolutions.edu.myapplication.network.GeofenceRequest
import familitysolutions.edu.myapplication.network.UpdateGeofenceRequest
import familitysolutions.edu.myapplication.network.RetrofitInstance
import familitysolutions.edu.myapplication.util.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeofenceViewModel @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _geofencesState = MutableStateFlow<GeofencesState>(GeofencesState.Initial)
    val geofencesState: StateFlow<GeofencesState> = _geofencesState

    fun getGeofencesForCurrentUser() {
        viewModelScope.launch {
            _geofencesState.value = GeofencesState.Loading
            try {
                val username = dataStoreManager.getUsername.first() ?: ""
                val token = dataStoreManager.getToken.first() ?: ""
                
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val response = apiService.getGeofencesByUsername(username)
                if (response.isSuccessful) {
                    response.body()?.let { geofences ->
                        _geofencesState.value = GeofencesState.Success(geofences)
                    } ?: run {
                        _geofencesState.value = GeofencesState.Error("No se encontraron geocercas")
                    }
                } else {
                    _geofencesState.value = GeofencesState.Error("Error al obtener geocercas: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _geofencesState.value = GeofencesState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun createGeofence(name: String, latitude: Double, longitude: Double, radius: Int) {
        viewModelScope.launch {
            _geofencesState.value = GeofencesState.Loading
            try {
                val username = dataStoreManager.getUsername.first() ?: ""
                val token = dataStoreManager.getToken.first() ?: ""
                
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val geofenceRequest = GeofenceRequest(
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                    radius = radius,
                    username = username
                )
                
                val response = apiService.createGeofence(geofenceRequest)
                if (response.isSuccessful) {
                    response.body()?.let { geofence ->
                        _geofencesState.value = GeofencesState.GeofenceCreated(geofence)
                        // Recargar la lista de geocercas
                        getGeofencesForCurrentUser()
                    } ?: run {
                        _geofencesState.value = GeofencesState.Error("Error al crear geocerca")
                    }
                } else {
                    _geofencesState.value = GeofencesState.Error("Error al crear geocerca: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _geofencesState.value = GeofencesState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun updateGeofence(geofenceId: Long, name: String, latitude: Double, longitude: Double, radius: Int) {
        viewModelScope.launch {
            _geofencesState.value = GeofencesState.Loading
            try {
                val token = dataStoreManager.getToken.first() ?: ""
                
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val updateGeofenceRequest = UpdateGeofenceRequest(
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                    radius = radius
                )
                
                val response = apiService.updateGeofence(geofenceId, updateGeofenceRequest)
                if (response.isSuccessful) {
                    response.body()?.let { geofence ->
                        _geofencesState.value = GeofencesState.GeofenceUpdated(geofence)
                        // Recargar la lista de geocercas
                        getGeofencesForCurrentUser()
                    } ?: run {
                        _geofencesState.value = GeofencesState.Error("Error al actualizar geocerca")
                    }
                } else {
                    _geofencesState.value = GeofencesState.Error("Error al actualizar geocerca: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _geofencesState.value = GeofencesState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun deleteGeofence(geofenceId: Long) {
        viewModelScope.launch {
            _geofencesState.value = GeofencesState.Loading
            try {
                val token = dataStoreManager.getToken.first() ?: ""
                
                val retrofit = RetrofitInstance.getRetrofit(token)
                val apiService = retrofit.create(ApiService::class.java)
                
                val response = apiService.deleteGeofence(geofenceId)
                if (response.isSuccessful) {
                    _geofencesState.value = GeofencesState.GeofenceDeleted
                    // Recargar la lista de geocercas
                    getGeofencesForCurrentUser()
                } else {
                    _geofencesState.value = GeofencesState.Error("Error al eliminar geocerca: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _geofencesState.value = GeofencesState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}

sealed class GeofencesState {
    object Initial : GeofencesState()
    object Loading : GeofencesState()
    data class Success(val geofences: List<Geofence>) : GeofencesState()
    data class GeofenceCreated(val geofence: Geofence) : GeofencesState()
    data class GeofenceUpdated(val geofence: Geofence) : GeofencesState()
    object GeofenceDeleted : GeofencesState()
    data class Error(val message: String) : GeofencesState()
} 