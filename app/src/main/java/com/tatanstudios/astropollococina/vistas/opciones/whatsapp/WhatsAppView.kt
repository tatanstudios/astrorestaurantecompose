package com.tatanstudios.astropollococina.vistas.opciones.whatsapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.BarraSuperior
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.componentes.ErrorReintentar
import com.tatanstudios.astropollococina.componentes.TarjetaBlanca
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.viewmodel.WhatsAppViewModel
import kotlinx.coroutines.flow.first

/**
 * Numero de WhatsApp de la sucursal (recibe "Nueva orden #X" con cada orden)
 * y mensajes que quedan en el bolson para esos avisos.
 */
@Composable
fun WhatsAppScreen(
    navController: NavHostController,
    viewModel: WhatsAppViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val teclado = LocalSoftwareKeyboardController.current
    val tokenManager = remember { TokenManager(ctx) }

    var idusuario by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var numeroCargado by remember { mutableStateOf(false) }
    var errorNumero by remember { mutableStateOf<String?>(null) }

    val info by viewModel.info.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val guardando by viewModel.guardando.observeAsState(false)
    val resultadoGuardar by viewModel.resultadoGuardar.observeAsState()

    LaunchedEffect(Unit) {
        idusuario = tokenManager.idUsuario.first()
        viewModel.cargar(idusuario)
    }

    // al llegar la informacion se llena el campo (solo la primera vez)
    LaunchedEffect(info) {
        val i = info ?: return@LaunchedEffect
        if (!numeroCargado) {
            numero = sinCodigoPais(i.whatsapp)
            numeroCargado = true
        }
    }

    LaunchedEffect(resultadoGuardar) {
        resultadoGuardar?.getContentIfNotHandled()?.let { r ->
            when (r?.success) {
                1 -> {
                    numero = sinCodigoPais(r.whatsapp)
                    errorNumero = null
                    CustomToasty(ctx, if (numero.isBlank()) "Número quitado" else "Número guardado", ToastType.SUCCESS)
                }
                2 -> errorNumero = r.mensaje ?: "El número no es válido."
                null -> CustomToasty(ctx, "No se pudo conectar con el servidor. Revisa tu internet.", ToastType.ERROR)
                else -> CustomToasty(ctx, "No se pudo guardar. Inténtalo de nuevo.", ToastType.ERROR)
            }
        }
    }

    val disponibles = info?.disponibles ?: 0
    val numeroGuardado = info?.whatsapp.orEmpty()
    val recibe = numeroGuardado.isNotBlank() && disponibles > 0

    Scaffold(
        containerColor = AppColors.crema,
        topBar = { BarraSuperior(titulo = "WhatsApp", onAtras = { navController.popBackStack() }) }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (info == null && isLoading) {
                CircularProgressIndicator(color = AppColors.rojo, modifier = Modifier.align(Alignment.Center))
            } else if (info != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // estado general
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (recibe) AppColors.verdeFondo else AppColors.ambarFondo)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (recibe) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                            contentDescription = null,
                            tint = if (recibe) AppColors.verde else AppColors.ambar,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            when {
                                numeroGuardado.isBlank() -> "No tienes número registrado: no te llegarán avisos por WhatsApp."
                                disponibles <= 0 -> "No quedan mensajes disponibles: los avisos por WhatsApp están pausados."
                                else -> "Recibirás un WhatsApp con cada orden nueva."
                            },
                            fontWeight = FontWeight.SemiBold,
                            color = if (recibe) AppColors.verde else AppColors.ambar,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // mensajes disponibles
                    TarjetaBlanca {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.verdeFondo),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Sms, contentDescription = null, tint = AppColors.verde, modifier = Modifier.size(26.dp))
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Mensajes disponibles", color = AppColors.grisTexto, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    disponibles.toString(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (disponibles > 0) AppColors.letraPrincipal else AppColors.rojoOscuro
                                )
                            }
                        }
                        Text(
                            "Cada orden nueva gasta 1 mensaje. Si llega a 0, las órdenes siguen llegando a la app, pero no por WhatsApp.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.grisIcono
                        )
                    }

                    // numero
                    TarjetaBlanca {
                        Text("Número que recibe los avisos", fontWeight = FontWeight.SemiBold, color = AppColors.letraPrincipal)
                        OutlinedTextField(
                            value = numero,
                            onValueChange = { nuevo ->
                                // el +503 va fijo: solo los 8 digitos
                                numero = nuevo.filter { it.isDigit() }.take(8)
                                errorNumero = null
                            },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                            prefix = { Text("+503 ", color = AppColors.letraPrincipal, fontWeight = FontWeight.SemiBold) },
                            placeholder = { Text("71234567") },
                            singleLine = true,
                            isError = errorNumero != null,
                            supportingText = {
                                Text(errorNumero ?: "Escribe solo los 8 dígitos. Déjalo vacío para no recibir avisos.")
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.rojo,
                                focusedLeadingIconColor = AppColors.rojo,
                                cursorColor = AppColors.rojo
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                teclado?.hide()
                                if (numero.isNotEmpty() && numero.length != 8) {
                                    errorNumero = "El número debe tener 8 dígitos."
                                } else {
                                    viewModel.guardar(idusuario, numero)
                                }
                            },
                            enabled = !guardando,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.rojo, contentColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            if (guardando) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Guardando...", fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Guardar número", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            ErrorReintentar(viewModel = viewModel, isLoading = isLoading)
        }
    }
}

/** El servidor guarda "503XXXXXXXX"; en pantalla se muestran solo los 8 digitos (el +503 va fijo). */
private fun sinCodigoPais(numero: String?): String {
    val digitos = numero.orEmpty().filter { it.isDigit() }
    return if (digitos.length == 11 && digitos.startsWith("503")) digitos.substring(3) else digitos
}
