package com.tatanstudios.astropollococina.vistas.opciones.ordenesnuevas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.BarraSuperior
import com.tatanstudios.astropollococina.componentes.BloqueSkeleton
import com.tatanstudios.astropollococina.componentes.ContenedorSkeleton
import com.tatanstudios.astropollococina.componentes.ErrorReintentar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.componentes.BarraToolbarColor
import com.tatanstudios.astropollococina.componentes.CustomModal1BotonTitulo
import com.tatanstudios.astropollococina.componentes.CustomModal2Botones
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.componentes.LoadingModal
import com.tatanstudios.astropollococina.componentes.ProductoItemCard
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.model.ordenes.ModeloProductoOrdenesArray
import com.tatanstudios.astropollococina.model.rutas.Routes
import com.tatanstudios.astropollococina.network.RetrofitBuilder
import com.tatanstudios.astropollococina.viewmodel.CancelarOrdenViewModel
import com.tatanstudios.astropollococina.viewmodel.IniciarOrdenViewModel
import com.tatanstudios.astropollococina.viewmodel.ProductosOrdenViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EstadoNuevaOrdenScreen(navController: NavHostController, _idorden: Int,
                           viewModelCancelar: CancelarOrdenViewModel = viewModel(),
                           viewModelIniciarOrden: IniciarOrdenViewModel = viewModel(),
                           viewModelProductosOrden: ProductosOrdenViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // listado de productos
    val isLoadingProductos by viewModelProductosOrden.isLoading.observeAsState(initial = false)
    val resultadoProductos by viewModelProductosOrden.resultado.observeAsState()
    var boolDatosCargados by remember { mutableStateOf(false) }

    // Para datos de orden cancelada
    val isLoadingCancelar by viewModelCancelar.isLoading.observeAsState(initial = false)
    val resultadoCancelar by viewModelCancelar.resultado.observeAsState()
    val errorCancelar by viewModelCancelar.error.observeAsState()
    LaunchedEffect(errorCancelar) {
        errorCancelar?.getContentIfNotHandled()?.let {
            CustomToasty(ctx, ctx.getString(R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }

    // para datos de iniciar orden
    val isLoadingIniciar by viewModelIniciarOrden.isLoading.observeAsState(initial = false)
    val resultadoIniciar by viewModelIniciarOrden.resultado.observeAsState()
    val errorIniciar by viewModelIniciarOrden.error.observeAsState()
    LaunchedEffect(errorIniciar) {
        errorIniciar?.getContentIfNotHandled()?.let {
            CustomToasty(ctx, ctx.getString(R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
    var showDialogIniciarOrden by remember { mutableStateOf(false) }
    var showDialogOrdenIniciadaApi by remember { mutableStateOf(false) }


    // titulo y mensaje de respuestas
    var textoTituloApi by remember { mutableStateOf("") }
    var textoMensajeApi by remember { mutableStateOf("") }


    var modeloListaProductosArray by remember { mutableStateOf(listOf<ModeloProductoOrdenesArray>()) }


    val tokenManager = remember { TokenManager(ctx) }

    val coroutineScope = rememberCoroutineScope()
    //val keyboardController = LocalSoftwareKeyboardController.current

    var showDialogCancelamiento by remember { mutableStateOf(false) }



    // Lanzar la solicitud cuando se carga la pantalla
    LaunchedEffect(Unit) {
        viewModelProductosOrden.productosOrdenRetrofit(idorden = _idorden)
    }



    Scaffold(
        containerColor = AppColors.crema,
        topBar = {
            BarraSuperior(
                titulo = "Orden #$_idorden",
                onAtras = { navController.popBackStack() }
            )
        },
        // acciones fijas abajo: primero se revisa el pedido completo, luego se inicia o cancela
        bottomBar = {
            if (boolDatosCargados) {
                Surface(shadowElevation = 12.dp, color = Color.White) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showDialogCancelamiento = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.rojoOscuro),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, AppColors.rojoOscuro),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Cancelar", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { showDialogIniciarOrden = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.verde, contentColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1.6f).height(52.dp)
                        ) {
                            Icon(Icons.Filled.Restaurant, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Iniciar orden", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!boolDatosCargados && isLoadingProductos) {
                ContenedorSkeleton(Modifier.fillMaxSize().padding(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        BloqueSkeleton(Modifier.fillMaxWidth().height(70.dp), radio = 16.dp)
                        repeat(4) { BloqueSkeleton(Modifier.fillMaxWidth().height(96.dp), radio = 16.dp) }
                    }
                }
            } else if (boolDatosCargados) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        val unidades = modeloListaProductosArray.sumOf { it.cantidad }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppColors.ambarFondo)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = AppColors.ambar, modifier = Modifier.size(26.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Esperando que la inicies", fontWeight = FontWeight.SemiBold, color = AppColors.ambar)
                                Text(
                                    "$unidades ${if (unidades == 1) "producto" else "productos"} · toca uno para ver el detalle",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.grisTexto
                                )
                            }
                        }
                    }

                    items(modeloListaProductosArray) { tipoProducto ->
                        ProductoItemCard(
                            cantidad = tipoProducto.cantidad.toString(),
                            hayImagen = tipoProducto.utilizaImagen,
                            imagenUrl = "${RetrofitBuilder.urlImagenes}${tipoProducto.imagen}",
                            titulo = tipoProducto.nombreProducto,
                            descripcion = tipoProducto.nota,
                            precio = tipoProducto.precio,
                            opciones = tipoProducto.opciones.orEmpty(),
                            onClick = {
                                navController.navigate(
                                    Routes.VistaInfoProductoOrden.createRoute(tipoProducto.id.toString()),
                                    navOptions { launchSingleTop = true }
                                )
                            }
                        )
                    }
                }
            }

            ErrorReintentar(viewModelProductosOrden, isLoadingProductos)
        }

        // CANCELAR: motivo (lo ve el cliente) + confirmacion
        if (showDialogCancelamiento) {
            com.tatanstudios.astropollococina.componentes.DialogoCancelarOrden(
                idorden = _idorden,
                onDismiss = { showDialogCancelamiento = false },
                onConfirmar = { motivo ->
                    showDialogCancelamiento = false
                    viewModelCancelar.cancelarOrdenRetrofit(idorden = _idorden, notaCancelar = motivo)
                }
            )
        }

        // CONFIRMAR PARA INICIAR ORDEN
        if (showDialogIniciarOrden) {
            CustomModal2Botones(
                showDialog = true,
                message = "Se le avisará al cliente que su orden se está preparando.",
                onDismiss = { showDialogIniciarOrden = false },
                onAccept = {
                    showDialogIniciarOrden = false
                    textoTituloApi = ""
                    textoMensajeApi = ""
                    coroutineScope.launch {
                        viewModelIniciarOrden.iniciarOrdenRetrofit(idorden = _idorden)
                    }
                },
                txtBtnAceptar = "Iniciar",
                txtBtnCancelar = "Todavía no",
                colorAceptar = AppColors.verde,
                titulo = "¿Iniciar la orden #$_idorden?",
            )
        }

        // MENSAJE DE API AL INICIAR ORDEN
        if (showDialogOrdenIniciadaApi) {
            CustomModal1BotonTitulo(
                showDialog = showDialogOrdenIniciadaApi,
                title = textoTituloApi,
                message = textoMensajeApi,
                onDismiss = {
                    showDialogOrdenIniciadaApi = false
                    navController.popBackStack()
                }
            )
        }

        if (isLoadingCancelar) LoadingModal(isLoading = true, titulo = "Cancelando orden...")
        if (isLoadingIniciar) LoadingModal(isLoading = true, titulo = "Iniciando orden...")

    } // end-scalfold








    resultadoProductos?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                // LISTADO DE PRODUCTOS
                modeloListaProductosArray = result.lista
                boolDatosCargados = true

            }
            else -> {
                // Error, recargar de nuevo
                CustomToasty(
                    ctx,
                    stringResource(id = R.string.error_reintentar_de_nuevo),
                    ToastType.ERROR
                )
            }
        }
    }


    resultadoCancelar?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                // ORDEN CANCELADA
                CustomToasty(
                    ctx,
                    stringResource(id = R.string.orden_cancelada),
                    ToastType.SUCCESS
                )
                navController.popBackStack()
            }
            else -> {
                // Error, recargar de nuevo
                CustomToasty(
                    ctx,
                    stringResource(id = R.string.error_reintentar_de_nuevo),
                    ToastType.ERROR
                )
            }
        }
    }


    resultadoIniciar?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> {
                // ORDEN CANCELADA POR CLIENTE
                textoTituloApi = result.titulo?: ""
                textoMensajeApi = result.mensaje?: ""
                showDialogOrdenIniciadaApi = true
            }
            2 -> {
               // ORDEN INICIADA
                textoTituloApi = result.titulo?: ""
                textoMensajeApi = result.mensaje?: ""
                showDialogOrdenIniciadaApi = true
            }
            else -> {
                // Error, recargar de nuevo
                CustomToasty(
                    ctx,
                    stringResource(id = R.string.error_reintentar_de_nuevo),
                    ToastType.ERROR
                )
            }
        }
    }


}

