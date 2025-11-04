package com.example.alguiendijochamba

import android.app.Application
import com.example.alguiendijochamba.data.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ChambaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Logger de Koin (muy útil para depurar)
            androidLogger()
            // Proveer el contexto de Android a Koin
            androidContext(this@ChambaApp)
            // Cargar nuestros módulos
            modules(appModule)
        }
    }
}