package com.tatanstudios.astropollococina.vistas.opciones.ordenesnuevas

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.BarraSuperior
import com.tatanstudios.astropollococina.componentes.BloqueSkeleton
import com.tatanstudios.astropollococina.componentes.ContenedorSkeleton
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.componentes.ErrorReintentar
import com.tatanstudios.astropollococina.componentes.LineasOpcionesCocina
import com.tatanstudios.astropollococina.componentes.TarjetaBlanca
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.model.ordenes.ModeloInfoProductoArray
import com.tatanstudios.astropollococina.network.RetrofitBuilder
import com.tatanstudios.astropollococina.viewmodel.InfoProductoViewModel

@Composable
fun InfoProductoScreen(navController: NavHostController, _idproducto: Int,
                       viewModel: InfoProductoViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val resultado by viewModel.resultado.observeAsState()
    var producto by remember { mutableStateOf<ModeloInfoProductoArray?>(null) }

    LaunchedEffect(Unit) {
        viewModel.infoProductoRetrofit(idproducto = _idproducto)
    }

    Scaffold(
        containerColor = AppColors.crema,
        topBar = { BarraSuperior(titulo = stringResource(R.string.producto), onAtras = { navController.popBackStack() }) }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val p = producto
            if (p == null) {
                if (isLoading) {
                    ContenedorSkeleton(Modifier.fillMaxSize().padding(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            BloqueSkeleton(Modifier.fillMaxWidth().height(200.dp), radio = 18.dp)
                            BloqueSkeleton(Modifier.fillMaxWidth().height(160.dp), radio = 18.dp)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (p.utilizaImagen == 1) {
                        AsyncImage(
                            model = ImageRequest.Builder(ctx)
                                .data("${RetrofitBuilder.urlImagenes}${p.imagen}")
                                .crossfade(true)
                                .build(),
                            contentDescription = p.nombreProducto,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(androidx.compose.ui.graphics.Color.White)
                                .padding(12.dp),
                            contentScale = ContentScale.Fit,
                            placeholder = painterResource(R.drawable.spinloading),
                            error = painterResource(R.drawable.camaradefecto)
                        )
                    }

                    TarjetaBlanca {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${p.cantidad}x",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AppColors.rojo)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                p.nombreProducto ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.letraPrincipal
                            )
                        }
                        if (!p.descripcion.isNullOrBlank()) {
                            Text(p.descripcion, style = MaterialTheme.typography.bodyMedium, color = AppColors.grisIcono)
                        }
                    }

                    // lo que la cocina necesita ver: opciones elegidas y nota del cliente
                    val opciones = p.opciones.orEmpty()
                    if (opciones.isNotEmpty() || !p.nota.isNullOrBlank()) {
                        TarjetaBlanca {
                            if (opciones.isNotEmpty()) {
                                Text("Opciones del cliente", fontWeight = FontWeight.SemiBold, color = AppColors.letraPrincipal)
                                LineasOpcionesCocina(opciones)
                            }
                            if (!p.nota.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppColors.rojoFondo)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Filled.EditNote, contentDescription = null, tint = AppColors.rojoOscuro, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Nota: ${p.nota}", fontWeight = FontWeight.Medium, color = AppColors.rojoOscuro)
                                }
                            }
                        }
                    }

                    TarjetaBlanca {
                        FilaPrecio("Precio unitario", p.precio ?: "")
                        FilaPrecio("Unidades", p.cantidad.toString())
                        HorizontalDivider(color = AppColors.grisSuave)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Total", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = AppColors.letraPrincipal, modifier = Modifier.weight(1f))
                            Text(p.multiplicado ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.rojo)
                        }
                    }
                }
            }

            ErrorReintentar(viewModel, isLoading)
        }
    }

    resultado?.getContentIfNotHandled()?.let { result ->
        if (result.success == 1 && result.lista.isNotEmpty()) {
            producto = result.lista.last()
        } else {
            CustomToasty(ctx, stringResource(id = R.string.error_reintentar_de_nuevo), ToastType.ERROR)
        }
    }
}

@Composable
private fun FilaPrecio(etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = AppColors.grisIcono, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = AppColors.grisTexto)
    }
}
