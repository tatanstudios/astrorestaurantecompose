package com.tatanstudios.astropollococina.network

import okhttp3.OkHttpClient
import com.tatanstudios.astropollococina.extras.SesionApi
import io.reactivex.rxjava3.functions.Predicate
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    // El servidor depende del tipo de compilacion (ver app/build.gradle.kts):
    // debug -> servidor local, release -> produccion.
    private const val BASE_URL = com.tatanstudios.astropollococina.BuildConfig.BASE_URL
    const val urlImagenes = com.tatanstudios.astropollococina.BuildConfig.URL_IMAGENES

    // Para .retry(RetrofitBuilder.reintentar): reintenta fallos de red, pero no errores 4xx (sin sesion, no autorizado),
    // que se repetirian para siempre.
    val reintentar = Predicate<Throwable> { error -> !(error is HttpException && error.code() in 400..499) }

    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null
   // private var authenticatedApiService: ApiService? = null

    fun getApiService(): ApiService {
        if (apiService == null) {
            val client = buildClientNoAuth()
            retrofit = buildRetrofit(client)
            apiService = createService(ApiService::class.java)
        }
        return apiService!!
    }

   /* fun getAuthenticatedApiService(token: String): ApiService {
        if (authenticatedApiService == null) {
            val client = buildClientWithAuth(token)
            retrofit = buildRetrofit(client)
            authenticatedApiService = createService(ApiService::class.java)
        }
        return authenticatedApiService!!
    }*/

    private fun buildClientNoAuth(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Connection", "close")

                // token de Sanctum (el login funciona igual sin token)
                val token = SesionApi.token()
                if (token.isNotEmpty()) builder.addHeader("Authorization", "Bearer $token")

                val response = chain.proceed(builder.build())
                // token rechazado: se borra la sesion y la app vuelve al login
                if (response.code == 401) SesionApi.notificarSesionExpirada()
                response
            }
            .build()
    }

   /* private fun buildClientWithAuth(token: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .header("Accept", "application/json")
                    .header("Connection", "close")
                    .header("Authorization", "Bearer $token")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }*/

    private fun buildRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(client)
            .build()
    }

    private fun <T> createService(serviceClass: Class<T>): T {
        return retrofit!!.create(serviceClass)
    }
}