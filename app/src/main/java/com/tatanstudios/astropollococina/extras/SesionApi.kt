package com.tatanstudios.astropollococina.extras

import android.content.Context
import com.tatanstudios.astropollococina.network.RetrofitBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Token de Sanctum con el que la app se identifica ante la API.
 *
 * - Se entrega al iniciar sesión o registrarse y se guarda en DataStore (TokenManager).
 * - Cada petición lo envía en "Authorization: Bearer <token>" (ver RetrofitBuilder).
 * - No vence: solo deja de servir si se cierra sesión, se cambia la contraseña o el usuario es bloqueado.
 *   En ese caso la API responde 401, se borra la sesión y la app vuelve al login ([sesionExpirada]).
 */
object SesionApi {

    private lateinit var appContext: Context
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // copia en memoria para no leer DataStore en cada petición
    @Volatile
    private var tokenEnMemoria: String? = null

    /** true cuando el usuario venia con sesion de una version anterior: el login muestra el aviso. */
    var avisoIniciarSesionDeNuevo = false

    const val MENSAJE_INICIAR_SESION_DE_NUEVO = "Por seguridad actualizamos la app. Inicia sesión de nuevo con tu usuario y contraseña."

    private val _sesionExpirada = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Emite cuando la API rechaza el token; la navegación principal lleva al login. */
    val sesionExpirada: SharedFlow<Unit> = _sesionExpirada.asSharedFlow()

    /** Se llama una vez al iniciar la app (SistemaController). */
    fun iniciar(context: Context) {
        appContext = context.applicationContext
    }

    /** Token actual; lo usa el interceptor de OkHttp (hilo de red, por eso puede bloquear). */
    fun token(): String =
        tokenEnMemoria ?: runBlocking { TokenManager(appContext).tokenGuardado.first() }.also { tokenEnMemoria = it }

    internal fun actualizarToken(token: String) {
        tokenEnMemoria = token
    }

    /** La API respondió 401: el token ya no sirve. */
    fun notificarSesionExpirada() {
        val habiaSesion = tokenEnMemoria != "" // "" = ya cerrada o visitante; null = aún sin leer
        tokenEnMemoria = ""
        if (!habiaSesion) return
        Notificaciones.desvincular()
        scope.launch { TokenManager(appContext).deletePreferences() }
        _sesionExpirada.tryEmit(Unit)
    }

    /** Cerrar sesión por decisión del usuario: revoca el token en la API (si hay red) y borra la sesión local. */
    suspend fun cerrarSesion() {
        withContext(Dispatchers.IO) {
            runCatching { if (token().isNotEmpty()) RetrofitBuilder.getApiService().cerrarSesion().blockingGet() }
        }
        Notificaciones.desvincular()
        TokenManager(appContext).deletePreferences()
    }
}
