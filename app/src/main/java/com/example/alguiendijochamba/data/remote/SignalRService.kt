package com.example.alguiendijochamba.data.remote

import android.util.Log
import com.example.alguiendijochamba.data.local.SessionManager
import com.example.alguiendijochamba.domain.model.JobRequest
import com.google.gson.Gson // 👈 Asegúrate de importar esto
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SignalRService(private val sessionManager: SessionManager) {

    private var hubConnection: HubConnection? = null
    private val gson = Gson() // Instancia de Gson para conversión manual

    // Flow para emitir eventos a la UI
    private val _jobRequests = MutableSharedFlow<JobRequest>()
    val jobRequests = _jobRequests.asSharedFlow()

    // ⚠️ REVISA TU IP AQUÍ:
    private val HUB_URL = "http://10.0.2.2:5000/hubs/servicerequests"

    fun startConnection() {
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            Log.e("SignalR_DEBUG", "❌ ERROR: No hay token guardado. Imposible conectar.")
            return
        }

        try {
            Log.e("SignalR_DEBUG", "🔄 Intentando conectar a $HUB_URL con token...")

            hubConnection = HubConnectionBuilder.create(HUB_URL)
                .withAccessTokenProvider(Single.just(token))
                .build()

            // -----------------------------------------------------------------------
            // 🚀 CORRECCIÓN CLAVE: Escuchar como 'Object' para evitar fallos de conversión
            // -----------------------------------------------------------------------
            hubConnection?.on("ReceiveNewRequest", { rawData ->
                try {
                    // 1. Ver qué llegó realmente (esto aparecerá en el Logcat sí o sí)
                    Log.e("SignalR_DEBUG", "🔥🔥🔥 RAW DATA LLEGÓ: $rawData")

                    // 2. Convertir manualmente el objeto crudo (LinkedTreeMap) a JSON y luego a JobRequest
                    val jsonString = gson.toJson(rawData)
                    val job = gson.fromJson(jsonString, JobRequest::class.java)

                    Log.e("SignalR_DEBUG", "✅ Conversión Exitosa! ID: ${job.id}, Costo: ${job.totalCost}")

                    // 3. Emitir a la UI
                    CoroutineScope(Dispatchers.IO).launch {
                        _jobRequests.emit(job)
                    }
                } catch (e: Exception) {
                    Log.e("SignalR_DEBUG", "❌ Error al convertir JSON manual: ${e.message}")
                    e.printStackTrace()
                }
            }, Object::class.java) // 👈 IMPORTANTE: Escuchamos Object, no JobRequest

            // --- Listener de Estado ---
            hubConnection?.onClosed {
                Log.e("SignalR_DEBUG", "⚠️ Conexión CERRADA. Error: $it")
            }

            // INICIAR
            hubConnection?.start()?.blockingAwait()
            Log.e("SignalR_DEBUG", "✅ CONECTADO EXITOSAMENTE. Estado: ${hubConnection?.connectionState}")

        } catch (e: Exception) {
            Log.e("SignalR_DEBUG", "❌ CRASH AL CONECTAR: ${e.message}")
            e.printStackTrace()
        }
    }

    fun stopConnection() {
        hubConnection?.stop()
        Log.e("SignalR_DEBUG", "🛑 Desconectado manualmente.")
    }

    fun respondToRequest(jobId: String, accepted: Boolean, proposedCost: Double) {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            try {
                hubConnection?.send("RespondToRequest", jobId, accepted, BigDecimal.valueOf(proposedCost))
                Log.e("SignalR_DEBUG", "📤 Respuesta enviada: $accepted")
            } catch (e: Exception) {
                Log.e("SignalR_DEBUG", "❌ Error al enviar respuesta: ${e.message}")
            }
        } else {
            Log.e("SignalR_DEBUG", "⚠️ No se pudo responder: No conectado.")
        }
    }
}