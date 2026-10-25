package com.borisjh.appcruduser.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borisjh.appcruduser.data.remote.*
import kotlinx.coroutines.launch

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val message: String) : UiState
    data class Error(val message: String) : UiState
}

class UserViewModel : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
        private set

    var users by mutableStateOf<List<UserResponse>>(emptyList())
        private set

    var searchedUserResult by mutableStateOf<UserResponse?>(null)
    var showUserDetailDialog by mutableStateOf(false)

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    var searchQuery by mutableStateOf("")

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                val response = ApiClient.authService.login(LoginRequest(email, password))
                ApiClient.setAuthToken(response.accessToken)
                isLoggedIn = true
                uiState = UiState.Idle
                loadUsers()
                onSuccess()
            } catch (e: Exception) {
                uiState = UiState.Error(e.localizedMessage ?: "Error al iniciar sesión (401/404)")
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                users = ApiClient.userService.getAllUsers()
                uiState = UiState.Idle
            } catch (e: Exception) {
                uiState = UiState.Error(e.localizedMessage ?: "Error al cargar usuarios")
            }
        }
    }

    fun getUserById(id: String) {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                searchedUserResult = ApiClient.userService.getUserById(id)
                showUserDetailDialog = true
                uiState = UiState.Idle
            } catch (e: Exception) {
                uiState = UiState.Error(e.localizedMessage ?: "Usuario no encontrado (404)")
            }
        }
    }

    fun createUser(id: String, name: String, email: String, password: String, role: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                ApiClient.userService.createUser(UserRequest(id, name, email, password, role))
                loadUsers()
                uiState = UiState.Success("Usuario creado exitosamente")
                onSuccess()
            } catch (e: Exception) {
                uiState = UiState.Error(e.localizedMessage ?: "Error al crear usuario")
            }
        }
    }

    fun updateUser(id: String, name: String, email: String, password: String?, role: String, status: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                ApiClient.userService.updateUser(id, UpdateUserRequest(name, email, password?.takeIf { it.isNotBlank() }, role, status))
                loadUsers()
                uiState = UiState.Success("Usuario actualizado exitosamente")
                onSuccess()
            } catch (e: Exception) {
                uiState = UiState.Error(e.localizedMessage ?: "Error al actualizar usuario")
            }
        }
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                ApiClient.userService.deleteUser(id)
                loadUsers()
                uiState = UiState.Success("Usuario eliminado")
            } catch (e: Exception) {
                uiState = UiState.Error(e.localizedMessage ?: "Error al eliminar usuario")
            }
        }
    }

    fun logout() {
        ApiClient.setAuthToken(null)
        isLoggedIn = false
        users = emptyList()
        searchedUserResult = null
        showUserDetailDialog = false
        uiState = UiState.Idle
    }
}
