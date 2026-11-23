package com.tatanstudios.astropollococina.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatanstudios.astropollococina.viewmodel.CargaViewModel

/** Datos que muestra la tarjeta de una orden (completadas, canceladas, historial). */
data class DatosOrden(
    val id: Int,
    val fecha: String,
    val total: String,
    val cliente: String?,
    val telefono: String?,
    val direccion: String?,
    val referencia: String?,
    val nota: String?,
    val cupon: String?,
    /** linea extra destacada: "Iniciada: 10:30", "Motivo: ..." */
    val extra: String? = null,
    /** estilo propio de esta orden (historial mezcla estados); null = el de la lista */
    val estilo: EstiloEstado? = null,
)

/** Estilo del estado de la lista (chip y color de la línea extra). */
data class EstiloEstado(val texto: String, val fondo: Color, val color: Color, val iconoExtra: ImageVector = Icons.Filled.Schedule)

@Composable
fun TarjetaOrden(orden: DatosOrden, estilo: EstiloEstado, onCancelar: (() -> Unit)? = null, onClick: () -> Unit) {
    TarjetaBlanca(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Orden #${orden.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.letraPrincipal,
                modifier = Modifier.weight(1f)
            )
            Chip(estilo.texto, estilo.fondo, estilo.color)
        }
        DatoConIcono(Icons.Filled.CalendarMonth, orden.fecha)
        if (!orden.extra.isNullOrBlank()) DatoConIcono(estilo.iconoExtra, orden.extra, estilo.color)
        if (!orden.cliente.isNullOrBlank()) DatoConIcono(Icons.Filled.Person, orden.cliente, AppColors.letraPrincipal)
        if (!orden.telefono.isNullOrBlank()) DatoConIcono(Icons.Filled.Phone, orden.telefono)
        if (!orden.direccion.isNullOrBlank()) DatoConIcono(Icons.Filled.LocationOn, orden.direccion)
        if (!orden.referencia.isNullOrBlank()) DatoConIcono(Icons.Filled.Place, "Referencia: ${orden.referencia}")
        if (!orden.nota.isNullOrBlank()) DatoConIcono(Icons.Filled.EditNote, "Nota: ${orden.nota}")
        if (!orden.cupon.isNullOrBlank()) Chip("Cupón: ${orden.cupon}", AppColors.verdeFondo, AppColors.verde)

        HorizontalDivider(color = AppColors.grisSuave)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Total", style = MaterialTheme.typography.labelMedium, color = AppColors.grisIcono)
                Text(orden.total, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.rojo)
            }
            if (onCancelar != null) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onCancelar,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.rojoOscuro),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = AppColors.rojoOscuro),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) { Text("Cancelar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
            }
            Text(if (onCancelar != null) "Productos" else "Ver productos", color = AppColors.rojo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AppColors.rojo)
        }
    }
}

/**
 * Pantalla de lista de órdenes con barra, skeleton, vacío, deslizar para recargar y Reintentar.
 * [ordenes] es null mientras todavía no se ha cargado nada.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PantallaListaOrdenes(
    titulo: String,
    onAtras: () -> Unit,
    ordenes: List<DatosOrden>?,
    isLoading: Boolean,
    viewModel: CargaViewModel,
    estilo: EstiloEstado,
    iconoVacio: ImageVector,
    tituloVacio: String,
    mensajeVacio: String,
    onRecargar: (() -> Unit)?,
    onClickOrden: (DatosOrden) -> Unit,
    onCancelarOrden: ((DatosOrden) -> Unit)? = null,
) {
    Scaffold(
        containerColor = AppColors.crema,
        topBar = { BarraSuperior(titulo = titulo, onAtras = onAtras) }
    ) { innerPadding ->
        val avisoRecarga = rememberAvisoRecarga(isLoading, viewModel) { "Lista actualizada · ${ordenes?.size ?: 0} ${if ((ordenes?.size ?: 0) == 1) "orden" else "órdenes"}" }
        val pullRefreshState = rememberPullRefreshState(
            refreshing = avisoRecarga.refrescando,
            onRefresh = { avisoRecarga.iniciar(); onRecargar?.invoke() }
        )
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .let { if (onRecargar != null) it.pullRefresh(pullRefreshState) else it }
        ) {
            when {
                ordenes == null -> if (isLoading) ListaSkeleton(cantidad = 3, alto = 190.dp)

                ordenes.isEmpty() -> EstadoVacio(iconoVacio, tituloVacio, mensajeVacio)

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "${ordenes.size} ${if (ordenes.size == 1) "orden" else "órdenes"}" +
                                if (onRecargar != null) " · desliza hacia abajo para actualizar" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.grisIcono
                        )
                    }
                    items(ordenes, key = { it.id }) { orden ->
                        TarjetaOrden(orden, orden.estilo ?: estilo, onCancelar = onCancelarOrden?.let { { it(orden) } }) { onClickOrden(orden) }
                    }
                }
            }

            if (onRecargar != null) {
                PullRefreshIndicator(
                    refreshing = avisoRecarga.refrescando,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            // recargando con datos ya visibles: indicador pequeño arriba
            if (isLoading && ordenes != null && !avisoRecarga.refrescando) {
                androidx.compose.material3.LinearProgressIndicator(
                    color = AppColors.rojo,
                    trackColor = Color.Transparent,
                    modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
                )
            }

            ErrorReintentar(viewModel, isLoading)
        }
    }
}
