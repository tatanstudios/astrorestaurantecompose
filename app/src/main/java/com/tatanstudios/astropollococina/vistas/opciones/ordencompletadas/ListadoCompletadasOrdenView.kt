package com.tatanstudios.astropollococina.vistas.opciones.ordencompletadas

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TaskAlt
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
import com.tatanstudios.astropollococina.componentes.DialogoCancelarOrden
import com.tatanstudios.astropollococina.componentes.LoadingModal
import com.tatanstudios.astropollococina.componentes.EstiloEstado
import com.tatanstudios.astropollococina.componentes.PantallaListaOrdenes
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.model.rutas.Routes
import com.tatanstudios.astropollococina.viewmodel.CancelarOrdenViewModel
import com.tatanstudios.astropollococina.viewmodel.OrdenCompletadasBuscarViewModel
import kotlinx.coroutines.flow.first

/** Órdenes iniciadas hoy. */
@Composable
fun ListadoCompletadasOrdenScreen(navController: NavHostController,
                                  viewModel: OrdenCompletadasBuscarViewModel = viewModel(),
                                  viewModelCancelar: CancelarOrdenViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(true)
    val resultado by viewModel.resultado.observeAsState()
    val tokenManager = remember { TokenManager(ctx) }
    var idusuario by remember { mutableStateOf("") }
    var ordenes by remember { mutableStateOf<List<DatosOrden>?>(null) }

    // cancelar una orden ya iniciada
    var ordenPorCancelar by remember { mutableStateOf<Int?>(null) }
    val isLoadingCancelar by viewModelCancelar.isLoading.observeAsState(false)
    val resultadoCancelar by viewModelCancelar.resultado.observeAsState()
    val errorCancelar by viewModelCancelar.error.observeAsState()
    LaunchedEffect(errorCancelar) {
        errorCancelar?.getContentIfNotHandled()?.let {
            CustomToasty(ctx, "No se pudo cancelar. Revisa tu internet e inténtalo de nuevo.", ToastType.ERROR)
        }
    }

    LaunchedEffect(Unit) {
        idusuario = tokenManager.idUsuario.first()
        viewModel.completadasOrdenRetrofit(idusuario)
    }

    PantallaListaOrdenes(
        titulo = "Iniciadas hoy",
        onAtras = { navController.popBackStack() },
        ordenes = ordenes,
        isLoading = isLoading,
        viewModel = viewModel,
        estilo = EstiloEstado("Iniciada", AppColors.verdeFondo, AppColors.verde, Icons.Filled.TaskAlt),
        iconoVacio = Icons.Filled.Restaurant,
        tituloVacio = "Todavía no hay órdenes iniciadas hoy",
        mensajeVacio = "Las órdenes que inicies aparecerán aquí.",
        onRecargar = { if (idusuario.isNotEmpty()) viewModel.completadasOrdenRetrofit(idusuario) },
        onCancelarOrden = { orden -> ordenPorCancelar = orden.id },
        onClickOrden = { orden ->
            navController.navigate(Routes.VistaListadoProductoOrden.createRoute(orden.id.toString())) {
                launchSingleTop = true
            }
        }
    )

    ordenPorCancelar?.let { id ->
        DialogoCancelarOrden(
            idorden = id,
            aviso = "Esta orden ya se está preparando. Cancélala solo si de verdad no se va a entregar.",
            onDismiss = { ordenPorCancelar = null },
            onConfirmar = { motivo ->
                ordenPorCancelar = null
                viewModelCancelar.cancelarOrdenRetrofit(idorden = id, notaCancelar = motivo)
            }
        )
    }
    if (isLoadingCancelar) LoadingModal(isLoading = true, titulo = "Cancelando orden...")

    resultadoCancelar?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1) {
            CustomToasty(ctx, result.mensaje ?: "Orden cancelada", ToastType.SUCCESS)
            if (idusuario.isNotEmpty()) viewModel.completadasOrdenRetrofit(idusuario)
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }

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
                    extra = it.fechaPreparada?.takeIf { f -> f.isNotBlank() }?.let { f -> "Iniciada: $f" },
                )
            }
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
}
