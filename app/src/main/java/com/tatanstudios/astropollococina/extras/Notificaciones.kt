package com.tatanstudios.astropollococina.extras

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.content.IntentCompat
import androidx.core.app.NotificationManagerCompat
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val TAG = "AstroPush"

/**
 * Notificaciones push (OneSignal) de la app del restaurante.
 *
 * Cada teléfono se vincula con el usuario que inició sesión usando el External ID
 * "restaurante_{idUsuario}" (OneSignal.login). Cuando un cliente hace un pedido, la API
 * envía la notificación a todos los usuarios activos de ese restaurante por ese mismo id,
 * así que un usuario puede estar en varios teléfonos y al cerrar sesión deja de recibir.
 * El servidor usa el mismo formato (App\Services\OneSignalService::externalIdRestaurante).
 *
 * SONIDO: el canal de OneSignal "Nuevos pedidos" usa res/raw/notipasivo. Android guarda el sonido del
 * canal como NÚMERO de recurso la primera vez que lo crea, así que no se deben agregar ni quitar archivos
 * en res/raw que cambien ese número (por eso se mantiene notialarma.mp3 aunque no se use).
 * Si se quita, notipasivo cambia de número y los teléfonos que ya tienen el canal suenan con el tono normal.
 */
object Notificaciones {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** El aviso "faltan ajustes" se muestra una sola vez cada vez que se abre la app. */
    var avisoAjustesMostrado = false

    /** Lleva prefijo porque OneSignal rechaza en silencio ids genéricos como "1". */
    fun externalId(idUsuario: String) = "restaurante_$idUsuario"

    /** Vincula este teléfono con el usuario y pide permiso si aún no se respondió. */
    fun vincular(idUsuario: String) {
        if (idUsuario.isBlank()) return
        runCatching {
            val id = externalId(idUsuario)
            if (OneSignal.User.externalId != id) OneSignal.login(id)
            Log.i(TAG, "vinculado externalId=$id suscripcion=${OneSignal.User.pushSubscription.id}")
            scope.launch { runCatching { OneSignal.Notifications.requestPermission(false) } }
        }.onFailure { Log.w(TAG, "no se pudo vincular: $it") }
    }

    /** Desvincula el teléfono: deja de recibir avisos de ese restaurante. */
    fun desvincular() {
        runCatching { OneSignal.logout() }.onFailure { Log.w(TAG, "no se pudo desvincular: $it") }
    }

    /** Estado actual para la pantalla de Notificaciones. */
    data class Estado(
        val permiso: Boolean,
        val externalId: String,
        val esperado: String,
        val suscripcionId: String,
        val suscrito: Boolean,
    ) {
        /** El teléfono está vinculado a este usuario. */
        val vinculado: Boolean get() = externalId.isNotBlank() && externalId == esperado
        /** OneSignal ya le dio un id a este teléfono y acepta notificaciones. */
        val registrado: Boolean get() = suscripcionId.isNotBlank() && suscrito
    }

    // ---------- que Android no "duerma" la app aunque pasen dias sin abrirla ----------

    /** Sin restricción de batería (la app puede recibir avisos en segundo plano). */
    fun sinOptimizacionBateria(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Android 11+ pausa las apps que no se abren en meses ("Pausar actividad si no se usa"):
     * les quita permisos y deja de entregarles notificaciones. true = la app está excluida.
     */
    fun sinPausaPorInactividad(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            runCatching { context.packageManager.isAutoRevokeWhitelisted }.getOrDefault(true)
        } else true

    fun abrirPausaPorInactividad(context: Context) {
        val intent = runCatching {
            IntentCompat.createManageUnusedAppRestrictionsIntent(context, context.packageName)
        }.getOrNull() ?: detallesApp(context)
        abrir(context, intent)
    }

    fun pedirSinOptimizacionBateria(context: Context) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            .setData(Uri.parse("package:${context.packageName}"))
        abrir(context, intent)
    }

    /** Marcas que cierran apps en segundo plano por su cuenta (además de lo de Android). */
    fun fabricanteRestrictivo(): String? {
        val m = (Build.MANUFACTURER ?: "").lowercase()
        return when {
            m.contains("xiaomi") || m.contains("redmi") || m.contains("poco") -> "Xiaomi"
            m.contains("huawei") -> "Huawei"
            m.contains("honor") -> "Honor"
            m.contains("oppo") -> "Oppo"
            m.contains("realme") -> "Realme"
            m.contains("oneplus") -> "OnePlus"
            m.contains("vivo") -> "Vivo"
            m.contains("samsung") -> "Samsung"
            m.contains("tcl") || m.contains("alcatel") -> "Alcatel/TCL"
            m.contains("tecno") || m.contains("infinix") || m.contains("itel") -> "Tecno/Infinix"
            else -> null
        }
    }

    /** Abre la pantalla de "inicio automático"/administrador de batería del fabricante, o los detalles de la app. */
    fun abrirAjustesFabricante(context: Context) {
        val candidatos = listOf(
            "com.miui.securitycenter" to "com.miui.permcenter.autostart.AutoStartManagementActivity",
            "com.huawei.systemmanager" to "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
            "com.huawei.systemmanager" to "com.huawei.systemmanager.optimize.process.ProtectActivity",
            "com.hihonor.systemmanager" to "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
            "com.coloros.safecenter" to "com.coloros.safecenter.permission.startup.StartupAppListActivity",
            "com.oplus.safecenter" to "com.oplus.safecenter.permission.startup.StartupAppListActivity",
            "com.vivo.permissionmanager" to "com.vivo.permissionmanager.activity.BgStartUpManagerActivity",
            "com.iqoo.secure" to "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager",
            "com.samsung.android.lool" to "com.samsung.android.sm.battery.ui.BatteryActivity",
            "com.tcl.batterysaver" to "com.tcl.batterysaver.ui.MainActivity",
            "com.transsion.phonemaster" to "com.cyin.himgr.autostart.AutoStartActivity",
        )
        for ((paquete, clase) in candidatos) {
            val intent = Intent().setClassName(paquete, clase).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(context.packageManager) != null && runCatching { context.startActivity(intent) }.isSuccess) return
        }
        abrir(context, detallesApp(context))
    }

    private fun detallesApp(context: Context) =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:${context.packageName}"))

    private fun abrir(context: Context, intent: Intent) {
        runCatching { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            .onFailure { runCatching { context.startActivity(detallesApp(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } }
    }

    /** Lo indispensable para recibir pedidos siempre (lo del fabricante no se puede comprobar). */
    fun faltaAlgo(context: Context): Boolean =
        !NotificationManagerCompat.from(context).areNotificationsEnabled() ||
            !sinOptimizacionBateria(context) || !sinPausaPorInactividad(context)

    fun estado(context: Context, idUsuario: String): Estado {
        val sub = runCatching { OneSignal.User.pushSubscription }.getOrNull()
        return Estado(
            permiso = NotificationManagerCompat.from(context).areNotificationsEnabled(),
            externalId = runCatching { OneSignal.User.externalId }.getOrDefault(""),
            esperado = if (idUsuario.isBlank()) "" else externalId(idUsuario),
            suscripcionId = sub?.id.orEmpty(),
            suscrito = sub?.optedIn == true,
        )
    }
}
