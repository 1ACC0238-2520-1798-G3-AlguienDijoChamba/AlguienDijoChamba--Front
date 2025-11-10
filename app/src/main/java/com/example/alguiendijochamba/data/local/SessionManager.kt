package com.example.alguiendijochamba.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("ChambaAppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
    }

    /**
     * Guarda el token de autenticación (JWT).
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Obtiene el token de autenticación guardado.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Borra el token (para cerrar sesión).
     */
    fun clearAuthToken() {
        prefs.edit().remove(USER_TOKEN).apply()
    }
}