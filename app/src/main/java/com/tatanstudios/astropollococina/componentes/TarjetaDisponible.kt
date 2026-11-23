package com.tatanstudios.astropollococina.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tatanstudios.astropollococina.R

/**
 * Tarjeta de categoría o producto con interruptor de disponibilidad.
 * El interruptor cambia el estado (se confirma antes en la pantalla); tocar la tarjeta abre [onClick] si existe.
 */
@Composable
fun TarjetaDisponible(
    titulo: String,
    imagenUrl: String?,
    estado: String?,
    activo: Boolean,
    onCambiarActivo: (Boolean) -> Unit,
    detalle: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val modImagen = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, AppColors.grisSuave, RoundedCornerShape(12.dp))
                .padding(3.dp)
                .alpha(if (activo) 1f else 0.45f)
            if (imagenUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imagenUrl).crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.spinloading),
                    error = painterResource(R.drawable.camaradefecto),
                    modifier = modImagen
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.camaradefecto),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = modImagen
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (activo) AppColors.letraPrincipal else AppColors.grisIcono,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val textoEstado = estado?.takeIf { it.isNotBlank() } ?: if (activo) "Disponible" else "No disponible"
                Chip(
                    textoEstado,
                    if (activo) AppColors.verdeFondo else AppColors.rojoFondo,
                    if (activo) AppColors.verde else AppColors.rojoOscuro
                )
                if (!detalle.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = AppColors.grisIcono, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(detalle, style = MaterialTheme.typography.bodySmall, color = AppColors.grisIcono)
                    }
                }
            }

            Switch(
                checked = activo,
                onCheckedChange = onCambiarActivo,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppColors.verde,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                    uncheckedBorderColor = Color.Transparent
                )
            )
            if (onClick != null) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AppColors.grisIcono,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
