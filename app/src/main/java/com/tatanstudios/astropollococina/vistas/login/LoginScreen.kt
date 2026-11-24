package com.tatanstudios.astropollococina.vistas.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.onesignal.OneSignal
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.CustomModal1Boton
import com.tatanstudios.astropollococina.extras.SesionApi
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.model.rutas.Routes
import com.tatanstudios.astropollococina.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {

    val ctx = LocalContext.current
    val usuario by viewModel.usuario.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val resultado by viewModel.resultado.observeAsState()
    val errorRed by viewModel.errorRed.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val tokenManager = remember { TokenManager(ctx) }
    val scope = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }
    // errores debajo de cada campo y un mensaje general (usuario bloqueado, sin internet...)
    var errorUsuario by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }
    var errorGeneral by remember { mutableStateOf<String?>(null) }

    // venia con sesion de la version anterior (sin token): explicar por que debe entrar de nuevo
    var showAvisoSesion by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (SesionApi.avisoIniciarSesionDeNuevo) {
            SesionApi.avisoIniciarSesionDeNuevo = false
            showAvisoSesion = true
        }
    }

    fun entrar() {
        keyboardController?.hide()
        errorGeneral = null
        errorUsuario = if (usuario.isBlank()) "Ingresa tu usuario" else null
        errorPassword = if (password.isBlank()) "Ingresa tu contraseña" else null
        if (errorUsuario == null && errorPassword == null) {
            viewModel.verificarUsuarioPasssword(idonesignal = getOneSignalUserId())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.crema)
    ) {
        // franja roja de fondo arriba
        Box(
            Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                .background(AppColors.rojoBarra)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(36.dp))
            Image(
                painter = painterResource(id = R.drawable.logonegrocirculo),
                contentDescription = stringResource(id = R.string.logotipo),
                modifier = Modifier
                    .size(110.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(14.dp))
            Text(
                stringResource(id = R.string.astro_pollo),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Control de órdenes",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(28.dp))

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Iniciar sesión",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.letraPrincipal
                    )
                    Text(
                        "Ingresa con el usuario de tu restaurante.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.grisIcono
                    )

                    CampoLogin(
                        valor = usuario,
                        onCambio = {
                            if (it.length <= 20) viewModel.setUsuario(it)
                            errorUsuario = null; errorGeneral = null
                        },
                        etiqueta = "Usuario",
                        icono = Icons.Filled.Person,
                        error = errorUsuario,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    CampoLogin(
                        valor = password,
                        onCambio = {
                            if (it.length <= 16) viewModel.setPassword(it)
                            errorPassword = null; errorGeneral = null
                        },
                        etiqueta = "Contraseña",
                        icono = Icons.Filled.Lock,
                        error = errorPassword,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailing = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = AppColors.grisIcono
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { entrar() })
                    )

                    errorGeneral?.let { msg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.rojoFondo)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = AppColors.rojoOscuro, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(msg, style = MaterialTheme.typography.bodyMedium, color = AppColors.rojoOscuro)
                        }
                    }

                    Spacer(Modifier.height(2.dp))
                    Button(
                        onClick = { if (!isLoading) entrar() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.rojo, contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.size(10.dp))
                            Text("Ingresando...", fontWeight = FontWeight.SemiBold)
                        } else {
                            Text(
                                stringResource(id = R.string.iniciar_sesion),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                "¿Olvidaste tu contraseña? Pídele al administrador que la cambie desde el panel.",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.grisIcono,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(24.dp))
        }

        if (showAvisoSesion) {
            CustomModal1Boton(true, SesionApi.MENSAJE_INICIAR_SESION_DE_NUEVO, onDismiss = { showAvisoSesion = false })
        }
    }

    errorRed?.getContentIfNotHandled()?.let {
        errorGeneral = "No pudimos conectar con el servidor. Revisa tu internet e inténtalo de nuevo."
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> errorGeneral = ctx.getString(R.string.usuario_bloqueado)
            2 -> {
                // INICIO SESION CORRECTO
                com.tatanstudios.astropollococina.extras.Notificaciones.vincular(result.id.toString())
                scope.launch {
                    tokenManager.saveID(result.id.toString())
                    tokenManager.saveToken(result.token ?: "")
                    navController.navigate(Routes.VistaPrincipal.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            3 -> errorPassword = ctx.getString(R.string.password_incorrecto)
            4 -> errorUsuario = ctx.getString(R.string.usuario_incorrecto)
            else -> errorGeneral = ctx.getString(R.string.error_reintentar_de_nuevo)
        }
    }
}

/** Campo de texto redondeado con icono y mensaje de error debajo. */
@Composable
private fun CampoLogin(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = trailing,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.rojo,
            focusedLabelColor = AppColors.rojo,
            focusedLeadingIconColor = AppColors.rojo,
            cursorColor = AppColors.rojo,
            unfocusedBorderColor = Color(0xFFDADADA),
            unfocusedLeadingIconColor = AppColors.grisIcono,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        modifier = Modifier.fillMaxWidth()
    )
}


// ID DE ONE SIGNAL
fun getOneSignalUserId(): String {
    return OneSignal.User.pushSubscription.id
}
