package com.tatanstudios.astropollococina.extras

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel


// NOTE: Replace the below with your own ONESIGNAL_APP_ID
val ONESIGNAL_APP_ID = "5c22da89-09a8-4b89-ad94-84172cdd14e8"

class SistemaController : Application() {
    override fun onCreate() {
        super.onCreate()

        // sesion de la API (token de Sanctum)
        SesionApi.iniciar(this)

        // logs detallados de OneSignal solo en debug (en release no se llena el logcat)
        if (com.tatanstudios.astropollococina.BuildConfig.DEBUG) {
            OneSignal.Debug.logLevel = LogLevel.VERBOSE
        }

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
    }
}


