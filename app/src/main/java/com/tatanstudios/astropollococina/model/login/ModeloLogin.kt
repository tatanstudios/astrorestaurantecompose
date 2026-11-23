package com.tatanstudios.astropollococina.model.login

import com.google.gson.annotations.SerializedName

data class ModeloLogin(
    @SerializedName("success")
    val success: Int,

    @SerializedName("id")
    val id: Int,

    @SerializedName("mensaje")
    val mensaje: String? = null,

    // token de Sanctum para las demas peticiones
    @SerializedName("token")
    val token: String? = null
)
