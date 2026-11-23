package com.tatanstudios.astropollococina.vistas.opciones.ordencanceladas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.BarraSuperior
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.componentes.ErrorReintentar
import com.tatanstudios.astropollococina.componentes.EstadoVacio
import com.tatanstudios.astropollococina.componentes.ListaSkeleton
import com.tatanstudios.astropollococina.componentes.ProductoItemCard
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.model.ordenes.ModeloProductoOrdenesArray
import com.tatanstudios.astropollococina.model.rutas.Routes
import com.tatanstudios.astropollococina.network.RetrofitBuilder
import com.tatanstudios.astropollococina.viewmodel.ProductosOrdenViewModel

/** Productos de una orden (desde completadas o canceladas). Solo lectura. */
@Composable
fun ListadoProductosOrdenScreen(navController: NavHostController, _idorden: Int,
                                viewModelProductosOrden: ProductosOrdenViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val isLoadingProductos by viewModelProductosOrden.isLoading.observeAsState(initial = false)
    val resultadoProductos by viewModelProductosOrden.resultado.observeAsState()
    var boolDatosCargados by remember { mutableStateOf(false) }
    var modeloListaProductosArray by remember { mutableStateOf(listOf<ModeloProductoOrdenesArray>()) }

    LaunchedEffect(Unit) {
        viewModelProductosOrden.productosOrdenRetrofit(idorden = _idorden)
    }

    Scaffold(
        containerColor = AppColors.crema,
        topBar = { BarraSuperior(titulo = "Productos · Orden #$_idorden", onAtras = { navController.popBackStack() }) }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                !boolDatosCargados && isLoadingProductos -> ListaSkeleton(cantidad = 4, alto = 96.dp)

                boolDatosCargados && modeloListaProductosArray.isEmpty() -> EstadoVacio(
                    Icons.Filled.ShoppingBag,
                    "Sin productos",
                    "Esta orden no tiene productos registrados."
                )

                boolDatosCargados -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        val unidades = modeloListaProductosArray.sumOf { it.cantidad }
                        Text(
                            "$unidades ${if (unidades == 1) "producto" else "productos"} · toca uno para ver el detalle",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.grisIcono
                        )
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
    }

    resultadoProductos?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1) {
            modeloListaProductosArray = result.lista
            boolDatosCargados = true
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
}
