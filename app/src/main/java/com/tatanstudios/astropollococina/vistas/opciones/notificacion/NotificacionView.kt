package com.tatanstudios.astropollococina.vistas.opciones.notificacion

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.BarraSuperior
import com.tatanstudios.astropollococina.componentes.TarjetaBlanca
import com.tatanstudios.astropollococina.extras.Notificaciones
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.viewmodel.NotificacionPruebaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/**
 * Revisa que este teléfono reciba los avisos de órdenes nuevas:
 * permiso, segundo plano y que esté vinculado en OneSignal con el usuario (External ID).
 */
@Composable
fun NotificacionScreen(
    navController: NavHostController,
    viewModel: NotificacionPruebaViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val tokenManager = remember { TokenManager(ctx) }

    var idusuario by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(Notificaciones.estado(ctx, "")) }
    var sinOptimizacion by remember { mutableStateOf(isIgnoringBatteryOptimizations(ctx)) }
    var sinPausa by remember { mutableStateOf(Notificaciones.sinPausaPorInactividad(ctx)) }
    val fabricante = remember { Notificaciones.fabricanteRestrictivo() }
    // resultado de la prueba: (ok, mensaje)
    var resultadoPrueba by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val isLoading by viewModel.isLoading.observeAsState(false)
    val resultado by viewModel.resultado.observeAsState()
    val errorEnvio by viewModel.error.observeAsState()

    fun actualizar() {
        estado = Notificaciones.estado(ctx, idusuario)
        sinOptimizacion = isIgnoringBatteryOptimizations(ctx)
        sinPausa = Notificaciones.sinPausaPorInactividad(ctx)
    }

    LaunchedEffect(Unit) {
        idusuario = tokenManager.idUsuario.first()
        Notificaciones.vincular(idusuario)
        // OneSignal confirma el registro en segundo plano: revisar unas veces
        repeat(5) {
            actualizar()
            if (estado.vinculado && estado.registrado) return@LaunchedEffect
            delay(1500)
        }
    }

    // al volver de Ajustes se revisa de nuevo
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) actualizar()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(errorEnvio) {
        errorEnvio?.getContentIfNotHandled()?.let {
            resultadoPrueba = false to "No se pudo conectar con el servidor. Revisa tu internet."
        }
    }

    val todoBien = estado.permiso && estado.vinculado && estado.registrado && sinOptimizacion && sinPausa

    Scaffold(
        containerColor = AppColors.crema,
        topBar = { BarraSuperior(titulo = "Notificaciones", onAtras = { navController.popBackStack() }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // resumen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (todoBien) AppColors.verdeFondo else AppColors.ambarFondo)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (todoBien) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (todoBien) AppColors.verde else AppColors.ambar,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (todoBien) "Este teléfono recibe los avisos" else "Revisa los puntos marcados en rojo",
                        fontWeight = FontWeight.SemiBold,
                        color = if (todoBien) AppColors.verde else AppColors.ambar
                    )
                    Text(
                        "Con todo en verde, los pedidos llegan aunque no abras la app en días, como en WhatsApp.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.grisTexto
                    )
                }
            }

            FilaRevision(
                ok = estado.permiso,
                titulo = "Permiso de notificaciones",
                textoOk = "Permitido",
                textoMal = "Están bloqueadas. Actívalas para recibir las órdenes.",
                textoBoton = "Activar",
                onArreglar = { abrirAjustesNotificaciones(ctx) }
            )

            FilaRevision(
                ok = estado.vinculado && estado.registrado,
                titulo = "Registrado para recibir avisos",
                textoOk = "Vinculado a tu usuario",
                textoMal = when {
                    !estado.vinculado -> "Este teléfono no está vinculado a tu usuario."
                    estado.suscripcionId.isBlank() -> "Todavía no se registra. Revisa tu internet."
                    else -> "El teléfono no acepta notificaciones. Revisa el permiso."
                },
                textoBoton = "Registrar",
                onArreglar = {
                    Notificaciones.vincular(idusuario)
                    actualizar()
                }
            )

            FilaRevision(
                ok = sinOptimizacion,
                titulo = "Funcionar en segundo plano",
                textoOk = "Sin restricción de batería",
                textoMal = "El ahorro de batería puede retrasar o bloquear los avisos.",
                textoBoton = "Permitir",
                onArreglar = { Notificaciones.pedirSinOptimizacionBateria(ctx) }
            )

            FilaRevision(
                ok = sinPausa,
                titulo = "No pausar si no se usa",
                textoOk = "Android no pausará la app aunque pasen días sin abrirla",
                textoMal = "Si pasan días sin abrir la app, Android la pausa y deja de entregar los avisos. Desactiva «Pausar actividad si no se usa».",
                textoBoton = "Desactivar",
                onArreglar = { Notificaciones.abrirPausaPorInactividad(ctx) }
            )

            // marcas que cierran apps por su cuenta: no se puede comprobar, solo guiar
            if (fabricante != null) {
                TarjetaBlanca {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AppColors.ambarFondo),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = AppColors.ambar, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Ajuste de $fabricante", fontWeight = FontWeight.SemiBold, color = AppColors.letraPrincipal)
                            Text(
                                "Estos teléfonos cierran apps por su cuenta. Activa «Inicio automático» y en batería elige «Sin restricciones» para Astro Restaurante. No la cierres deslizándola desde las apps recientes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.grisTexto
                            )
                        }
                    }
                    TextButton(onClick = { Notificaciones.abrirAjustesFabricante(ctx) }, modifier = Modifier.align(Alignment.End)) {
                        Text("Abrir ajustes", color = AppColors.rojo, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            TarjetaBlanca {
                Text("Enviar notificación de prueba", fontWeight = FontWeight.SemiBold, color = AppColors.letraPrincipal)
                Text(
                    "El servidor te enviará un aviso a este teléfono. Debería llegarte en unos segundos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.grisIcono
                )
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        if (!isLoading) {
                            resultadoPrueba = null
                            Notificaciones.vincular(idusuario)
                            viewModel.enviarNotificacionRetrofit(idusuario)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.rojo, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Enviando...", fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Enviar prueba", fontWeight = FontWeight.SemiBold)
                    }
                }
                resultadoPrueba?.let { (ok, msg) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (ok) AppColors.verdeFondo else AppColors.rojoFondo)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (ok) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = if (ok) AppColors.verde else AppColors.rojoOscuro,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(msg, style = MaterialTheme.typography.bodyMedium, color = if (ok) AppColors.verde else AppColors.rojoOscuro)
                    }
                }
            }

            // datos para soporte
            Text(
                "Identificador: ${estado.externalId.ifBlank { "sin vincular" }}\n" +
                    "Suscripción: ${estado.suscripcionId.ifBlank { "sin registrar" }}",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.grisIcono,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        resultadoPrueba = when (result.success) {
            1 -> true to "Enviada. Si no te llega en un minuto, revisa los puntos de arriba."
            2 -> false to (result.mensaje ?: "Este teléfono todavía no está registrado para recibir avisos.")
            3 -> false to (result.mensaje ?: "El servidor no pudo enviar la notificación.")
            else -> false to "No se pudo enviar. Inténtalo de nuevo."
        }
    }
}

@Composable
private fun FilaRevision(
    ok: Boolean,
    titulo: String,
    textoOk: String,
    textoMal: String,
    textoBoton: String?,
    onArreglar: () -> Unit,
) {
    TarjetaBlanca {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (ok) AppColors.verdeFondo else AppColors.rojoFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (ok) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (ok) AppColors.verde else AppColors.rojoOscuro,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.SemiBold, color = AppColors.letraPrincipal)
                Text(
                    if (ok) textoOk else textoMal,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (ok) AppColors.verde else AppColors.rojoOscuro
                )
            }
            if (!ok && textoBoton != null) {
                TextButton(onClick = onArreglar) {
                    Text(textoBoton, color = AppColors.rojo, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}

fun requestBatteryOptimizationPermission(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
        data = Uri.parse("package:${context.packageName}")
    }
    context.startActivity(intent)
}

fun abrirAjustesNotificaciones(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}
