package com.tatanstudios.astropollococina.extras

import kotlinx.coroutines.flow.first
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Extension function to create a DataStore instance
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class TokenManager(private val context: Context) {

    // Claves
    private val ID_KEY = stringPreferencesKey("ID")
    // token de Sanctum de la sesion (ver SesionApi)
    private val TOKEN_KEY = stringPreferencesKey("TOKEN_API")

    // Guardar ID en DataStore
    suspend fun saveID(id: String) {
        context.dataStore.edit { preferences ->
            preferences[ID_KEY] = id
        }
    }

    // Guardar el token que entrega la API al iniciar sesion
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
        SesionApi.actualizarToken(token)
    }

    val tokenGuardado: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[TOKEN_KEY] ?: "" }

    // Leer ID como Flow. Sin token no hay sesion: quien venga de una version anterior
    // debe iniciar sesion una vez para obtener su token.
    val idUsuario: Flow<String> = context.dataStore.data
        .map { preferences ->
            if (preferences[TOKEN_KEY].isNullOrEmpty()) "" else preferences[ID_KEY] ?: ""
        }

    // Eliminar ID
    /**
     * Sesion de una version anterior de la app: tiene id guardado pero no token de la API.
     * Si la encuentra, borra ese id (ya no sirve) y devuelve true para avisar una sola vez.
     */
    suspend fun limpiarSesionAnteriorSinToken(): Boolean {
        val prefs = context.dataStore.data.first()
        val sesionVieja = !prefs[ID_KEY].isNullOrEmpty() && prefs[TOKEN_KEY].isNullOrEmpty()
        if (sesionVieja) context.dataStore.edit { it.remove(ID_KEY) }
        return sesionVieja
    }

    suspend fun deletePreferences() {
        context.dataStore.edit { preferences ->
            preferences.remove(ID_KEY)
            preferences.remove(TOKEN_KEY)
        }
        SesionApi.actualizarToken("")
    }

    // Migrar desde SharedPreferences (solo una vez)
    suspend fun migrateFromSharedPreferencesIfNeeded() {
        val sharedPrefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val hasMigrated = sharedPrefs.getBoolean("hasMigratedToDataStore", false)

        if (!hasMigrated) {
            val oldId = sharedPrefs.getString("ID", null)
            if (!oldId.isNullOrEmpty()) {
                saveID(oldId)
            }

            sharedPrefs.edit().putBoolean("hasMigratedToDataStore", true).apply()
        }
    }
}




// UTILIZADO PARA EJECUTAR 1 VEZ LAS PETICIONES RETROFIT
class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}