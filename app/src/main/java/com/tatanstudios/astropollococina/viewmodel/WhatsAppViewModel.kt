package com.tatanstudios.astropollococina.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tatanstudios.astropollococina.extras.Event
import com.tatanstudios.astropollococina.model.ordenes.ModeloWhatsApp
import com.tatanstudios.astropollococina.network.RetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

/** Seccion WhatsApp: carga el numero y el saldo, y guarda un numero nuevo. */
class WhatsAppViewModel : CargaViewModel() {

    private val _info = MutableLiveData<ModeloWhatsApp?>(null)
    val info: LiveData<ModeloWhatsApp?> get() = _info

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _guardando = MutableLiveData(false)
    val guardando: LiveData<Boolean> get() = _guardando

    // resultado de guardar (una vez): el modelo, o null si fallo la conexion
    private val _resultadoGuardar = MutableLiveData<Event<ModeloWhatsApp?>>()
    val resultadoGuardar: LiveData<Event<ModeloWhatsApp?>> get() = _resultadoGuardar

    private val disposables = CompositeDisposable()

    fun cargar(idusuario: String) {
        if (_isLoading.value == true) return
        prepararCarga { cargar(idusuario) }
        _isLoading.value = true

        disposables.add(
            RetrofitBuilder.getApiService().informacionWhatsApp(idusuario)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(2, RetrofitBuilder.reintentar)
                .subscribe(
                    { response ->
                        _isLoading.value = false
                        if (response.success == 1) _info.value = response else marcarError()
                    },
                    {
                        _isLoading.value = false
                        marcarError()
                    }
                )
        )
    }

    fun guardar(idusuario: String, numero: String) {
        if (_guardando.value == true) return
        _guardando.value = true

        disposables.add(
            RetrofitBuilder.getApiService().actualizarWhatsApp(idusuario, numero.trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        _guardando.value = false
                        if (response.success == 1) _info.value = response
                        _resultadoGuardar.value = Event(response)
                    },
                    {
                        _guardando.value = false
                        _resultadoGuardar.value = Event(null)
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
