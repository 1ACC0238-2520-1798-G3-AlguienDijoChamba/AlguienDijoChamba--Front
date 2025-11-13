package com.example.alguiendijochamba.data.di

import com.example.alguiendijochamba.data.local.SessionManager
import com.example.alguiendijochamba.data.remote.ApiService
import com.example.alguiendijochamba.data.remote.AuthInterceptor
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import com.example.alguiendijochamba.domain.usecase.GetReniecInfoUseCase
import com.example.alguiendijochamba.presentation.viewmodel.CompleteProfileViewModel
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
import com.example.alguiendijochamba.data.remote.SignalRService

// Este es el módulo principal de Koin
val appModule = module {

    // --- 1. Dependencias de Red (Network) ---

    // El SessionManager DEBE ser 'single' para que solo haya una instancia
    single { SessionManager(androidContext()) }
    single { SignalRService(get()) }
    single { AuthInterceptor(get()) }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .build()
    }
    single {
        val gson = GsonBuilder().serializeNulls().create()
        val BASE_URL   = "http://10.0.2.2:5000/"

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    single<ApiService> {
        get<Retrofit>().create(ApiService::class.java)
    }

    // --- 2. Repositorios ---
    // Repositorio necesita ApiService y Context
    single { UserRepositoryImpl(get(), androidContext()) }

    // --- 3. Casos de Uso (Use Cases) ---
    single { GetReniecInfoUseCase(get()) }

    // --- 4. ViewModels ---
    viewModel {
        HomeViewModel(
            repository = get<UserRepositoryImpl>(),
            signalRService = get<SignalRService>()
        )
    }
    viewModel { ProfileViewModel(get()) }

    // ¡CORRECCIÓN! Añadimos el tercer 'get()' para SessionManager
    viewModel { RegisterViewModel(get(), get(), get()) }

    viewModel { SignInViewModel(get(), get()) }
    viewModel { CompleteProfileViewModel(get()) }
}