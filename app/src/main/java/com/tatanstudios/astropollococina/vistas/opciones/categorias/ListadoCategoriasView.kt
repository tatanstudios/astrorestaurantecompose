package com.tatanstudios.astropollococina.vistas.opciones.categorias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.Alignment
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
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.model.ordenes.ModeloCategoriasArray
import com.tatanstudios.astropollococina.model.rutas.Routes
import com.tatanstudios.astropollococina.network.RetrofitBuilder
import com.tatanstudios.astropollococina.viewmodel.ActualizarCategoriaViewModel
import com.tatanstudios.astropollococina.viewmodel.CategoriasBuscarViewModel
import kotlinx.coroutines.flow.first

/** Categorías del menú: el interruptor activa/desactiva y tocar la tarjeta abre sus productos. */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListadoCategoriasScreen(navController: NavHostController,
                            viewModel: CategoriasBuscarViewModel = viewModel(),
                            viewModelActualizar: ActualizarCategoriaViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(true)
    val resultado by viewModel.resultado.observeAsState()

    val isLoadingActualizar by viewModelActualizar.isLoading.observeAsState(false)
    val resultadoActualizar by viewModelActualizar.resultado.observeAsState()
    val errorActualizar by viewModelActualizar.error.observeAsState()
    LaunchedEffect(errorActualizar) {
        errorActualizar?.getContentIfNotHandled()?.let {
            CustomToasty(ctx, ctx.getString(R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }

    val tokenManager = remember { TokenManager(ctx) }
    var idusuario by remember { mutableStateOf("") }
    var categorias by remember { mutableStateOf<List<ModeloCategoriasArray>?>(null) }

    // categoria a la que se le quiere cambiar el estado (se confirma antes)
    var porCambiar by remember { mutableStateOf<Pair<ModeloCategoriasArray, Boolean>?>(null) }

    LaunchedEffect(Unit) {
        idusuario = tokenManager.idUsuario.first()
        viewModel.categoriasRetrofit(idusuario)
    }

    val avisoRecarga = com.tatanstudios.astropollococina.componentes.rememberAvisoRecarga(isLoading, viewModel) { "Categorías actualizadas" }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = avisoRecarga.refrescando,
        onRefresh = { if (idusuario.isNotEmpty()) { avisoRecarga.iniciar(); viewModel.categoriasRetrofit(idusuario) } }
    )

    Scaffold(
        containerColor = AppColors.crema,
        topBar = { BarraSuperior(titulo = stringResource(R.string.categorias), onAtras = { navController.popBackStack() }) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState)
        ) {
            val lista = categorias
            when {
                lista == null -> if (isLoading) ListaSkeleton(cantidad = 5, alto = 84.dp)

                lista.isEmpty() -> EstadoVacio(Icons.Filled.Category, "No hay categorías", "Las categorías del menú aparecerán aquí.")

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            "Usa el interruptor para mostrar u ocultar una categoría a los clientes. Toca la tarjeta para ver sus productos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.grisIcono
                        )
                    }
                    items(lista, key = { it.id }) { categoria ->
                        TarjetaDisponible(
                            titulo = categoria.nombre,
                            imagenUrl = categoria.imagen?.takeIf { it.isNotBlank() }?.let { "${RetrofitBuilder.urlImagenes}$it" },
                            estado = categoria.estado,
                            activo = categoria.activo == 1,
                            detalle = if (categoria.usaHorario == 1) categoria.horario else null,
                            onCambiarActivo = { nuevo -> porCambiar = categoria to nuevo },
                            onClick = {
                                navController.navigate(Routes.VistaListaProductosCategorias.createRoute(categoria.id.toString())) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }

            PullRefreshIndicator(refreshing = avisoRecarga.refrescando, state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))

            ErrorReintentar(viewModel, isLoading)
        }

        porCambiar?.let { (categoria, nuevo) ->
            CustomModal2Botones(
                showDialog = true,
                titulo = if (nuevo) "¿Activar \"${categoria.nombre}\"?" else "¿Desactivar \"${categoria.nombre}\"?",
                message = if (nuevo) "Los clientes volverán a ver esta categoría en el menú."
                          else "Los clientes no podrán pedir productos de esta categoría.",
                onDismiss = { porCambiar = null },
                onAccept = {
                    porCambiar = null
                    viewModelActualizar.actualizarCategoriasRetrofit(idcategoria = categoria.id, valorcheck = if (nuevo) 1 else 0)
                },
                txtBtnAceptar = if (nuevo) "Activar" else "Desactivar",
                txtBtnCancelar = "Cancelar",
                colorAceptar = if (nuevo) AppColors.verde else AppColors.rojoOscuro,
            )
        }

        if (isLoadingActualizar) LoadingModal(isLoading = true, titulo = "Guardando...")
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1) {
            categorias = result.lista
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }

    resultadoActualizar?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1) {
            CustomToasty(ctx, stringResource(id = R.string.actualizado), ToastType.SUCCESS)
            if (idusuario.isNotEmpty()) viewModel.categoriasRetrofit(idusuario)
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
}
