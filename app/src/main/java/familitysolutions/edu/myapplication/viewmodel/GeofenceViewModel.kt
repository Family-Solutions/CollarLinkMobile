package familitysolutions.edu.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import familitysolutions.edu.myapplication.model.*
import familitysolutions.edu.myapplication.network.ApiService
import familitysolutions.edu.myapplication.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@HiltViewModel
class GeofenceViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _geofencesState = MutableStateFlow<GeofencesState>(GeofencesState.Initial)
    val geofencesState: StateFlow<GeofencesState> = _geofencesState

    private val dataStore = application.dataStore
    private val TOKEN_KEY = stringPreferencesKey("token")

    private suspend fun getApiServiceWithToken(): ApiService {
        val prefs = dataStore.data.first()
        val token = prefs[TOKEN_KEY]
        val retrofit = RetrofitInstance.getRetrofit(token)
        return retrofit.create(ApiService::class.java)
    }

    fun getGeofencesByUsername(username: String) {
        viewModelScope.launch {
            _geofencesState.value = GeofencesState.Loading
            try {
                val apiService = getApiServiceWithToken()
                val response = apiService.getGeofencesByUsername(username)
                if (response.isSuccessful) {
                    response.body()?.let { geofences ->
                        _geofencesState.value = GeofencesState.Success(geofences)
                    } ?: run {
                        _geofencesState.value = GeofencesState.Error("No se encontraron geocercas")
                    }
                } else {
                    _geofencesState.value = GeofencesState.Error("Error al obtener geocercas: ${response.code()}")
                }
            } catch (e: Exception) {
                _geofencesState.value = GeofencesState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun createGeofence(geofenceRequest: GeofenceRequest) {
        viewModelScope.launch {
            _geofencesState.value = GeofencesState.Loading
            try {
                val apiService = getApiServiceWithToken()
                val response = apiService.createGeofence(geofenceRequest)
                if (response.isSuccessful) {
                    response.body()?.let { geofence ->
                        _geofencesState.value = GeofencesState.GeofenceCreated(geofence)
                    } ?: run {
                        _geofencesState.value = GeofencesState.Error("Error al crear geocerca")
                    }
                } else {
                    _geofencesState.value = GeofencesState.Error("Error al crear geocerca: ${response.code()}")
                }
            } catch (e: Exception) {
                _geofencesState.value = GeofencesState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun updateGeofence(geofenceId: Long, updateGeofenceRequest: UpdateGeofenceRequest) {
        viewModelScope.launch {
            _geofencesState.value = GeofencesState.Loading
            try {
                val apiService = getApiServiceWithToken()
                val response = apiService.updateGeofence(geofenceId, updateGeofenceRequest)
                if (response.isSuccessful) {
                    response.body()?.let { geofence ->
                        _geofencesState.value = GeofencesState.GeofenceUpdated(geofence)
                    } ?: run {
                        _geofencesState.value = GeofencesState.Error("Error al actualizar geocerca")
                    }
                } else {
                    _geofencesState.value = GeofencesState.Error("Error al actualizar geocerca: ${response.code()}")
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
                val apiService = getApiServiceWithToken()
                val response = apiService.deleteGeofence(geofenceId)
                if (response.isSuccessful) {
                    _geofencesState.value = GeofencesState.GeofenceDeleted
                } else {
                    _geofencesState.value = GeofencesState.Error("Error al eliminar geocerca: ${response.code()}")
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