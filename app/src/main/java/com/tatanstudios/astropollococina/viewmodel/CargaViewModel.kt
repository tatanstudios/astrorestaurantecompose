package com.tatanstudios.astropollococina.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Base para los ViewModels que cargan datos de una pantalla.
 * Si la petición falla (sin internet o error del servidor) [error] pasa a true
 * y la pantalla muestra el panel "Reintentar", que repite la última petición.
 */
abstract class CargaViewModel : ViewModel() {
    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    private var ultimaPeticion: (() -> Unit)? = null

    /** Guarda la petición para poder repetirla y limpia el error anterior. */
    protected fun prepararCarga(peticion: () -> Unit) {
        ultimaPeticion = peticion
        _error.value = false
    }

    protected fun marcarError() {
        _error.value = true
    }

    fun reintentar() {
        ultimaPeticion?.invoke()
    }
}
