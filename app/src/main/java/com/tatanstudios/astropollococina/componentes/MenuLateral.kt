package com.tatanstudios.astropollococina.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tatanstudios.astropollococina.R

/**
 * Contenido del menú lateral. Los ids son los mismos que usa la pantalla principal:
 * 1 nuevas, 3 iniciadas hoy, 4 canceladas hoy, 5 categorías, 6 notificaciones, 7 historial, 8 cerrar sesión.
 */
@Composable
fun MenuLateral(
    cantidadNuevas: Int,
    version: String,
    restaurante: String?,
    usuario: String?,
    login: String?,
    onOpcion: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        // encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.rojoBarra)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logonegrocirculo),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                restaurante?.takeIf { it.isNotBlank() } ?: "Astro Pollo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text("Control de órdenes", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            Seccion("Órdenes")
            OpcionMenu(Icons.Filled.ShoppingCart, "Nuevas órdenes", AppColors.ambar, AppColors.ambarFondo, seleccionada = true,
                contador = cantidadNuevas) { onOpcion(1) }
            OpcionMenu(Icons.Filled.Restaurant, "Iniciadas hoy", AppColors.verde, AppColors.verdeFondo) { onOpcion(3) }
            OpcionMenu(Icons.Filled.Cancel, "Canceladas hoy", AppColors.rojoOscuro, AppColors.rojoFondo) { onOpcion(4) }
            OpcionMenu(Icons.AutoMirrored.Filled.ReceiptLong, "Historial", AppColors.letraPrincipal, AppColors.grisSuave) { onOpcion(7) }

            Seccion("Menú")
            OpcionMenu(Icons.Filled.Category, "Categorías y productos", AppColors.letraPrincipal, AppColors.grisSuave) { onOpcion(5) }

            Seccion("Ajustes")
            OpcionMenu(Icons.Filled.NotificationsActive, "Notificaciones", AppColors.letraPrincipal, AppColors.grisSuave) { onOpcion(6) }
        }

        HorizontalDivider(color = AppColors.grisSuave)
        OpcionMenu(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", AppColors.rojoOscuro, AppColors.rojoFondo, colorTexto = AppColors.rojoOscuro) { onOpcion(8) }
        Text(
            "Versión $version",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.grisIcono,
            modifier = Modifier.padding(start = 24.dp, bottom = 14.dp, top = 2.dp)
        )
    }
}

@Composable
private fun Seccion(titulo: String) {
    Text(
        titulo.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.grisIcono,
        modifier = Modifier.padding(start = 24.dp, top = 14.dp, bottom = 6.dp)
    )
}

@Composable
private fun OpcionMenu(
    icono: ImageVector,
    texto: String,
    colorIcono: Color,
    fondoIcono: Color,
    seleccionada: Boolean = false,
    contador: Int = 0,
    colorTexto: Color = AppColors.letraPrincipal,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (seleccionada) AppColors.rojo.copy(alpha = 0.07f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(fondoIcono),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (seleccionada) FontWeight.SemiBold else FontWeight.Medium,
            color = colorTexto,
            modifier = Modifier.weight(1f)
        )
        if (contador > 0) {
            Text(
                if (contador > 99) "99+" else contador.toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(AppColors.rojo)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
