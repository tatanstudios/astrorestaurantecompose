package com.tatanstudios.astropollococina.vistas.opciones.ordencanceladas

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.SentimentSatisfied
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
import com.tatanstudios.astropollococina.viewmodel.OrdenCanceladasBuscarViewModel
import kotlinx.coroutines.flow.first

/** Órdenes canceladas hoy. */
@Composable
fun ListadoCanceladasOrdenScreen(navController: NavHostController,
                                 viewModel: OrdenCanceladasBuscarViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(true)
    val resultado by viewModel.resultado.observeAsState()
    val tokenManager = remember { TokenManager(ctx) }
    var idusuario by remember { mutableStateOf("") }
    var ordenes by remember { mutableStateOf<List<DatosOrden>?>(null) }

    LaunchedEffect(Unit) {
        idusuario = tokenManager.idUsuario.first()
        viewModel.canceladasOrdenRetrofit(idusuario)
    }

    PantallaListaOrdenes(
        titulo = "Canceladas hoy",
        onAtras = { navController.popBackStack() },
        ordenes = ordenes,
        isLoading = isLoading,
        viewModel = viewModel,
        estilo = EstiloEstado("Cancelada", AppColors.rojoFondo, AppColors.rojoOscuro, Icons.Filled.Cancel),
        iconoVacio = Icons.Filled.SentimentSatisfied,
        tituloVacio = "No hay órdenes canceladas hoy",
        mensajeVacio = "Las órdenes canceladas por ti o por el cliente aparecerán aquí.",
        onRecargar = { if (idusuario.isNotEmpty()) viewModel.canceladasOrdenRetrofit(idusuario) },
        onClickOrden = { orden ->
            navController.navigate(Routes.VistaListadoProductoOrden.createRoute(orden.id.toString())) {
                launchSingleTop = true
            }
        }
    )

    resultado?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1) {
            ordenes = result.lista.map {
                val motivo = it.notaCancelada?.takeIf { n -> n.isNotBlank() }
                val hora = it.fechaCancelada?.takeIf { f -> f.isNotBlank() }
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
                    extra = listOfNotNull(hora?.let { h -> "Cancelada: $h" }, motivo?.let { m -> "Motivo: $m" })
                        .joinToString("\n").ifBlank { null },
                )
            }
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
}
