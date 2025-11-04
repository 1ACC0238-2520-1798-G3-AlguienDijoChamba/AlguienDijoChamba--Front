package com.example.alguiendijochamba.data.di

import com.example.alguiendijochamba.data.local.SessionManager
import com.example.alguiendijochamba.data.remote.ApiService
import com.example.alguiendijochamba.data.remote.AuthInterceptor
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import com.example.alguiendijochamba.domain.usecase.GetReniecInfoUseCase
import com.example.alguiendijochamba.presentation.viewmodel.HomeViewModel
import com.example.alguiendijochamba.presentation.viewmodel.ProfileViewModel
import com.example.alguiendijochamba.presentation.viewmodel.RegisterViewModel
import com.example.alguiendijochamba.presentation.viewmodel.SignInViewModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Este es el módulo principal de Koin
val appModule = module {

    // --- 1. Dependencias de Red (Network) ---

    // Le dice a Koin cómo crear un SessionManager (como singleton)
    single { SessionManager(androidContext()) }

    // Le dice a Koin cómo crear el Interceptor (usa 'get()' para obtener el SessionManager)
    single { AuthInterceptor(get()) }

    // Le dice a Koin cómo crear el OkHttpClient
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>()) // Koin inyecta el interceptor
            .build()
    }

    // Le dice a Koin cómo crear Retrofit
    single {
        val gson = GsonBuilder().serializeNulls().create()

        // ¡¡AQUÍ ESTÁ LA URL DEL BACKEND!!
        val BASE_URL = "http://10.0.2.2:5000/"

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get()) // Koin inyecta el OkHttpClient
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Le dice a Koin cómo crear el ApiService
    single {
        get<Retrofit>().create(ApiService::class.java) // Koin inyecta Retrofit
    }

    // --- 2. Repositorios ---
    // Koin ahora sabe que para crear un UserRepositoryImpl, debe pasarle el ApiService
    single { UserRepositoryImpl(get()) }

    // --- 3. Casos de Uso (Use Cases) ---
    // Koin sabe que para crear GetReniecInfoUseCase, debe pasarle el UserRepositoryImpl
    single { GetReniecInfoUseCase(get()) }

    // --- 4. ViewModels ---
    // Koin se encargará de las Factories automáticamente
    viewModel { HomeViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { RegisterViewModel(get(), get()) } // Pide UserRepository y GetReniecInfoUseCase
    viewModel { SignInViewModel(get(), get()) }   // Pide SessionManager y UserRepository
}