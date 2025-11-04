package com.example.alguiendijochamba.data.di

import android.content.Context
import com.example.alguiendijochamba.data.local.SessionManager
import com.example.alguiendijochamba.data.remote.AuthInterceptor
import com.example.alguiendijochamba.data.remote.ApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    const val BASE_URL = "http://10.0.2.2:5000/"

    // Esta función crea todas las dependencias de red.
    fun provideApiService(context: Context): ApiService {

        // 1. Crea el SessionManager para leer el token.
        val sessionManager = SessionManager(context.applicationContext)

        // 2. Crea el Interceptor que añade el token a las cabeceras.
        val authInterceptor = AuthInterceptor(sessionManager)

        // 3. Construye el cliente HTTP que usará el Interceptor.
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        // 4. Configurar Gson explícitamente
        val gson = GsonBuilder()
            .serializeNulls() // Serializar campos null como null
            .create()

        // 5. Construye Retrofit usando el cliente seguro.
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        // 6. Crea y devuelve la implementación de ApiService.
        return retrofit.create(ApiService::class.java)
    }
}

