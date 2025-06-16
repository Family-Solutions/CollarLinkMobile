package familitysolutions.edu.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import familitysolutions.edu.myapplication.network.ApiService
import familitysolutions.edu.myapplication.network.SignInRequest
import familitysolutions.edu.myapplication.network.SignUpRequest
import familitysolutions.edu.myapplication.util.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val apiService: ApiService
) : AndroidViewModel(application) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState
    private val context = getApplication<Application>().applicationContext

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.signIn(SignInRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let { signInResponse ->
                        DataStoreManager.saveToken(context, signInResponse.token)
                        DataStoreManager.saveUsername(context, signInResponse.username)
                        _authState.value = AuthState.Success(signInResponse.token)
                    } ?: run {
                        _authState.value = AuthState.Error("Respuesta vacía del servidor")
                    }
                } else {
                    _authState.value = AuthState.Error("Error de autenticación: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun signUp(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.signUp(SignUpRequest(username, password, listOf("ROLE_USER")))
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success("Usuario registrado exitosamente")
                } else {
                    _authState.value = AuthState.Error("Error en el registro: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun getToken(): String? = DataStoreManager.getToken(context).first()
    suspend fun getUsername(): String? = DataStoreManager.getUsername(context).first()
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
} 