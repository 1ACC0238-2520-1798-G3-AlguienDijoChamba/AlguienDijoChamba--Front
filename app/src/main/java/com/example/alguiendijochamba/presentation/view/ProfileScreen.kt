package com.example.alguiendijochamba.presentation.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alguiendijochamba.R
import com.example.alguiendijochamba.presentation.navigation.Screen
import com.example.alguiendijochamba.presentation.viewmodel.ProfileViewModel
import com.example.alguiendijochamba.presentation.viewmodel.ProfileUiState
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import org.koin.androidx.compose.koinViewModel

// Usamos @OptIn para suprimir la advertencia del compilador sobre TopAppBar y otros elementos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            viewModel.onPhotoSelected(uri)
        }
    )

    if (uiState.profileDeleted) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.SignInScreen.route) { popUpTo(0) }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.onDeleteProfile { navController.navigate(Screen.AuthScreen.route) { popUpTo(0) } }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            if (uiState.isEditing) {
                TopAppBar(
                    title = { Text("Editar Perfil") },
                    navigationIcon = {
                        IconButton(onClick = viewModel::onEditToggle) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar edición")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        // --- CORRECCIÓN CLAVE: El fondo principal ahora es BLANCO ---
        // Esto asegura que el espacio no ocupado (la franja que quieres cambiar) sea blanco.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // <--- CAMBIO AQUÍ: Fondo principal Blanco
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {

            // --- ProfileHeader MANTIENE SU FONDO AZUL ---
            ProfileHeader(
                uiState = uiState,
                onSettingsClick = viewModel::onEditToggle,
                onPhotoClick = { photoPickerLauncher.launch("image/*") },
                onPhotoDelete = viewModel::onPhotoDelete,
                isEditing = uiState.isEditing
            )

            // El formulario ya tenía Color.White, por lo que el formulario se une
            // con el nuevo fondo blanco del Column principal.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                ProfileForm(
                    uiState = uiState,
                    viewModel = viewModel,
                    onDeleteRequest = { showDeleteDialog = true }
                )
            }
        }
    }
}

// --- Componente ProfileHeader ---
@Composable
fun ProfileHeader(
    uiState: ProfileUiState,
    onSettingsClick: () -> Unit,
    onPhotoClick: () -> Unit,
    onPhotoDelete: () -> Unit,
    isEditing: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(MaterialTheme.colorScheme.primary) // Mantiene el fondo azul
            .padding(16.dp)
    ) {
        // Ícono de Configuración/Ajustes
        IconButton(onClick = onSettingsClick, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = Color.White)
        }

        // Título del Perfil
        Text("Perfil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter))

        // Columna Central de la Foto y Nombre
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = uiState.fotoPerfilUrl ?: R.drawable.profile_placeholder,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable(enabled = isEditing, onClick = onPhotoClick),
                    contentScale = ContentScale.Crop
                )
                if (isEditing) {
                    // Ícono para indicar que la foto es editable
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 8.dp, y = 8.dp)
                            .background(PrimaryBlue, CircleShape)
                            .padding(4.dp)
                    )
                }
            }

            // Botón de eliminar foto solo en modo edición
            if (isEditing && uiState.fotoPerfilUrl != null) {
                TextButton(onClick = onPhotoDelete) {
                    Text("Eliminar foto anterior", color = MaterialTheme.colorScheme.error)
                }
            }

            Text("${uiState.nombres} ${uiState.apellidos}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(uiState.ocupacion, color = Color.White.copy(alpha = 0.8f))
        }

        // Botón FAB de "Más"
        if (!isEditing) {
            FloatingActionButton(
                onClick = { /* Acción para editar habilidades (segunda imagen) */ },
                modifier = Modifier.align(Alignment.BottomCenter).offset(y = 30.dp),
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, contentDescription = "Editar Habilidades") }
        }
    }
}

// --- Componente ProfileForm ---
@Composable
fun ProfileForm(uiState: ProfileUiState, viewModel: ProfileViewModel, onDeleteRequest: () -> Unit) {
    Column(
        modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mensaje de Error General (si falla el guardado)
        if (uiState.saveError != null) {
            Text(uiState.saveError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth())
        }

        Text(if (uiState.isEditing) "Editar Perfil" else "Perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Campos NO editables
        OutlinedTextField(value = uiState.nombres, onValueChange = {}, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
        OutlinedTextField(value = uiState.apellidos, onValueChange = {}, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth(), readOnly = true)

        // Campos editables
        OutlinedTextField(value = uiState.ocupacion, onValueChange = viewModel::onOcupacionChange, label = { Text("Ocupación") }, modifier = Modifier.fillMaxWidth(), readOnly = !uiState.isEditing)

        // Email con validación
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = !uiState.isEditing,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = uiState.emailError != null,
            supportingText = { uiState.emailError?.let { Text(it) } }
        )

        // Celular con validación
        OutlinedTextField(
            value = uiState.celular,
            onValueChange = viewModel::onCelularChange, // <-- Usará la nueva lógica de 9 dígitos
            label = { Text("Celular") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = !uiState.isEditing,
            // Restringe el teclado a números
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            // Muestra el error si existe
            isError = uiState.celularError != null,
            supportingText = { uiState.celularError?.let { Text(it) } }
        )

        Spacer(Modifier.height(16.dp))

        if (uiState.isEditing) {
            // Botones de Edición
            Button(onClick = viewModel::onSaveProfile, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White) else Text("Guardar Cambios")
            }
            OutlinedButton(onClick = viewModel::onEditToggle, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
            Spacer(Modifier.height(8.dp))

            // Botón de Eliminación (Requiere Diálogo)
            Button(
                onClick = onDeleteRequest, // Llama al diálogo
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar Cuenta")
            }
        } else {
            Button(onClick = viewModel::onEditToggle, modifier = Modifier.fillMaxWidth()) { Text("Editar") }
        }
    }
}

// --- Diálogo de Confirmación de Eliminación ---

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Advertencia de Eliminación") },
        text = {
            Text("Estás a punto de eliminar tu cuenta de forma permanente. Esta acción es irreversible y se eliminarán todos tus datos (incluyendo User y Professional). ¿Estás seguro?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar Cuenta", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}