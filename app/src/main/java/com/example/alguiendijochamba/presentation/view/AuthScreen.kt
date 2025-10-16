package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alguiendijochamba.R // Asegúrate de tener un drawable para la imagen del perfil
import com.example.alguiendijochamba.presentation.viewmodel.AuthViewModel
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import com.example.alguiendijochamba.ui.theme.DarkBlue
import com.example.alguiendijochamba.ui.theme.LightBlue
import com.example.alguiendijochamba.ui.theme.MediumGray
import com.example.alguiendijochamba.ui.theme.AlguienDijoChambaTheme
import com.example.alguiendijochamba.presentation.navigation.Screen

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val isRegistering by viewModel.isRegistering.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // Top Bar con Back button y puntos de navegación
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button_desc),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }, // Volver a la pantalla anterior
                tint = DarkBlue
            )
            Spacer(modifier = Modifier.weight(1f)) // Empuja los puntos a la derecha
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                // Indicador de página (gris para la primera, azul para la segunda)
                Box(modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .padding(horizontal = 4.dp)
                    .offset(x = (-4).dp)
                ) {
                    // Aquí podrías poner el indicador gris para la primera pantalla
                }
                Box(modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .padding(horizontal = 4.dp)
                    .offset(x = 4.dp)
                ) {
                    // Y un punto azul para la segunda
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Imagen de perfil
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder), // Reemplaza con tu imagen
            contentDescription = stringResource(R.string.profile_image_desc),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Título
        Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace "Join our professional community"
        Text(
            text = stringResource(R.string.auth_join_community),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = LightBlue
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clickable { /* Acción para unirse a la comunidad */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Descripción
        Text(
            text = stringResource(R.string.auth_description),
            style = MaterialTheme.typography.bodyMedium.copy(color = MediumGray),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.weight(1f)) // Empuja los botones hacia abajo

        // Botón principal (Register o Login)
        Button(
            onClick = {
                if (isRegistering) {
                    navController.navigate(Screen.RegisterScreen.route)
                    // Aquí podrías navegar a la siguiente pantalla de registro completo o dashboard
                } else {
                    viewModel.login()
                    // Aquí podrías navegar al dashboard después del login
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = if (isRegistering) stringResource(R.string.button_register_professional)
                else stringResource(R.string.button_login), // Si no se registra, es login
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace "I already have an account" o "Register now"
        Text(
            text = if (isRegistering) stringResource(R.string.auth_already_have_account)
            else stringResource(R.string.auth_register_now),
            style = MaterialTheme.typography.bodyLarge.copy( // <-- 'style' va aquí
                fontWeight = FontWeight.Medium,
                color = DarkBlue
            ),
            modifier = Modifier.clickable { // <-- Solo un 'modifier'
                if (isRegistering) {
                    // Navega a la pantalla de Sign In
                    navController.navigate(Screen.SignInScreen.route)
                } else {
                    // Vuelve al modo de registro (si estuviera en modo login)
                    viewModel.toggleAuthMode()
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun AuthScreenPreview() {
    AlguienDijoChambaTheme {
        AuthScreen(navController = rememberNavController())
    }
}