package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alguiendijochamba.R
import com.example.alguiendijochamba.presentation.viewmodel.RegisterViewModel
import com.example.alguiendijochamba.ui.theme.AlguienDijoChambaTheme
import com.example.alguiendijochamba.ui.theme.LightBlue
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.AnnotatedString
import androidx.core.text.HtmlCompat
import com.example.alguiendijochamba.presentation.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmarContrasenaVisible by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.register_title)) },
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
        if (showTermsDialog) {
            PolicyDialog(
                title = stringResource(id = R.string.terms_title),
                content = stringResource(id = R.string.terms_and_conditions_full_text),
                onDismiss = { showTermsDialog = false }
            )
        }
        if (showPrivacyDialog) {
            PolicyDialog(
                title = stringResource(id = R.string.privacy_title),
                content = stringResource(id = R.string.privacy_policy_full_text),
                onDismiss = { showPrivacyDialog = false }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Campo DNI
                OutlinedTextField(
                    value = uiState.dni,
                    onValueChange = viewModel::onDniChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.dni_label)) },
                    trailingIcon = {
                        if (uiState.isLoadingReniec) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            IconButton(onClick = viewModel::onDniLookup) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar DNI")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = uiState.dniError != null,
                    supportingText = { uiState.dniError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Campos Nombres y Apellidos
                OutlinedTextField(
                    value = uiState.nombres,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.nombres_label)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uiState.apellidos,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.apellidos_label)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Campo Email
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.email_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Campo Celular
                OutlinedTextField(
                    value = uiState.celular,
                    onValueChange = viewModel::onCelularChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.celular_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = uiState.celularError != null,
                    supportingText = { uiState.celularError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Campo Contraseña
                OutlinedTextField(
                    value = uiState.contrasena,
                    onValueChange = viewModel::onContrasenaChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.password_label)) },
                    visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                            Icon(
                                imageVector = if (contrasenaVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = uiState.contrasenaError != null,
                    supportingText = { uiState.contrasenaError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Campo Confirmar Contraseña
                OutlinedTextField(
                    value = uiState.confirmarContrasena,
                    onValueChange = viewModel::onConfirmarContrasenaChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.confirm_password_label)) },
                    visualTransformation = if (confirmarContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmarContrasenaVisible = !confirmarContrasenaVisible }) {
                            Icon(
                                imageVector = if (confirmarContrasenaVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = uiState.confirmarContrasenaError != null,
                    supportingText = { uiState.confirmarContrasenaError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Método de Pago
                Text(stringResource(id = R.string.payment_method_label), style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PaymentMethodOption(
                        text = stringResource(id = R.string.payment_card),
                        isSelected = uiState.metodoPago == "Tarjeta",
                        onClick = { viewModel.onMetodoPagoChange("Tarjeta") },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentMethodOption(
                        text = stringResource(id = R.string.payment_wallet),
                        isSelected = uiState.metodoPago == "Billetera",
                        onClick = { viewModel.onMetodoPagoChange("Billetera") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Términos y Condiciones
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.terminosAceptados,
                            onCheckedChange = viewModel::onTerminosChange,
                            // Marca el checkbox en rojo si hay error
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryBlue,
                                uncheckedColor = if (uiState.terminosError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        TermsAndConditionsText(
                            onTermsClick = { showTermsDialog = true },
                            onPrivacyClick = { showPrivacyDialog = true }
                        )
                    }

                    // Muestra el mensaje de error si existe
                    uiState.terminosError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp) // Alinea el texto con el Checkbox
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }


            item {
                // Botón Crear Cuenta
                Button(
                    onClick = {
                        viewModel.onCreateAccountClicked {
                            navController.navigate(Screen.CompleteProfileScreen.route) {
                                popUpTo(Screen.AuthScreen.route)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(stringResource(id = R.string.create_account_button))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    val annotatedString: AnnotatedString = remember(html) {
        buildAnnotatedString {
            // Usa el parser de HTML de Android y lo convierte a un texto que Compose entiende
            append(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT))
        }
    }
    Text(
        text = annotatedString,
        modifier = modifier
    )
}
@Composable
fun PolicyDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            LazyColumn {
                item {
                    // Usamos nuestro nuevo Composable para mostrar el texto con formato
                    HtmlText(html = content)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.dialog_close_button))
            }
        }
    )
}

@Composable
fun PaymentMethodOption(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isSelected) PrimaryBlue else Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Aquí podrías agregar un ícono
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
@Composable
fun TermsAndConditionsText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        append("Acepto los ")
        pushStringAnnotation(tag = "TERMS", annotation = "terms_url")
        withStyle(style = SpanStyle(color = LightBlue)) {
            append("Términos y Condiciones")
        }
        pop()
        append(" y la ")
        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy_url")
        withStyle(style = SpanStyle(color = LightBlue)) {
            append("Política de Privacidad")
        }
        pop()
    }

    ClickableText(text = annotatedString, onClick = { offset ->
        annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
            .firstOrNull()?.let {
                onTermsClick() // Llama a la función para mostrar el diálogo de términos
            }

        annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
            .firstOrNull()?.let {
                onPrivacyClick() // Llama a la función para mostrar el diálogo de privacidad
            }
    })
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    AlguienDijoChambaTheme {
        RegisterScreen(rememberNavController())
    }
}
