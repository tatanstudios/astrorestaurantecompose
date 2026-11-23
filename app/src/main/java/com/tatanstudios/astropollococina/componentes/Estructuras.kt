package com.tatanstudios.astropollococina.componentes

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.extras.ItemsMenuLateral
import es.dmoral.toasty.Toasty

@Composable
fun BloqueTextFieldLogin(text: String, onTextChanged: (String) -> Unit, maxLength: Int) {
    // Color común para el texto del placeholder y la línea
    val commonColor = Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White) // Fondo blanco
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Ícono al inicio con color gris claro
        Icon(
            imageVector = Icons.Filled.Person,  // Aquí puedes poner el ícono que prefieras
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp) // Espacio entre el ícono y el TextField
                .size(24.dp), // Tamaño del ícono
            tint = commonColor // Usamos el color común para el ícono
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = commonColor, // Usamos el mismo color para la línea
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            TextField(
                value = text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { newText ->
                    if (newText.length <= maxLength) {
                        onTextChanged(newText)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 17.sp, // Tamaño del texto
                    fontWeight = FontWeight.Normal // Negrita
                ),
                placeholder = { Text(text = stringResource(id = R.string.usuario), color = commonColor) }, // Color del placeholder
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White, // Fondo blanco cuando está enfocado
                    unfocusedContainerColor = Color.White, // Fondo blanco cuando no está enfocado
                    disabledContainerColor = Color.White, // Fondo blanco cuando está deshabilitado
                    errorContainerColor = Color.White, // Fondo blanco si hay error
                    focusedIndicatorColor = commonColor, // Color de la línea cuando está enfocado
                    unfocusedIndicatorColor = commonColor  // Color de la línea cuando no está enfocado
                ),
            )
        }
    }
}

@Composable
fun BloqueTextFieldPassword(
    text: String,
    onTextChanged: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChanged: (Boolean) -> Unit,
    maxLength: Int
) {
    // Color común para el texto del placeholder y la línea
    val commonColor = Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White) // Fondo blanco
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Ícono al inicio con color gris claro
        Icon(
            imageVector = Icons.Filled.Lock,  // Puedes cambiarlo por otro ícono si lo prefieres
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp) // Espacio entre el ícono y el TextField
                .size(24.dp), // Tamaño del ícono
            tint = commonColor // Usamos el color común para el ícono
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = commonColor, // Usamos el mismo color para la línea
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
        ) {
            TextField(
                value = text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { newText ->
                    if (newText.length <= maxLength) {  // Limitar la longitud de la contraseña si lo deseas
                        onTextChanged(newText)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 17.sp, // Tamaño del texto
                    fontWeight = FontWeight.Normal // Negrita
                ),
                placeholder = { Text(text = stringResource(id = R.string.contrasena), color = commonColor) }, // Color del placeholder
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChanged(!isPasswordVisible) }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = commonColor
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White, // Fondo blanco cuando está enfocado
                    unfocusedContainerColor = Color.White, // Fondo blanco cuando no está enfocado
                    disabledContainerColor = Color.White, // Fondo blanco cuando está deshabilitado
                    errorContainerColor = Color.White, // Fondo blanco si hay error
                    focusedIndicatorColor = commonColor, // Color de la línea cuando está enfocado
                    unfocusedIndicatorColor = commonColor  // Color de la línea cuando no está enfocado
                ),
            )
        }
    }
}



@Composable
fun CustomModal1Boton(showDialog: Boolean, message: String, onDismiss: () -> Unit) {
    if (showDialog) {
        DialogoBase(titulo = null, mensaje = message, icono = Icons.Filled.Info, colorIcono = AppColors.letraPrincipal) {
            BotonDialogo("Aceptar", AppColors.rojo, Modifier.fillMaxWidth(), onDismiss)
        }
    }
}

/** Base comun de los avisos: tarjeta blanca redondeada, icono en circulo, titulo y mensaje. */
@Composable
fun DialogoBase(
    titulo: String?,
    mensaje: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorIcono: Color,
    botones: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(22.dp))
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colorIcono.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.height(14.dp))
            if (!titulo.isNullOrBlank()) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.letraPrincipal,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
            }
            Text(
                mensaje,
                style = if (titulo.isNullOrBlank()) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                fontWeight = if (titulo.isNullOrBlank()) FontWeight.SemiBold else FontWeight.Normal,
                color = if (titulo.isNullOrBlank()) AppColors.letraPrincipal else AppColors.grisIcono,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))
            botones()
        }
    }
}

