package com.tatanstudios.astropollococina.network


import com.tatanstudios.astropollococina.model.login.ModeloLogin
import com.tatanstudios.astropollococina.model.ordenes.ModeloCategorias
import com.tatanstudios.astropollococina.model.ordenes.ModeloDatosBasicos
import com.tatanstudios.astropollococina.model.ordenes.ModeloHistorialOrdenes
import com.tatanstudios.astropollococina.model.ordenes.ModeloInfoProducto
import com.tatanstudios.astropollococina.model.ordenes.ModeloListaProductoCategorias
import com.tatanstudios.astropollococina.model.ordenes.ModeloNuevasOrdenes
import com.tatanstudios.astropollococina.model.ordenes.ModeloOrdenesCanceladas
import com.tatanstudios.astropollococina.model.ordenes.ModeloOrdenesCompletadas
import com.tatanstudios.astropollococina.model.ordenes.ModeloProductoHistorialOrdenes
import com.tatanstudios.astropollococina.model.ordenes.ModeloProductoOrdenes
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    // VERIFICACION DE NUMERO
    // CERRAR SESION: revoca el token de este dispositivo
    @POST("restaurante/cerrar-sesion")
    fun cerrarSesion(): Single<ModeloDatosBasicos>


    @POST("restaurante/login")
    @FormUrlEncoded
    fun verificarUsuarioPassword(@Field("usuario") telefono: String,
                          @Field("password") password: String,
                          @Field("idfirebase") idfirebase: String?,
                          // 1 = esta app usa External ID de OneSignal (la API borra el token viejo)
                          @Field("externo") externo: Int = 1
                          ): Single<ModeloLogin>

    // LISTADO DE NUEVAS ORDENES
    @POST("restaurante/nuevas/ordenes")
    @FormUrlEncoded
    fun listadoNuevasOrdenas(@Field("id") id: String,
                             @Field("idfirebase") idfirebase: String?,
                             @Field("externo") externo: Int = 1
    ): Single<ModeloNuevasOrdenes>


    // LISTADO DE PRODUCTOS DE UNA ORDEN
    @POST("restaurante/listado/producto/orden")
    @FormUrlEncoded
    fun listadoProductosOrden(@Field("idorden") idorden: Int
    ): Single<ModeloProductoOrdenes>

    // CANCELAR ORDEN
    @POST("restaurante/cancelar/orden")
    @FormUrlEncoded
    fun cancelarOrden(@Field("idorden") idorden: Int,
                      @Field("mensaje") mensaje: String
    ): Single<ModeloDatosBasicos>


    // INICIAR LA ORDEN
    @POST("restaurante/proceso/orden/iniciar-orden")
    @FormUrlEncoded
    fun iniciarOrden(@Field("idorden") idorden: Int
    ): Single<ModeloDatosBasicos>


    // INFORMACION DE UN PRODUCTOS SELECCIONADO
    @POST("restaurante/listado/productos/ordenes-individual")
    @FormUrlEncoded
    fun infoProductoIndividual(@Field("idordendescrip") idordendescrip: Int
    ): Single<ModeloInfoProducto>


    // LISTADO DE ORDENES INICIADAS HOY
    @POST("restaurante/completadashoy/ordenes")
    @FormUrlEncoded
    fun listadoOrdenesCompletadas(@Field("id") id: String
    ): Single<ModeloOrdenesCompletadas>


    // LISTADO DE ORDENES CANCELADAS HOY
    @POST("restaurante/canceladashoy/ordenes")
    @FormUrlEncoded
    fun listadoOrdenesCanceladas(@Field("id") id: String
    ): Single<ModeloOrdenesCanceladas>


    // LISTADO DE CATEGORIAS
    @POST("restaurante/listado/categorias")
    @FormUrlEncoded
    fun listadoCategorias(@Field("id") id: String
    ): Single<ModeloCategorias>


    // ACTUALIZAR CATEGORIA
    @POST("restaurante/actualizar/estado/categorias")
    @FormUrlEncoded
    fun actualizarCategoria(@Field("idcategoria") idcategoria: Int,
                          @Field("checkvalor") checkvalor: Int
    ): Single<ModeloDatosBasicos>



    // LISTADO DE PRODUCTOS POR CATEGORIA
    @POST("restaurante/categoria/listado/productos")
    @FormUrlEncoded
    fun listadoProductosCategoria(@Field("idcategoria") idcategoria: Int
    ): Single<ModeloListaProductoCategorias>


    // ACTUALIZAR ESTADO DE PRODUCTO
    @POST("restaurante/actualizar/estado/producto")
    @FormUrlEncoded
    fun actualizarEstadoProducto(@Field("idproducto") idproducto: Int,
                                 @Field("checkvalor") checkvalor: Int,
    ): Single<ModeloDatosBasicos>


    // HISTORIAL LISTADO DE ORDENES
    @POST("restaurante/historial/ordenes")
    @FormUrlEncoded
    fun listadoHistorialOrdenes(@Field("id") id: String,
                                 @Field("fecha1") fecha1: String,
                                 @Field("fecha2") fecha2: String,
    ): Single<ModeloHistorialOrdenes>



    // LISTADO DE PRODUCTOS HISTORIAL ORDEN
    @POST("restaurante/listado/producto/orden")
    @FormUrlEncoded
    fun listadoProductosHistorialOrden(@Field("idorden") idorden: Int
    ): Single<ModeloProductoHistorialOrdenes>


    // ENVIARME UNA NOTIFICACION
    @POST("restaurante/notificacion/modo/prueba")
    @FormUrlEncoded
    fun notificacionPrueba(@Field("id") id: String,
                           @Field("externo") externo: Int = 1
    ): Single<ModeloDatosBasicos>



}


