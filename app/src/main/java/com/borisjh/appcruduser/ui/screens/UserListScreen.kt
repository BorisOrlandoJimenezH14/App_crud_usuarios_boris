package com.borisjh.appcruduser.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.borisjh.appcruduser.data.remote.ApiClient
import com.borisjh.appcruduser.data.remote.UserResponse
import com.borisjh.appcruduser.ui.theme.*
import com.borisjh.appcruduser.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserViewModel,
    onAddUser: () -> Unit,
    onEditUser: (UserResponse) -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    var showGetByIdDialog by remember { mutableStateOf(false) }

    val filteredUsers = viewModel.users.filter {
        it.name.contains(viewModel.searchQuery, ignoreCase = true) ||
                it.email.contains(viewModel.searchQuery, ignoreCase = true) ||
                it.role.contains(viewModel.searchQuery, ignoreCase = true) ||
                it.id.contains(viewModel.searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = BlackBackground,
        topBar = {
            TopAppBar(
                title = { Text("ADMIN CRUD DASHBOARD", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = TextSecondary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Swagger Endpoints Action Bar
            Text("SWAGGER ENDPOINTS ACTIONS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // POST /api/users
                Button(
                    onClick = onAddUser,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text("POST /users", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // GET /api/users
                Button(
                    onClick = { viewModel.loadUsers() },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text("GET /users", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // GET /api/users/{id}
                Button(
                    onClick = { showGetByIdDialog = true },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00838F)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text("GET /users/{id}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                placeholder = { Text("Search by ID, name, email, role...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardSurface,
                    unfocusedContainerColor = CardSurface
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            if (viewModel.users.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found or loading...", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredUsers) { user ->
                        UserCard(
                            user = user,
                            onEdit = { onEditUser(user) },
                            onDelete = { viewModel.deleteUser(user.id) }
                        )
                    }
                }
            }
        }
    }

    // Get User By ID Dialog
    if (showGetByIdDialog) {
        var inputId by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showGetByIdDialog = false },
            containerColor = CardSurface,
            title = { Text("GET /api/users/{id}", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter User ID to query backend:", color = TextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = inputId,
                        onValueChange = { inputId = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (inputId.isNotBlank()) {
                        showGetByIdDialog = false
                        viewModel.getUserById(inputId.trim())
                    }
                }) {
                    Text("Execute", color = TextPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGetByIdDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // User Detail Result Dialog
    if (viewModel.showUserDetailDialog && viewModel.searchedUserResult != null) {
        val u = viewModel.searchedUserResult!!
        AlertDialog(
            onDismissRequest = { viewModel.showUserDetailDialog = false },
            containerColor = CardSurface,
            title = { Text("User Found [GET /users/{id}]", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("ID: ${u.id}", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Name: ${u.name}", color = TextPrimary)
                    Text("Email: ${u.email}", color = TextSecondary)
                    Text("Role: ${u.role}", color = TextPrimary)
                    Text("Status: ${u.status}", color = Color.Green)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.showUserDetailDialog = false
                    onEditUser(u)
                }) {
                    Text("Edit [PUT]", color = TextPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showUserDetailDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun UserCard(user: UserResponse, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onEdit),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Surface(
                    color = AccentGray,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = user.role,
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "ID: ${user.id}",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = user.email,
                color = TextSecondary,
                fontSize = 13.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ${user.status}",
                    color = if (user.status == "ACTIVE") Color.Green else TextSecondary,
                    fontSize = 12.sp
                )

                Row {
                    TextButton(onClick = onEdit) {
                        Text("Edit [PUT]", color = Color(0xFF1565C0), fontSize = 12.sp)
                    }
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text("Delete [DEL]", color = ErrorColor, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = CardSurface,
            title = { Text("DELETE /api/users/{id}", color = TextPrimary) },
            text = { Text("Are you sure you want to delete ${user.name} (ID: ${user.id})?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Delete", color = ErrorColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
