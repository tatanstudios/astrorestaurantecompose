package com.tatanstudios.astropollococina.extras

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.Keep
import com.onesignal.notifications.INotificationReceivedEvent
import com.onesignal.notifications.INotificationServiceExtension
import com.tatanstudios.astropollococina.R

private const val TAG = "AstroPush"

/**
 * Se ejecuta cada vez que llega una notificación de OneSignal, aunque la app esté cerrada
 * (registrado en AndroidManifest con "com.onesignal.NotificationServiceExtension").
 *
 * Algunos teléfonos (ej. Alcatel/TCL) no reproducen el sonido propio del canal y ponen el tono
 * normal. Por eso la app silencia esta notificación y reproduce ella misma res/raw/notipasivo,
 * así suena igual en cualquier marca. Respeta el modo silencio y vibración del teléfono.
 */
@Keep
class NotificacionServicio : INotificationServiceExtension {
    override fun onNotificationReceived(event: INotificationReceivedEvent) {
        // al reiniciar la app OneSignal vuelve a mostrar las pendientes: esas no suenan otra vez
        if (event.restoring) return

        // sin sonido ni vibración del canal (aunque el canal los tenga): los pone la app
        event.notification.setExtender { builder -> builder.setSilent(true) }
        SonidoNotificacion.reproducir(event.context)
    }
}

object SonidoNotificacion {

    @Volatile
    private var reproductor: MediaPlayer? = null

    fun reproducir(context: Context) {
        val ctx = context.applicationContext
        val audio = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (audio.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> return
            AudioManager.RINGER_MODE_VIBRATE -> { vibrar(ctx); return }
        }
        vibrar(ctx)
        runCatching {
            // si llegan varias seguidas, no se enciman
            reproductor?.let { runCatching { it.stop(); it.release() } }
            val afd = ctx.resources.openRawResourceFd(R.raw.notipasivo)
            val mp = MediaPlayer()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION) // usa el volumen de notificaciones
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.setOnCompletionListener {
                it.release()
                if (reproductor === it) reproductor = null
            }
            mp.prepare()
            mp.start()
            reproductor = mp
        }.onFailure { Log.w(TAG, "no se pudo reproducir el sonido: $it") }
    }

    private fun vibrar(ctx: Context) {
        runCatching {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1))
        }
    }
}
