package familitysolutions.edu.myapplication.viewmodel

import androidx.lifecycle.ViewModel
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
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.signIn(SignInRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let { signInResponse ->
                        dataStoreManager.saveToken(signInResponse.token)
                        dataStoreManager.saveUsername(signInResponse.username)
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

    suspend fun getToken(): String? = dataStoreManager.getToken.first()
    suspend fun getUsername(): String? = dataStoreManager.getUsername.first()
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
} 