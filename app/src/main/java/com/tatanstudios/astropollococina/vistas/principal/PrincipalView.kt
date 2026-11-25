package com.tatanstudios.astropollococina.vistas.principal

import com.tatanstudios.astropollococina.extras.SesionApi
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.componentes.CustomModalCerrarSesion
import com.tatanstudios.astropollococina.componentes.DrawerBody
import com.tatanstudios.astropollococina.componentes.DrawerHeader
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.extras.itemsMenu
import com.tatanstudios.astropollococina.model.rutas.Routes
import com.tatanstudios.astropollococina.viewmodel.NuevasOrdenesViewModel
import com.tatanstudios.astropollococina.vistas.login.getOneSignalUserId
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tatanstudios.astropollococina.componentes.CardNuevaOrden
import com.tatanstudios.astropollococina.componentes.CustomModal1Boton
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.componentes.LoadingModal
import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.model.ordenes.ModeloNuevasOrdenesArray
import kotlinx.coroutines.flow.first
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PrincipalScreen(
    navController: NavHostController,
    viewModel: NuevasOrdenesViewModel = viewModel()
) {

    val ctx = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showModalCerrarSesion by remember { mutableStateOf(false) }
    var boolDatosCargados by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.observeAsState(true)
    val tokenManager = remember { TokenManager(ctx) }
    val resultado by viewModel.resultado.observeAsState()
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    val keyboardController = LocalSoftwareKeyboardController.current
    var _idusuario by remember { mutableStateOf("") }
    var _idonesignal by remember { mutableStateOf("") }

    var popUsuarioBloqueado by remember { mutableStateOf(false) }

    // si falta un ajuste para recibir pedidos siempre (permiso, bateria, pausa por inactividad), avisar al entrar
    var avisoAjustesNotificaciones by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val n = com.tatanstudios.astropollococina.extras.Notificaciones
        if (!n.avisoAjustesMostrado && n.faltaAlgo(ctx)) {
            n.avisoAjustesMostrado = true
            avisoAjustesNotificaciones = true
        }
    }
    var modeloListaOrdenesNuevasArray by remember { mutableStateOf(listOf<ModeloNuevasOrdenesArray>()) }
    // restaurante y usuario con sesion (vienen con el listado de ordenes nuevas)
    var nombreRestaurante by remember { mutableStateOf<String?>(null) }
    var nombreUsuario by remember { mutableStateOf<String?>(null) }
    var loginUsuario by remember { mutableStateOf<String?>(null) }
    var popErrorCargar by remember { mutableStateOf(false) }


    // recarga pedida por el usuario (deslizar o boton): se muestra el indicador y al final un aviso
    var recargaManual by remember { mutableStateOf(false) }
    val refreshing = recargaManual && isLoading
    val errorCarga by viewModel.error.observeAsState(false)
    fun recargar() {
        if (_idusuario.isEmpty()) return
        recargaManual = true
        viewModel.nuevasOrdenesRetrofit(_idusuario, _idonesignal)
    }
    LaunchedEffect(isLoading) {
        if (!isLoading && recargaManual) {
            recargaManual = false
            if (errorCarga) {
                CustomToasty(ctx, "No se pudo actualizar. Revisa tu internet.", ToastType.ERROR)
            } else {
                val n = modeloListaOrdenesNuevasArray.size
                CustomToasty(
                    ctx,
                    if (n == 0) "Lista actualizada · sin órdenes nuevas" else "Lista actualizada · $n ${if (n == 1) "orden nueva" else "órdenes nuevas"}",
                    ToastType.SUCCESS
                )
            }
        }
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { recargar() }
    )

    LaunchedEffect(Unit) {

            _idusuario = tokenManager.idUsuario.first()
            _idonesignal = getOneSignalUserId()

            viewModel.nuevasOrdenesRetrofit(_idusuario, _idonesignal)

            // actualizar sola cada 30 s mientras esta pantalla está abierta
            // (al ir a otra pantalla se detiene; al volver se recarga de nuevo)
            while (true) {
                kotlinx.coroutines.delay(30_000)
                if (_idusuario.isNotEmpty()) viewModel.nuevasOrdenesRetrofit(_idusuario, _idonesignal)
            }
    }

    // ocultar teclado
    keyboardController?.hide()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerShape = androidx.compose.foundation.shape.RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
            ) {
                com.tatanstudios.astropollococina.componentes.MenuLateral(
                    cantidadNuevas = modeloListaOrdenesNuevasArray.size,
                    version = getVersionName(ctx),
                    restaurante = nombreRestaurante,
                    usuario = nombreUsuario,
                    login = loginUsuario,
                ) { opcion ->
                    when (opcion) {
                        1 -> {

                            recargar()
                        }
                        3 -> {

                            navController.navigate(Routes.VistaListadoOrdenCompletadas.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }
                        4 -> {

                            navController.navigate(Routes.VistaListadoOrdenCanceladas.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }
                        5 -> { // CATEGORIAS

                            navController.navigate(Routes.VistaListadoCategorias.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }
                        6 -> { // NOTIFICACION
                            navController.navigate(Routes.VistaNotificaciones.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }
                        7 -> { // HISTORIAL

                            navController.navigate(Routes.VistaHistorialFecha.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }
                        9 -> { // WHATSAPP
                            navController.navigate(Routes.VistaWhatsApp.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }
                        8 -> {
                            // cerrar sesion
                            showModalCerrarSesion = true
                        }
                    }

                    scope.launch {
                        drawerState.close()
                    }
                }

            }
        }
    ) {

        Scaffold(
            containerColor = com.tatanstudios.astropollococina.componentes.AppColors.crema,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.nuevas_ordenes) +
                                if (modeloListaOrdenesNuevasArray.isNotEmpty()) " (${modeloListaOrdenesNuevasArray.size})" else "",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { recargar() }, enabled = !refreshing) {
                            if (refreshing) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(Icons.Filled.Refresh, contentDescription = "Recargar", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = com.tatanstudios.astropollococina.componentes.AppColors.rojoBarra,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->


            if (modeloListaOrdenesNuevasArray.isEmpty() && boolDatosCargados) {
                // Mostrar imagen si la lista está vacía

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .pullRefresh(pullRefreshState), // también se puede recargar sin órdenes
                    contentAlignment = Alignment.Center
                ) {
                    com.tatanstudios.astropollococina.componentes.EstadoVacio(
                        icono = androidx.compose.material.icons.Icons.Filled.NotificationsNone,
                        titulo = stringResource(R.string.no_hay_ordenes_nuevas),
                        mensaje = "Te avisaremos cuando llegue una orden. La lista se actualiza sola cada 30 segundos o puedes deslizar hacia abajo."
                    )

                    PullRefreshIndicator(
                        refreshing = refreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }

            }else{
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .pullRefresh(pullRefreshState) // si usas pull-to-refresh
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().background(com.tatanstudios.astropollococina.componentes.AppColors.crema),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        modeloListaOrdenesNuevasArray.forEach { tipoOrden ->
                            item {
                                CardNuevaOrden(
                                    orden = tipoOrden.id.toString(),
                                    fecha = tipoOrden.fechaOrden,
                                    venta = tipoOrden.totalFormat,
                                    haycupon = tipoOrden.haycupon,
                                    cupon = tipoOrden.mensajeCupon,
                                    cliente = tipoOrden.cliente,
                                    direccion = tipoOrden.direccion,
                                    referencia = tipoOrden.referencia,
                                    telefono = tipoOrden.telefono,
                                    nota = tipoOrden.notaOrden,
                                    onClick = {
                                        navController.navigate(
                                            Routes.VistaEstadoNuevaOrden.createRoute(
                                                tipoOrden.id.toString(),
                                            ),
                                            navOptions {
                                                launchSingleTop = true
                                            }
                                        )
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = refreshing,
                        state = pullRefreshState,
                        modifier = Modifier
                            .align(Alignment.TopCenter) // ✅ ahora dentro de BoxScope
                    )
                }

            }




            if (showModalCerrarSesion) {
                com.tatanstudios.astropollococina.componentes.CustomModal2Botones(
                    showDialog = true,
                    titulo = "¿Cerrar sesión?",
                    message = "Tendrás que ingresar de nuevo con tu usuario y contraseña.",
                    txtBtnAceptar = "Cerrar sesión",
                    txtBtnCancelar = "Cancelar",
                    colorAceptar = com.tatanstudios.astropollococina.componentes.AppColors.rojoOscuro,
                    onDismiss = { showModalCerrarSesion = false },
                    onAccept = {
                        scope.launch {
                            // revoca el token en la API y borra la sesion local
                            SesionApi.cerrarSesion()

                            // cerrar modal
                            showModalCerrarSesion = false

                            navigateToLogin(navController)
                        }
                    })
            }

            if (avisoAjustesNotificaciones) {
                com.tatanstudios.astropollococina.componentes.CustomModal2Botones(
                    showDialog = true,
                    titulo = "Para que te lleguen todos los pedidos",
                    message = "Este teléfono puede dejar de avisarte de pedidos nuevos si pasa tiempo sin abrir la app. Revisa los ajustes de notificaciones (toma un minuto).",
                    txtBtnAceptar = "Revisar",
                    txtBtnCancelar = "Ahora no",
                    onDismiss = { avisoAjustesNotificaciones = false },
                    onAccept = {
                        avisoAjustesNotificaciones = false
                        navController.navigate(Routes.VistaNotificaciones.route) { launchSingleTop = true }
                    },
                )
            }

            if (popUsuarioBloqueado) {
                CustomModal1Boton(
                    popUsuarioBloqueado,
                    stringResource(R.string.usuario_bloqueado),
                    onDismiss = {
                        scope.launch {
                            SesionApi.cerrarSesion()
                            popUsuarioBloqueado = false
                            navigateToLogin(navController)
                        }
                    })
            }

            if (popErrorCargar) {
                CustomModal1Boton(
                    popErrorCargar,
                    stringResource(R.string.error_reintentar_de_nuevo),
                    onDismiss = {
                        popErrorCargar = false // antes no se cerraba nunca
                        scope.launch {
                            viewModel.nuevasOrdenesRetrofit(_idusuario, null)
                        }
                    })
            }

            // primera carga: skeleton; las actualizaciones automáticas van en silencio
            if (isLoading && !boolDatosCargados) {
                Box(Modifier.fillMaxSize().padding(innerPadding).background(com.tatanstudios.astropollococina.componentes.AppColors.crema)) {
                    com.tatanstudios.astropollococina.componentes.ListaSkeleton(cantidad = 3, alto = 200.dp)
                }
            }
            // sin datos y la carga falló: panel con Reintentar
            if (!boolDatosCargados) {
                Box(Modifier.fillMaxSize().padding(innerPadding)) {
                    com.tatanstudios.astropollococina.componentes.ErrorReintentar(viewModel, isLoading)
                }
            }
        }


        resultado?.getContentIfNotHandled()?.let { result ->
            when (result.success) {

                1 -> {
                    // USUARIO BLOQUEADO
                    popUsuarioBloqueado = true

                }
                2 -> {
                    // CARGA LA PANTALLA PRINCIPAL

                    modeloListaOrdenesNuevasArray = result.lista
                    result.restaurante?.let { nombreRestaurante = it }
                    result.usuario?.let { nombreUsuario = it }
                    result.login?.let { loginUsuario = it }
                    boolDatosCargados = true
                }
                else -> {
                    // Error, recargar de nuevo
                    CustomToasty(
                        ctx,
                        stringResource(id = R.string.error_reintentar_de_nuevo),
                        ToastType.ERROR
                    )
                }
            }
        }
    }
}


fun getVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "N/A"
    } catch (e: PackageManager.NameNotFoundException) {
        "N/A"
    }
}


// redireccionar a vista login
private fun navigateToLogin(navController: NavHostController) {
    navController.navigate(Routes.VistaLogin.route) {
        popUpTo(Routes.VistaPrincipal.route) {
            inclusive = true // Elimina VistaPrincipal de la pila
        }
        launchSingleTop = true // Asegura que no se creen múltiples instancias de VistaLogin
    }
}

