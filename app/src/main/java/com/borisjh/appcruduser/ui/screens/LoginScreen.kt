package com.borisjh.appcruduser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.borisjh.appcruduser.ui.theme.*
import com.borisjh.appcruduser.ui.viewmodel.UiState
import com.borisjh.appcruduser.ui.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "USER MANAGEMENT",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Minimalist Security Access",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = TextSecondary) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = TextSecondary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.login(email, password, onLoginSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextPrimary,
                    contentColor = BlackBackground
                )
            ) {
                if (viewModel.uiState is UiState.Loading) {
                    CircularProgressIndicator(color = BlackBackground, modifier = Modifier.size(24.dp))
                } else {
                    Text("LOGIN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            if (viewModel.uiState is UiState.Error) {
                Text(
                    text = (viewModel.uiState as UiState.Error).message,
                    color = ErrorColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}
