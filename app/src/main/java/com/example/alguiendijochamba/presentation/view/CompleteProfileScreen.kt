package com.example.alguiendijochamba.presentation.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alguiendijochamba.R
import com.example.alguiendijochamba.presentation.viewmodel.CompleteProfileViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AttachMoney
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    navController: NavController,
    viewModel: CompleteProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSpecialtyDialog by remember { mutableStateOf(false) }

    // --- Lanzadores para seleccionar archivos ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> viewModel.onPhotoSelected(uri) }
    )
    val certificationPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> viewModel.onCertificationSelected(uri) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.complete_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_button_desc))
                    }
                },
            )
        }
    ) { paddingValues ->

        if (showSpecialtyDialog) {
            SpecialtySelectionDialog(
                availableSpecialties = uiState.availableSpecialties,
                onSpecialtySelected = { viewModel.onSpecialtyAdded(it) },
                onDismiss = { showSpecialtyDialog = false }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Sección Foto de Perfil ---
            item {
                Text(stringResource(id = R.string.profile_photo), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        if (uiState.profilePhotoUri != null) {
                            AsyncImage(
                                model = uiState.profilePhotoUri,
                                contentDescription = "Profile photo",
                                modifier = Modifier.clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.padding(20.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(onClick = { photoPickerLauncher.launch("image/*") }) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.upload_photo))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Sección Especialidades ---
            item {
                Text(stringResource(id = R.string.specialties), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.specialties.forEach { specialty ->
                        InputChip(
                            selected = false,
                            onClick = { /* No action needed */ },
                            label = { Text(specialty) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove specialty",
                                    modifier = Modifier.size(18.dp).clickable { viewModel.onSpecialtyRemoved(specialty) }
                                )
                            }
                        )
                    }
                }

                TextButton(onClick = { showSpecialtyDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.add_specialty))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Sección Experiencia y Tarifa ---
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = uiState.yearsOfExperience,
                        onValueChange = viewModel::onExperienceChange,
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(id = R.string.years_of_experience)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null
                            )
                        },
                        isError = uiState.experienceError != null,
                        supportingText = { uiState.experienceError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = uiState.hourlyRate,
                        onValueChange = viewModel::onRateChange,
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(id = R.string.hourly_rate)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Sección Biografía ---
            item {
                OutlinedTextField(
                    value = uiState.professionalBio,
                    onValueChange = viewModel::onBioChange,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    label = { Text(stringResource(id = R.string.professional_bio)) },
                    placeholder = { Text(stringResource(id = R.string.bio_placeholder)) },
                    isError = uiState.bioError != null,
                    supportingText = {
                        Row(Modifier.fillMaxWidth()) {
                            uiState.bioError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            Spacer(modifier = Modifier.weight(1f))
                            Text("${uiState.professionalBio.length} / 1200")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Sección Certificaciones ---
            item {
                Text(stringResource(id = R.string.certifications), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                uiState.certificationUris.forEach { uri ->
                    Text(uri.lastPathSegment ?: "Certificado", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
                TextButton(onClick = { certificationPickerLauncher.launch("*/*") }) { // Puedes restringir el tipo de archivo
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.upload_certification))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Sección Previsualización y Guardar ---
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Aquí podrías mostrar la previsualización del perfil
                        Text("Previsualización de Perfil (WIP)", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = viewModel::onSaveProfile,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(stringResource(id = R.string.save_profile))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

}
@Composable
fun SpecialtySelectionDialog(
    availableSpecialties: List<String>,
    onSpecialtySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona una especialidad") },
        text = {
            LazyColumn {
                items(availableSpecialties.size) { index ->
                    val specialty = availableSpecialties[index]
                    Text(
                        text = specialty,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSpecialtySelected(specialty)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}