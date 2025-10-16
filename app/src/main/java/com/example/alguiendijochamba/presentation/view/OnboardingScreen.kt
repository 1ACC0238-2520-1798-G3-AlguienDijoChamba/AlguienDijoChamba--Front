package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.alguiendijochamba.R
import com.example.alguiendijochamba.presentation.viewmodel.OnboardingViewModel
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import com.example.alguiendijochamba.ui.theme.DarkBlue
import com.example.alguiendijochamba.ui.theme.LightBlue
import com.example.alguiendijochamba.ui.theme.MediumGray
import com.example.alguiendijochamba.presentation.navigation.Screen
import androidx.compose.ui.graphics.Color
import com.example.alguiendijochamba.ui.theme.AlguienDijoChambaTheme



@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // Puntos de navegación (indicadores de página, si hay más)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Asumo dos puntos para las dos pantallas que tienes
            Box(modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .padding(horizontal = 4.dp)
                .weight(1f, fill = false) // Pequeña separación
                .offset(x = (-4).dp) // Ajuste para centrar
            ) {
                // Aquí podrías poner el indicador azul para la primera pantalla
            }
            Box(modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .padding(horizontal = 4.dp)
                .weight(1f, fill = false) // Pequeña separación
                .offset(x = 4.dp) // Ajuste para centrar
            ) {
                // Y un punto gris para la segunda
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Imagen del trabajador
        Image(
            painter = painterResource(id = R.drawable.worker_placeholder), // Reemplaza con tu imagen
            contentDescription = stringResource(R.string.onboarding_image_desc),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp) // Tamaño fijo, pero el resto se adapta
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Título
        Text(
            text = stringResource(R.string.onboarding_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f) // Adapta el ancho
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtítulo con enlace
        Text(
            text = stringResource(R.string.onboarding_subtitle_part1) + " ",
            style = MaterialTheme.typography.bodyLarge.copy(color = MediumGray),
            textAlign = TextAlign.Center
        )
        // Puedes hacer que esta parte sea clickeable si la envuelves en un clickable
        Text(
            text = stringResource(R.string.app_name), // "AlguienDijoChamba"
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = LightBlue
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.8f)
        )
        Text(
            text = " " + stringResource(R.string.onboarding_subtitle_part2),
            style = MaterialTheme.typography.bodyLarge.copy(color = MediumGray),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Descripción
        Text(
            text = stringResource(R.string.onboarding_description),
            style = MaterialTheme.typography.bodyMedium.copy(color = MediumGray),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

        // Botón "Continue"
        Button(
            onClick = {
                viewModel.completeOnboarding()
                navController.navigate(Screen.AuthScreen.route) {
                    popUpTo(Screen.OnboardingScreen.route) { inclusive = true } // Remueve del backstack
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = MaterialTheme.shapes.small // Opcional: forma redondeada
        ) {
            Text(
                text = stringResource(R.string.button_continue),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right), // Icono de flecha
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun OnboardingScreenPreview() {
    AlguienDijoChambaTheme {
        OnboardingScreen(navController = rememberNavController())
    }
}