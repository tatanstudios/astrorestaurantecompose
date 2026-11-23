package com.tatanstudios.astropollococina.vistas.opciones.historial

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatanstudios.astropollococina.componentes.AppColors
import com.tatanstudios.astropollococina.componentes.BarraSuperior
import com.tatanstudios.astropollococina.componentes.TarjetaBlanca
import com.tatanstudios.astropollococina.model.rutas.Routes
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Elegir el rango del historial: atajos de un toque o un rango personalizado. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistorialFechaScreen(navController: NavHostController) {
    val ctx = LocalContext.current
    val hoy = LocalDate.now()
    val formatoVista = DateTimeFormatter.ofPattern("EEE d 'de' MMMM yyyy", Locale("es", "ES"))
    val formatoApi = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var fechaDesde by remember { mutableStateOf(hoy) }
    var fechaHasta by remember { mutableStateOf(hoy) }
    var error by remember { mutableStateOf<String?>(null) }

    fun buscar(desde: LocalDate, hasta: LocalDate) {
        navController.navigate(
            Routes.VistaHistorialListadoOrden.createRoute(fecha1 = desde.format(formatoApi), fecha2 = hasta.format(formatoApi))
        ) { launchSingleTop = true }
    }

    fun elegirFecha(inicial: LocalDate, minima: LocalDate?, alElegir: (LocalDate) -> Unit) {
        val dialogo = DatePickerDialog(
            ctx,
            { _, y, m, d -> alElegir(LocalDate.of(y, m + 1, d)) },
            inicial.year, inicial.monthValue - 1, inicial.dayOfMonth
        )
        dialogo.datePicker.maxDate = System.currentTimeMillis()
        minima?.let { dialogo.datePicker.minDate = it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() }
        dialogo.show()
    }

    Scaffold(
        containerColor = AppColors.crema,
        topBar = { BarraSuperior(titulo = "Historial", onAtras = { navController.popBackStack() }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaBlanca {
                Text("Ver rápido", fontWeight = FontWeight.SemiBold, color = AppColors.letraPrincipal)
                Text("Toca un período para ver sus órdenes.", style = MaterialTheme.typography.bodySmall, color = AppColors.grisIcono)
                Spacer(Modifier.height(4.dp))
                val atajos = listOf(
                    "Hoy" to (hoy to hoy),
                    "Ayer" to (hoy.minusDays(1) to hoy.minusDays(1)),
                    "Últimos 7 días" to (hoy.minusDays(6) to hoy),
                    "Este mes" to (hoy.withDayOfMonth(1) to hoy),
                    "Mes pasado" to (hoy.minusMonths(1).withDayOfMonth(1) to hoy.withDayOfMonth(1).minusDays(1)),
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    atajos.forEach { (texto, rango) ->
                        Text(
                            texto,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.rojo,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(AppColors.rojo.copy(alpha = 0.08f))
                                .clickable { buscar(rango.first, rango.second) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            TarjetaBlanca {
                Text("Rango personalizado", fontWeight = FontWeight.SemiBold, color = AppColors.letraPrincipal)
                CampoFecha("Desde", fechaDesde.format(formatoVista)) {
                    elegirFecha(fechaDesde, null) {
                        fechaDesde = it
                        if (fechaHasta.isBefore(it)) fechaHasta = it // mantener el rango válido
                        error = null
                    }
                }
                CampoFecha("Hasta", fechaHasta.format(formatoVista)) {
                    elegirFecha(fechaHasta, fechaDesde) { fechaHasta = it; error = null }
                }
                error?.let { Text(it, color = AppColors.rojoOscuro, style = MaterialTheme.typography.bodySmall) }
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        if (fechaDesde.isAfter(fechaHasta)) error = "La fecha \"Desde\" no puede ser mayor que \"Hasta\"."
                        else buscar(fechaDesde, fechaHasta)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.rojo, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Buscar órdenes", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CampoFecha(etiqueta: String, valor: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.grisSuave)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = AppColors.rojo, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = AppColors.grisIcono)
            Text(valor.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = AppColors.letraPrincipal)
        }
        Text("Cambiar", color = AppColors.rojo, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
    }
}