@Composable
fun BotonDialogo(texto: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White),
        modifier = modifier.height(48.dp)
    ) { Text(texto, fontWeight = FontWeight.SemiBold) }
}


@Composable
fun CustomModal1BotonTitulo(
    showDialog: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        DialogoBase(titulo = title, mensaje = message, icono = Icons.Filled.Info, colorIcono = AppColors.letraPrincipal) {
            BotonDialogo("Aceptar", AppColors.rojo, Modifier.fillMaxWidth(), onDismiss)
        }
    }
}




@Composable
fun LoadingModal(isLoading: Boolean, titulo:String = "Cargando...") {
    if (isLoading) {
        Dialog(onDismissRequest = { /* Evitar que se cierre el modal */ }) {
            Row(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(18.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(color = AppColors.rojo, strokeWidth = 3.dp, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = AppColors.letraPrincipal)
            }
        }
    }
}


enum class ToastType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}

fun CustomToasty(context: Context, message: String, type: ToastType) {
    when (type) {
        ToastType.SUCCESS -> Toasty.success(context, message, Toasty.LENGTH_SHORT, true).show()
        ToastType.ERROR -> Toasty.error(context, message, Toasty.LENGTH_SHORT, true).show()
        ToastType.INFO -> Toasty.info(context, message, Toasty.LENGTH_SHORT, true).show()
        ToastType.WARNING -> Toasty.warning(context, message, Toasty.LENGTH_SHORT, true).show()
    }
}



val MontserratFont = FontFamily(
    Font(R.font.montserratregular, FontWeight.Normal),
    Font(R.font.montserratmedium, FontWeight.Bold)
)

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(Color.Red)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp), // Este padding es interno, está bien
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logonegrocirculo),
                contentDescription = stringResource(R.string.logotipo),
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}


@Composable
fun DrawerBody(
    items: List<ItemsMenuLateral>,
    onItemClick: (ItemsMenuLateral) -> Unit
) {
    Column {
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = {
                    Text(
                        stringResource(id = item.idString),
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                },
                selected = false,
                onClick = { onItemClick(item) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}



@Composable
fun CustomModalCerrarSesion(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = message,
                        fontSize = 18.sp,
                        color = colorResource(R.color.colorNegro),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.colorGrisv1),
                                contentColor = colorResource(R.color.colorBlanco),
                            ),
                        ) {
                            Text(stringResource(id = R.string.no), color = Color.White)
                        }

                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.colorAzul),
                                contentColor = colorResource(R.color.colorBlanco)
                            ),
                        ) {
                            Text(stringResource(id = R.string.si), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun CardNuevaOrden(
    orden: String,
    fecha: String,
    venta: String,
    haycupon: Int,
    cupon: String?,
    cliente: String,
    direccion: String,
    referencia: String?,
    telefono: String?,
    nota: String?,
    onClick: () -> Unit
) {
    TarjetaBlanca(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Orden #$orden",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.letraPrincipal,
                modifier = Modifier.weight(1f)
            )
            Chip("Nueva", AppColors.ambarFondo, AppColors.ambar)
        }
        DatoConIcono(androidx.compose.material.icons.Icons.Filled.CalendarMonth, fecha)
        DatoConIcono(androidx.compose.material.icons.Icons.Filled.Person, cliente, AppColors.letraPrincipal)
        if (!telefono.isNullOrBlank()) DatoConIcono(androidx.compose.material.icons.Icons.Filled.Phone, telefono)
        DatoConIcono(androidx.compose.material.icons.Icons.Filled.LocationOn, direccion)
        if (!referencia.isNullOrBlank()) DatoConIcono(androidx.compose.material.icons.Icons.Filled.Place, "Referencia: $referencia")
        if (!nota.isNullOrBlank()) DatoConIcono(androidx.compose.material.icons.Icons.Filled.EditNote, "Nota: $nota", AppColors.rojoOscuro)
        if (haycupon == 1 && !cupon.isNullOrBlank()) Chip("Cupón: $cupon", AppColors.verdeFondo, AppColors.verde)

        androidx.compose.material3.HorizontalDivider(color = AppColors.grisSuave)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Total", style = MaterialTheme.typography.labelMedium, color = AppColors.grisIcono)
                Text(venta, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.rojo)
            }
            Text("Ver orden", color = AppColors.rojo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AppColors.rojo)
        }
    }
}

