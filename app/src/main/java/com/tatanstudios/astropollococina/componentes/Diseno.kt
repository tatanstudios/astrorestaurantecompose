package com.tatanstudios.astropollococina.componentes

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tatanstudios.astropollococina.viewmodel.CargaViewModel

/** Colores del diseño (mismos que la app del cliente). */
object AppColors {
    val crema = Color(0xFFFFFFF4)
    val rojo = Color(0xFFD84B4B)
    val rojoBarra = Color(0xFFE53935)
    val letraPrincipal = Color(0xFF0D2C55)
    val grisIcono = Color(0xFF656565)
    val grisTexto = Color(0xFF4E4A4A)
    val grisSuave = Color(0xFFF1F1F3)
    val verde = Color(0xFF2E7D32)
    val verdeFondo = Color(0xFFE8F5E9)
    val ambar = Color(0xFF8D6E00)
    val ambarFondo = Color(0xFFFFF8E1)
    val rojoFondo = Color(0xFFFFEBEE)
    val rojoOscuro = Color(0xFFC62828)
}

/** Barra superior roja con flecha de regreso (o con [navigationIcon] si [onAtras] es null). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    onAtras: (() -> Unit)?,
    navigationIcon: @Composable () -> Unit = {},
    acciones: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(titulo, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
            if (onAtras != null) {
                IconButton(onClick = onAtras) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                }
            } else navigationIcon()
        },
        actions = acciones,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AppColors.rojoBarra,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

/** Tarjeta blanca redondeada usada en todas las listas. */
@Composable
fun TarjetaBlanca(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth().let { if (onClick != null) it.clickable(onClick = onClick) else it },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = contenido)
    }
}

/** Fila icono + texto gris (fecha, dirección, teléfono...). */
@Composable
fun DatoConIcono(icono: ImageVector, texto: String, color: Color = AppColors.grisTexto) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icono, contentDescription = null, tint = AppColors.grisIcono, modifier = Modifier.size(18.dp).padding(top = 1.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

/** Etiqueta redondeada de color (estado, cupón...). */
@Composable
fun Chip(texto: String, fondo: Color, color: Color) {
    Text(
        texto,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/** Mensaje centrado cuando una lista no tiene datos. Es scrolleable para que funcione "deslizar para recargar". */
@Composable
fun EstadoVacio(icono: ImageVector, titulo: String, mensaje: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(60.dp))
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(AppColors.rojo.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = AppColors.rojo, modifier = Modifier.size(56.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.letraPrincipal, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(mensaje, style = MaterialTheme.typography.bodyMedium, color = AppColors.grisIcono, textAlign = TextAlign.Center)
    }
}

// ---------------- error con Reintentar ----------------

/**
 * Pantalla completa de "no se pudo cargar" con botón Reintentar.
 * Se dibuja ENCIMA del contenido (tapa lo de abajo y bloquea los toques).
 */
@Composable
fun PanelErrorReintentar(
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
    titulo: String = "No pudimos cargar la información",
    mensaje: String = "Revisa tu conexión a internet e inténtalo de nuevo.",
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.crema)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(AppColors.rojo.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.WifiOff, contentDescription = null, tint = AppColors.rojo, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = AppColors.letraPrincipal)
            Spacer(Modifier.height(8.dp))
            Text(mensaje, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = AppColors.grisIcono)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onReintentar,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.rojo, contentColor = Color.White),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Reintentar", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Muestra [PanelErrorReintentar] cuando la última carga del ViewModel falló y la repite al tocar
 * "Reintentar". Ponerlo como ÚLTIMO hijo del Box de contenido (debajo de la barra, así Atrás sigue funcionando).
 */
@Composable
fun ErrorReintentar(viewModel: CargaViewModel, isLoading: Boolean, modifier: Modifier = Modifier) {
    val error by viewModel.error.observeAsState(false)
    if (error && !isLoading) {
        PanelErrorReintentar(onReintentar = { viewModel.reintentar() }, modifier = modifier)
    }
}

// ---------------- skeleton ----------------

private val GrisSkeleton = Color(0xFFE6E6E6)

@Composable
fun BloqueSkeleton(modifier: Modifier, radio: Dp = 12.dp) {
    Box(modifier.clip(RoundedCornerShape(radio)).background(GrisSkeleton))
}

/** Hace "respirar" (parpadeo suave) a los bloques de adentro. */
@Composable
fun ContenedorSkeleton(modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    val transicion = rememberInfiniteTransition(label = "skeleton")
    val alfa by transicion.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "alfa"
    )
    Box(modifier.alpha(alfa)) { contenido() }
}

/** Skeleton genérico de una lista de tarjetas. */
@Composable
fun ListaSkeleton(cantidad: Int = 4, alto: Dp = 130.dp) {
    ContenedorSkeleton(Modifier.fillMaxSize().padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(cantidad) { BloqueSkeleton(Modifier.fillMaxWidth().height(alto), radio = 16.dp) }
        }
    }
}

// ---------------- aviso al recargar ----------------

/** Estado de una recarga pedida por el usuario (deslizar hacia abajo o botón Recargar). */
class AvisoRecarga internal constructor(
    private val manual: androidx.compose.runtime.MutableState<Boolean>,
    private val isLoading: Boolean,
) {
    /** true mientras dura la recarga manual: para mostrar el indicador girando. */
    val refrescando: Boolean get() = manual.value && isLoading
    fun iniciar() { manual.value = true }
}

/**
 * Al terminar una recarga manual muestra un aviso: [textoOk] si salió bien,
 * o "No se pudo actualizar" si falló (el panel Reintentar solo sale cuando no hay datos).
 */
@Composable
fun rememberAvisoRecarga(isLoading: Boolean, viewModel: CargaViewModel, textoOk: () -> String): AvisoRecarga {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val manual = remember { androidx.compose.runtime.mutableStateOf(false) }
    val error by viewModel.error.observeAsState(false)
    androidx.compose.runtime.LaunchedEffect(isLoading) {
        if (!isLoading && manual.value) {
            manual.value = false
            if (error) CustomToasty(ctx, "No se pudo actualizar. Revisa tu internet.", ToastType.ERROR)
            else CustomToasty(ctx, textoOk(), ToastType.SUCCESS)
        }
    }
    return AvisoRecarga(manual, isLoading)
}
