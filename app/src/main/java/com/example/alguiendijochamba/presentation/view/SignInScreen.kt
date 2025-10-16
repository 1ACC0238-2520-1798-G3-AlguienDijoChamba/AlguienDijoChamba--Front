package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alguiendijochamba.R
import com.example.alguiendijochamba.presentation.viewmodel.SignInViewModel
import com.example.alguiendijochamba.ui.theme.AlguienDijoChambaTheme
import com.example.alguiendijochamba.ui.theme.LightBlue
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import androidx.compose.foundation.text.KeyboardOptions
import com.example.alguiendijochamba.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.sign_in_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_desc)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo de la empresa
            Image(
                painter = painterResource(id = R.drawable.logo_placeholder), // Reemplaza con tu logo
                contentDescription = stringResource(id = R.string.logo_desc),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Campo de Email
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange, // <-- CORREGIDO: sin guion
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.email_label)) },
                placeholder = { Text(stringResource(id = R.string.email_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = uiState.emailError != null,
                supportingText = {
                    uiState.emailError?.let { error ->
                        Text(text = error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.password_label)) },
                placeholder = { Text(stringResource(id = R.string.password_placeholder)) },
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = stringResource(id = R.string.toggle_password_visibility_desc)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace de "Olvidé mi contraseña"
            Text(
                text = stringResource(id = R.string.forgot_password),
                color = LightBlue,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* Navegar a la pantalla de recuperar contraseña */ }
            )

            Spacer(modifier = Modifier.weight(1f)) // Empuja el contenido de abajo hacia el fondo

            // Enlace para crear cuenta
            Row {
                Text(text = stringResource(id = R.string.new_user_prompt))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.create_account),
                    color = LightBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        // Vuelve a la pantalla anterior que es la de registro
                        navController.navigate(Screen.RegisterScreen.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Sign In
            Button(
                onClick = {
                    viewModel.onSignInClicked {
                        navController.navigate(Screen.HomeScreen.route) {
                            // Limpia toda la pila de navegación anterior
                            popUpTo(0)
                        }
                    }
                }, // <-- Se movió la coma aquí, después de la llave de cierre.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(id = R.string.sign_in_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    AlguienDijoChambaTheme {
        SignInScreen(navController = rememberNavController())
    }
}