@Composable
fun CampoTexto(etiqueta: String, valor: String?,
               colorEtiqueta: Color = Color.Black
) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$etiqueta: ",
            fontWeight = FontWeight.Bold,
            color = colorEtiqueta,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = valor ?: "",
            color = colorEtiqueta,
            fontSize = 17.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun CustomModal2Botones(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    txtBtnAceptar: String,
    txtBtnCancelar: String,
    colorAceptar: Color = AppColors.rojo,
    titulo: String? = null,
) {
    if (showDialog) {
        DialogoBase(titulo = titulo, mensaje = message, icono = Icons.AutoMirrored.Filled.HelpOutline, colorIcono = colorAceptar) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D0D0)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.grisTexto),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text(txtBtnCancelar, fontWeight = FontWeight.SemiBold) }
                BotonDialogo(txtBtnAceptar, colorAceptar, Modifier.weight(1f), onAccept)
            }
        }
    }
}




@Composable
fun ProductoItemCard(
    cantidad: String,
    hayImagen: Int,
    imagenUrl: String,
    titulo: String?,
    descripcion: String?, // lo que el cliente escribe ejemplo (pollo, res)
    precio: String,
    modifier: Modifier = Modifier,
    opciones: List<String> = emptyList(), // modificadores elegidos por el cliente
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box {
                if (hayImagen == 1) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imagenUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.spinloading)
                            .error(R.drawable.camaradefecto)
                            .build(),
                        contentDescription = stringResource(R.string.imagen_por_defecto),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, AppColors.grisSuave, RoundedCornerShape(12.dp))
                            .padding(3.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.camaradefecto),
                        contentDescription = stringResource(R.string.imagen_por_defecto),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, AppColors.grisSuave, RoundedCornerShape(12.dp))
                            .padding(3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // cantidad bien visible para la cocina
                    Text(
                        "${cantidad}x",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.rojo)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = titulo ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.letraPrincipal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                LineasOpcionesCocina(opciones)
                if (!descripcion.isNullOrBlank()) {
                    Text(
                        text = "Nota: $descripcion",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.rojoOscuro
                    )
                }
                Text(
                    text = precio,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.grisTexto
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppColors.grisIcono,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}




@Composable
fun CardCompletadasOrden(
    orden: String,
    fecha: String,
    venta: String,
    haycupon: Int,
    cupon: String?,
    cliente: String,
    direccion: String,
    referencia: String?,
    telefono: String?,
    nota: String?,
    fechaFinalizo: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CampoTexto(stringResource(R.string.orden_numeral), orden)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.fecha), fecha)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.finalizo), fechaFinalizo) // hora en que se inicio
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.venta), venta)

            if (haycupon == 1){
                Spacer(modifier = Modifier.height(6.dp))
                CampoTexto(stringResource(R.string.cupon), cupon, colorResource(R.color.colorRojo)) }
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.cliente), cliente)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.direccion), direccion)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.referencia), referencia)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.telefono), telefono)
            if (!nota.isNullOrBlank()) CampoTexto(stringResource(R.string.nota), nota)
        }
    }
}




@Composable
fun CardCanceladasOrden(
    orden: String,
    fecha: String,
    venta: String,
    haycupon: Int,
    cupon: String?,
    cliente: String,
    direccion: String,
    referencia: String?,
    telefono: String?,
    nota: String?,
    fechaCancelo: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CampoTexto(stringResource(R.string.orden_numeral), orden)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.fecha), fecha)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.cancelada), fechaCancelo)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.venta), venta)

            if (haycupon == 1){
                Spacer(modifier = Modifier.height(6.dp))
                CampoTexto(stringResource(R.string.cupon), cupon, colorResource(R.color.colorRojo)) }
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.cliente), cliente)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.direccion), direccion)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.referencia), referencia)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.telefono), telefono)

            if (!nota.isNullOrBlank()) CampoTexto(stringResource(R.string.nota), nota)
        }
    }
}




