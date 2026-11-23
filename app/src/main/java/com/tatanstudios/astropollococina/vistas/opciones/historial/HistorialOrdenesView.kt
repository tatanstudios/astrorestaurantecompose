package com.tatanstudios.astropollococina.vistas.opciones.historial

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.componentes.DatosOrden
import com.tatanstudios.astropollococina.componentes.EstiloEstado
import com.tatanstudios.astropollococina.componentes.PantallaListaOrdenes
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.model.rutas.Routes
import com.tatanstudios.astropollococina.viewmodel.HistorialFechasBuscarViewModel
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Órdenes del rango elegido en el historial. */
@Composable
fun HistorialOrdenScreen(navController: NavHostController,
                         _fecha1: String, _fecha2: String,
                         viewModel: HistorialFechasBuscarViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(true)
    val resultado by viewModel.resultado.observeAsState()
    val tokenManager = remember { TokenManager(ctx) }
    var ordenes by remember { mutableStateOf<List<DatosOrden>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.historialListadonRetrofit(tokenManager.idUsuario.first(), _fecha1, _fecha2)
    }

    PantallaListaOrdenes(
        titulo = tituloRango(_fecha1, _fecha2),
        onAtras = { navController.popBackStack() },
        ordenes = ordenes,
        isLoading = isLoading,
        viewModel = viewModel,
        estilo = EstiloEstado("Orden", AppColors.grisSuave, AppColors.grisTexto),
        iconoVacio = Icons.Filled.History,
        tituloVacio = "No hay órdenes en estas fechas",
        mensajeVacio = "Prueba con otro rango de fechas.",
        onRecargar = null,
        onClickOrden = { orden ->
            navController.navigate(Routes.VistaListadoProductosHistorialOrden.createRoute(idorden = orden.id.toString())) {
                launchSingleTop = true
            }
        }
    )

    resultado?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1) {
            ordenes = result.lista.map {
                DatosOrden(
                    id = it.id,
                    fecha = it.fechaOrden,
                    total = it.totalFormat,
                    cliente = it.cliente,
                    telefono = it.telefono,
                    direccion = it.direccion,
                    referencia = it.referencia,
                    nota = it.notaOrden,
                    cupon = if (it.haycupon == 1) it.mensajeCupon else null,
                    estilo = estiloPorEstado(it.estado),
                )
            }
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
}

/** Color del chip según el texto de estado que manda la API. */
private fun estiloPorEstado(estado: String?): EstiloEstado {
    val texto = estado?.takeIf { it.isNotBlank() } ?: "Orden"
    val e = texto.lowercase()
    return when {
        "cancel" in e -> EstiloEstado(texto, AppColors.rojoFondo, AppColors.rojoOscuro)
        "inici" in e || "complet" in e || "prepar" in e || "entreg" in e -> EstiloEstado(texto, AppColors.verdeFondo, AppColors.verde)
        else -> EstiloEstado(texto, AppColors.ambarFondo, AppColors.ambar)
    }
}

private fun tituloRango(fecha1: String, fecha2: String): String {
    return try {
        val f = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val d1 = LocalDate.parse(fecha1).format(f)
        val d2 = LocalDate.parse(fecha2).format(f)
        if (d1 == d2) "Historial · $d1" else "$d1 – $d2"
    } catch (e: Exception) {
        "Historial"
    }
}
