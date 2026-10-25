package com.borisjh.appcruduser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.borisjh.appcruduser.data.remote.UserResponse
import com.borisjh.appcruduser.ui.screens.LoginScreen
import com.borisjh.appcruduser.ui.screens.UserFormScreen
import com.borisjh.appcruduser.ui.screens.UserListScreen
import com.borisjh.appcruduser.ui.theme.AppCrudUserTheme
import com.borisjh.appcruduser.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCrudUserTheme {
                val navController = rememberNavController()
                val viewModel: UserViewModel = viewModel()
                var editingUser by remember { mutableStateOf<UserResponse?>(null) }

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                navController.navigate("userList") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("userList") {
                        UserListScreen(
                            viewModel = viewModel,
                            onAddUser = {
                                editingUser = null
                                navController.navigate("userForm")
                            },
                            onEditUser = { user ->
                                editingUser = user
                                navController.navigate("userForm")
                            },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("userList") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("userForm") {
                        UserFormScreen(
                            viewModel = viewModel,
                            userToEdit = editingUser,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