@Composable
fun CardCategorias(
    imagenUrl: String,
    titulo: String,
    estado: String?,
    horario: String?,
    usahorario: Int,
    activo: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen desde URL con Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imagenUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.spinloading)
                    .error(R.drawable.camaradefecto)
                    .build(),
                contentDescription = stringResource(R.string.imagen_por_defecto),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildAnnotatedString {
                        if (activo == 0) {
                            // "Estado:" en negro normal
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append(stringResource(R.string.estado) + ": ")
                            }
                            // Estado en rojo
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append(estado ?: "")
                            }
                        } else {
                            // Solo el estado en negro y negrita
                            withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) {
                                append(estado ?: "")
                            }
                        }
                    },
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Unspecified // No forzamos color aquí porque usamos estilos internos
                )

                Spacer(modifier = Modifier.height(2.dp))

                if(usahorario == 1){
                    val texto = stringResource(R.string.por) + " " + horario
                    Text(
                        text = texto,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }

            }
        }
    }
}




@Composable
fun ProductoCategoriaItemCard(
    imagenUrl: String,
    titulo: String,
    estado: String?,
    activo: Int,
    utilizaImagen: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen desde URL con Coil
            if(utilizaImagen == 1){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imagenUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.spinloading)
                        .error(R.drawable.camaradefecto)
                        .build(),
                    contentDescription = stringResource(R.string.imagen_por_defecto),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.camaradefecto),
                    contentDescription = stringResource(R.string.imagen_por_defecto),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildAnnotatedString {
                        if (activo == 0) {
                            // "Estado:" en negro normal
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append(stringResource(R.string.estado) + ": ")
                            }
                            // Estado en rojo
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append(estado ?: "")
                            }
                        } else {
                            // Solo el estado en negro y negrita
                            withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) {
                                append(estado ?: "")
                            }
                        }
                    },
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Unspecified // No forzamos color aquí porque usamos estilos internos
                )

                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}





@Composable
fun CardHistorialOrden(
    orden: String,
    fecha: String,
    venta: String,
    estado: String?,
    haycupon: Int,
    cupon: String?,
    cliente: String,
    direccion: String,
    telefono: String?,
    nota: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CampoTexto(stringResource(R.string.orden_numeral), orden)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.fecha), fecha)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.venta), venta)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.estado), estado)

            if (haycupon == 1){
                Spacer(modifier = Modifier.height(6.dp))
                CampoTexto(stringResource(R.string.cupon), cupon, colorResource(R.color.colorRojo)) }
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.cliente), cliente)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.direccion), direccion)
            Spacer(modifier = Modifier.height(6.dp))
            CampoTexto(stringResource(R.string.telefono), telefono)

            if (!nota.isNullOrBlank()) CampoTexto(stringResource(R.string.nota), nota)
        }
    }
}




@Composable
fun ProductoListadoHistorialItemCard(
    cantidad: String,
  hayImagen: Int,
  imagenUrl: String,
  titulo: String?,
  descripcion: String?, // lo que el cliente escribe ejemplo (pollo, res)
  precio: String,
  modifier: Modifier = Modifier,
  opciones: List<String> = emptyList(), // modificadores elegidos por el cliente
  onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cantidad
            Box(
                modifier = Modifier
                    .background(color = colorResource(R.color.colorAzul), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${cantidad}x",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            // Imagen del producto desde URL
            if(hayImagen == 1){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imagenUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.spinloading)
                        .error(R.drawable.camaradefecto)
                        .build(),
                    contentDescription = stringResource(R.string.imagen_por_defecto),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.camaradefecto),
                    contentDescription = stringResource(R.string.imagen_por_defecto),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }


            Spacer(modifier = Modifier.width(8.dp))

            // Título y descripción
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo?: "",
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis
                )
                LineasOpcionesCocina(opciones)
                if(!descripcion.isNullOrBlank()){
                    Text(
                        text = descripcion?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Precio
            Text(
                text = precio,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


/**
 * Opciones que eligió el cliente (modificadores), una línea por bloque.
 * ej. "Elegir sabor: Naranja". Se destacan para que cocina no las pase por alto.
 */
@Composable
fun LineasOpcionesCocina(opciones: List<String>) {
    if (opciones.isEmpty()) return
    Column(modifier = Modifier.padding(top = 2.dp)) {
        opciones.forEach { linea ->
            val partes = linea.split(": ", limit = 2)
            Text(
                text = androidx.compose.ui.text.buildAnnotatedString {
                    append("• ")
                    if (partes.size == 2) {
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.SemiBold))
                        append(partes[0] + ": ")
                        pop()
                        append(partes[1])
                    } else {
                        append(linea)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF0D2C55),
            )
        }
    }
}
