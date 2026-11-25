package com.tatanstudios.astropollococina.model.ordenes

import com.google.gson.annotations.SerializedName

/**
 * Seccion WhatsApp: numero de la sucursal que recibe el aviso de cada orden nueva
 * y mensajes disponibles en el bolson.
 * success: 1 = ok, 2 = numero invalido (viene [mensaje]), 0 = error
 */
data class ModeloWhatsApp(
    @SerializedName("success") val success: Int,
    @SerializedName("whatsapp") val whatsapp: String?,
    @SerializedName("disponibles") val disponibles: Int?,
    @SerializedName("mensaje") val mensaje: String?,
)
