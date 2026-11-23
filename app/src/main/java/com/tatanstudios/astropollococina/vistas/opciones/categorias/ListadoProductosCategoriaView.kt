package com.tatanstudios.astropollococina.vistas.opciones.categorias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
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
import com.tatanstudios.astropollococina.componentes.CustomModal2Botones
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.componentes.ErrorReintentar
import com.tatanstudios.astropollococina.componentes.EstadoVacio
import com.tatanstudios.astropollococina.componentes.ListaSkeleton
import com.tatanstudios.astropollococina.componentes.LoadingModal
import com.tatanstudios.astropollococina.componentes.TarjetaDisponible
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.model.ordenes.ModeloListaProductoCategoriasArray
import com.tatanstudios.astropollococina.network.RetrofitBuilder
import com.tatanstudios.astropollococina.viewmodel.ActualizarProductosViewModel
import com.tatanstudios.astropollococina.viewmodel.ListadoProductosCategoriaViewModel

/** Productos de una categoría: el interruptor los marca disponibles o agotados. */
@Composable
fun ListadoProductosCategoriasScreen(navController: NavHostController, _idcategoria: Int,
                                     viewModelProductosCategorias: ListadoProductosCategoriaViewModel = viewModel(),
                                     viewModelActualizarProCate: ActualizarProductosViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val isLoadingProductos by viewModelProductosCategorias.isLoading.observeAsState(initial = true)
    val resultadoProductos by viewModelProductosCategorias.resultado.observeAsState()
    var productos by remember { mutableStateOf<List<ModeloListaProductoCategoriasArray>?>(null) }

    val isLoadingActualizar by viewModelActualizarProCate.isLoading.observeAsState(initial = false)
    val resultadoActualizar by viewModelActualizarProCate.resultado.observeAsState()
    val errorActualizar by viewModelActualizarProCate.error.observeAsState()
    LaunchedEffect(errorActualizar) {
        errorActualizar?.getContentIfNotHandled()?.let {
            CustomToasty(ctx, ctx.getString(R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }

    var porCambiar by remember { mutableStateOf<Pair<ModeloListaProductoCategoriasArray, Boolean>?>(null) }

    LaunchedEffect(Unit) {
        viewModelProductosCategorias.listaProductosCategoriasRetrofit(idcategoria = _idcategoria)
    }

    Scaffold(
        containerColor = AppColors.crema,
        topBar = { BarraSuperior(titulo = stringResource(R.string.productos), onAtras = { navController.popBackStack() }) }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val lista = productos
            when {
                lista == null -> if (isLoadingProductos) ListaSkeleton(cantidad = 6, alto = 84.dp)

                lista.isEmpty() -> EstadoVacio(Icons.Filled.Fastfood, "Sin productos", "Esta categoría todavía no tiene productos.")

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        val disponibles = lista.count { it.activo == 1 }
                        Text(
                            "$disponibles de ${lista.size} disponibles · apaga el interruptor si un producto se agotó.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.grisIcono
                        )
                    }
                    items(lista, key = { it.id }) { producto ->
                        TarjetaDisponible(
                            titulo = producto.nombre ?: "",
                            imagenUrl = if (producto.utilizaImagen == 1) "${RetrofitBuilder.urlImagenes}${producto.imagen}" else null,
                            estado = producto.estado,
                            activo = producto.activo == 1,
                            detalle = producto.precio?.takeIf { it.isNotBlank() }?.let { if (it.startsWith("$")) it else "$$it" },
                            onCambiarActivo = { nuevo -> porCambiar = producto to nuevo },
                        )
                    }
                }
            }

            ErrorReintentar(viewModelProductosCategorias, isLoadingProductos)
        }

        porCambiar?.let { (producto, nuevo) ->
            CustomModal2Botones(
                showDialog = true,
                titulo = if (nuevo) "¿Activar \"${producto.nombre}\"?" else "¿Desactivar \"${producto.nombre}\"?",
                message = if (nuevo) "Los clientes podrán volver a pedirlo."
                          else "Los clientes no podrán pedirlo hasta que lo actives de nuevo.",
                onDismiss = { porCambiar = null },
                onAccept = {
                    porCambiar = null
                    viewModelActualizarProCate.actualizarProductoCateRetrofit(idproducto = producto.id, valorcheck = if (nuevo) 1 else 0)
                },
                txtBtnAceptar = if (nuevo) "Activar" else "Desactivar",
                txtBtnCancelar = "Cancelar",
                colorAceptar = if (nuevo) AppColors.verde else AppColors.rojoOscuro,
            )
        }

        if (isLoadingActualizar) LoadingModal(isLoading = true, titulo = "Guardando...")
    }

    resultadoProductos?.getContentIfNotHandled()?.let { result ->
        when (result.success) {
            1 -> productos = result.lista
            2 -> productos = emptyList() // la categoría no tiene productos
            else -> CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }

    resultadoActualizar?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1) {
            CustomToasty(ctx, stringResource(id = R.string.actualizado), ToastType.SUCCESS)
            viewModelProductosCategorias.listaProductosCategoriasRetrofit(idcategoria = _idcategoria)
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
}
