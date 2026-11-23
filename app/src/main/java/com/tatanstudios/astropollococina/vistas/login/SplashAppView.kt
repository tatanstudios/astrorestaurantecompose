package com.tatanstudios.astropollococina.vistas.login

import com.tatanstudios.astropollococina.componentes.ToastType
import com.tatanstudios.astropollococina.componentes.CustomToasty
import com.tatanstudios.astropollococina.extras.SesionApi
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.first
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tatanstudios.astropollococina.R
import com.tatanstudios.astropollococina.extras.TokenManager
import com.tatanstudios.astropollococina.model.rutas.Routes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.LottieConstants
import com.tatanstudios.astropollococina.vistas.opciones.categorias.ListadoCategoriasScreen
import com.tatanstudios.astropollococina.vistas.opciones.categorias.ListadoProductosCategoriasScreen
import com.tatanstudios.astropollococina.vistas.opciones.historial.HistorialFechaScreen
import com.tatanstudios.astropollococina.vistas.opciones.historial.HistorialOrdenScreen
import com.tatanstudios.astropollococina.vistas.opciones.historial.ListadoProductosHistorialScreen
import com.tatanstudios.astropollococina.vistas.opciones.notificacion.NotificacionScreen
import com.tatanstudios.astropollococina.vistas.opciones.ordencanceladas.ListadoCanceladasOrdenScreen
import com.tatanstudios.astropollococina.vistas.opciones.ordencanceladas.ListadoProductosOrdenScreen
import com.tatanstudios.astropollococina.vistas.opciones.ordencompletadas.ListadoCompletadasOrdenScreen
import com.tatanstudios.astropollococina.vistas.opciones.ordenesnuevas.EstadoNuevaOrdenScreen
import com.tatanstudios.astropollococina.vistas.opciones.ordenesnuevas.InfoProductoScreen
import com.tatanstudios.astropollococina.vistas.principal.PrincipalScreen

class SplashApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MODO VERTICAL
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        enableEdgeToEdge()
        setContent {
            // INICIO DE APLICACION
            AppNavigation()
        }
    }
}



// *** RUTAS DE NAVEGACION ***
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val ctxNav = LocalContext.current
    val tokenManager = remember { TokenManager(ctxNav) }

    // Sin splash: se decide al instante si va al login o a la pantalla principal
    var inicio by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        tokenManager.migrateFromSharedPreferencesIfNeeded()
        inicio = when {
            // sesion de la version anterior (sin token): al login con aviso
            tokenManager.limpiarSesionAnteriorSinToken() -> {
                SesionApi.avisoIniciarSesionDeNuevo = true
                Routes.VistaLogin.route
            }
            tokenManager.idUsuario.first().isNotEmpty() -> {
                com.tatanstudios.astropollococina.extras.Notificaciones.vincular(tokenManager.idUsuario.first())
                Routes.VistaPrincipal.route
            }
            else -> Routes.VistaLogin.route
        }
    }

    val destinoInicial = inicio
    if (destinoInicial == null) {
        // solo dura lo que tarda en leer la sesion guardada (milisegundos)
        Box(Modifier.fillMaxSize().background(Color(0xFFFFFFF4)))
        return
    }

    // la API rechazo el token (contrasena cambiada o usuario bloqueado desde el panel)
    LaunchedEffect(Unit) {
        SesionApi.sesionExpirada.collect {
            CustomToasty(ctxNav, "Tu sesión terminó, inicia sesión de nuevo", ToastType.INFO)
            navController.navigate(Routes.VistaLogin.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = destinoInicial) {

        composable(Routes.VistaLogin.route) { LoginScreen(navController) }

        composable(Routes.VistaPrincipal.route) { PrincipalScreen(navController) }

        // Cuando se Toca la Card de nuevas ordenes
        composable(Routes.VistaEstadoNuevaOrden.route) { backStackEntry ->
            val idordenStr = backStackEntry.arguments?.getString("idorden") ?: "0"
            val idorden = idordenStr.toIntOrNull() ?: 0

            EstadoNuevaOrdenScreen(navController = navController, _idorden = idorden)
        }

        // cuando se toca un producto y se vera su informacion
        composable(Routes.VistaInfoProductoOrden.route) { backStackEntry ->
            val idproductoStr = backStackEntry.arguments?.getString("idproducto") ?: "0"
            val idproducto = idproductoStr.toIntOrNull() ?: 0

            InfoProductoScreen(navController = navController, _idproducto = idproducto)
        }

        // LISTADO DE ORDENES INICIADAS HOY
        composable(Routes.VistaListadoOrdenCompletadas.route) { ListadoCompletadasOrdenScreen(navController) }


        // LISTADO DE ORDENES CANCELADAS HOY
        composable(Routes.VistaListadoOrdenCanceladas.route) { ListadoCanceladasOrdenScreen(navController) }


        // LISTADO DE PRODUCTOS DE UNA ORDEN
        composable(Routes.VistaListadoProductoOrden.route) { backStackEntry ->
            val idordenStr = backStackEntry.arguments?.getString("idorden") ?: "0"
            val idorden = idordenStr.toIntOrNull() ?: 0

            ListadoProductosOrdenScreen(navController = navController, _idorden = idorden)
        }

        // LISTADO DE CATEGORIAS
        composable(Routes.VistaListadoCategorias.route) { ListadoCategoriasScreen(navController) }

        // LISTADO DE PRODUCTOS DE UNA CATEGORIA
        composable(Routes.VistaListaProductosCategorias.route) { backStackEntry ->
            val idcategoriaStr = backStackEntry.arguments?.getString("idcategoria") ?: "0"
            val idcategoria = idcategoriaStr.toIntOrNull() ?: 0

            ListadoProductosCategoriasScreen(navController = navController, _idcategoria = idcategoria)
        }


        // HISTORIAL FECHA
        composable(Routes.VistaHistorialFecha.route) { HistorialFechaScreen(navController) }


        // HISTORIAL LISTADO ORDENES
        composable(Routes.VistaHistorialListadoOrden.route) { backStackEntry ->
            val fecha1 = backStackEntry.arguments?.getString("fecha1") ?: ""
            val fecha2 = backStackEntry.arguments?.getString("fecha2") ?: ""

            HistorialOrdenScreen(navController = navController, _fecha1 = fecha1, _fecha2 = fecha2)
        }


        // LISTADO DE PRODUCTOS HISTORIAL ORDEN
        composable(Routes.VistaListadoProductosHistorialOrden.route) { backStackEntry ->
            val idordenStr = backStackEntry.arguments?.getString("idorden") ?: "0"
            val idorden = idordenStr.toIntOrNull() ?: 0

            ListadoProductosHistorialScreen(navController = navController, _idorden = idorden)
        }

        // NOTIFICACIONES
        composable(Routes.VistaNotificaciones.route) { NotificacionScreen(navController) }


    }
}
