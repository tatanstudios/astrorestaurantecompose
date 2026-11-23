package com.tatanstudios.astropollococina.vistas.opciones.historial

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
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.BarraSuperior
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.componentes.ErrorReintentar
import com.tatanstudios.astropollococina.componentes.EstadoVacio
import com.tatanstudios.astropollococina.componentes.ListaSkeleton
import com.tatanstudios.astropollococina.componentes.ProductoItemCard
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.model.ordenes.ModeloProductoHistorialOrdenesArray
import com.tatanstudios.astropollococina.model.rutas.Routes
import com.tatanstudios.astropollococina.network.RetrofitBuilder
import com.tatanstudios.astropollococina.viewmodel.ListadoProductosHistorialOrdenViewModel

/** Productos de una orden del historial. */
@Composable
fun ListadoProductosHistorialScreen(navController: NavHostController, _idorden: Int,
                                    viewModelProductosListaHistorial: ListadoProductosHistorialOrdenViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val isLoading by viewModelProductosListaHistorial.isLoading.observeAsState(initial = true)
    val resultado by viewModelProductosListaHistorial.resultado.observeAsState()
    var productos by remember { mutableStateOf<List<ModeloProductoHistorialOrdenesArray>?>(null) }

    LaunchedEffect(Unit) {
        viewModelProductosListaHistorial.listaProductosHistorialRetrofit(idorden = _idorden)
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
            val lista = productos
            when {
                lista == null -> if (isLoading) ListaSkeleton(cantidad = 4, alto = 96.dp)

                lista.isEmpty() -> EstadoVacio(Icons.Filled.ShoppingBag, "Sin productos", "Esta orden no tiene productos registrados.")

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        val unidades = lista.sumOf { it.cantidad }
                        Text(
                            "$unidades ${if (unidades == 1) "producto" else "productos"} · toca uno para ver el detalle",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.grisIcono
                        )
                    }
                    items(lista) { producto ->
                        ProductoItemCard(
                            cantidad = producto.cantidad.toString(),
                            hayImagen = producto.utilizaImagen,
                            imagenUrl = "${RetrofitBuilder.urlImagenes}${producto.imagen}",
                            titulo = producto.nombreProducto,
                            descripcion = producto.nota,
                            precio = producto.precio,
                            opciones = producto.opciones.orEmpty(),
                            onClick = {
                                navController.navigate(Routes.VistaInfoProductoOrden.createRoute(producto.id.toString())) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }

            ErrorReintentar(viewModelProductosListaHistorial, isLoading)
        }
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1) {
            productos = result.lista
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
}
