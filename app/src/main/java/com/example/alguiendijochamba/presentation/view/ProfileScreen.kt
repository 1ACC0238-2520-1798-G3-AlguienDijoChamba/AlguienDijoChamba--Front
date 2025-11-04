package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.alguiendijochamba.R
import com.example.alguiendijochamba.presentation.viewmodel.ProfileViewModel
import com.example.alguiendijochamba.presentation.viewmodel.ProfileViewModelFactory
import com.example.alguiendijochamba.presentation.viewmodel.ProfileUiState

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current.applicationContext as android.app.Application))
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    if (uiState.profileDeleted) {
        // TODO: Navegar a la pantalla de login
        Text("Perfil eliminado. Redirigiendo...")
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(bottom = 80.dp)
    ) {
        ProfileHeader(uiState, onSettingsClick = viewModel::onEditToggle)
        Spacer(Modifier.height(16.dp))
        ProfileForm(uiState, viewModel)
    }
}

@Composable
fun ProfileHeader(uiState: ProfileUiState, onSettingsClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(250.dp)
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .padding(16.dp)
    ) {
        IconButton(onClick = onSettingsClick, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = Color.White)
        }
        Text("Perfil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter))
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = uiState.fotoPerfilUrl ?: R.drawable.profile_placeholder,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(100.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Text("${uiState.nombres} ${uiState.apellidos}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(uiState.ocupacion, color = Color.White.copy(alpha = 0.8f))
        }
        if (!uiState.isEditing) {
            FloatingActionButton(
                onClick = { /* Acción del botón + */ },
                modifier = Modifier.align(Alignment.BottomCenter).offset(y = 30.dp),
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, contentDescription = "Más") }
        }
    }
}

@Composable
fun ProfileForm(uiState: ProfileUiState, viewModel: ProfileViewModel) {
    Column(
        modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(if (uiState.isEditing) "Editar Perfil" else "Perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedTextField(value = uiState.nombres, onValueChange = {}, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
        OutlinedTextField(value = uiState.apellidos, onValueChange = {}, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
        OutlinedTextField(value = uiState.ocupacion, onValueChange = viewModel::onOcupacionChange, label = { Text("Ocupación") }, modifier = Modifier.fillMaxWidth(), readOnly = !uiState.isEditing)
        OutlinedTextField(value = uiState.email, onValueChange = viewModel::onEmailChange, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), readOnly = !uiState.isEditing)
        OutlinedTextField(value = uiState.celular, onValueChange = viewModel::onCelularChange, label = { Text("Celular") }, modifier = Modifier.fillMaxWidth(), readOnly = !uiState.isEditing)

        Spacer(Modifier.height(16.dp))

        if (uiState.isEditing) {
            Button(onClick = viewModel::onSaveProfile, modifier = Modifier.fillMaxWidth()) { Text("Guardar Cambios") }
            OutlinedButton(onClick = viewModel::onEditToggle, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = viewModel::onDeleteProfile, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Eliminar Cuenta") }
        } else {
            Button(onClick = viewModel::onEditToggle, modifier = Modifier.fillMaxWidth()) { Text("Editar") }
        }
    }
}