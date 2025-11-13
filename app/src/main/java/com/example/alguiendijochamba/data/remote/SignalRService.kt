package com.example.alguiendijochamba.data.remote

import android.util.Log
import com.example.alguiendijochamba.data.local.SessionManager
import com.example.alguiendijochamba.domain.model.JobRequest
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

    // Usamos SharedFlow para emitir eventos a la UI (ViewModel)
    private val _jobRequests = MutableSharedFlow<JobRequest>()
    val jobRequests = _jobRequests.asSharedFlow()

    // URL del Hub (10.0.2.2 para emulador, IP real para dispositivo físico)
    private val HUB_URL = "http://10.0.2.2:5000/hubs/servicerequests"

    fun startConnection() {
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            Log.e("SignalR", "No hay token, no se puede conectar.")
            return
        }

        // Configurar la conexión
        hubConnection = HubConnectionBuilder.create(HUB_URL)
            .withAccessTokenProvider(Single.just(token)) // Envía el token en el QueryString
            .build()

        // --- ESCUCHAR EVENTOS DEL BACKEND ---

        // Escuchar "ReceiveNewRequest" (viene del CreateJobRequestCommandHandler)
        hubConnection?.on("ReceiveNewRequest", { job: JobRequest ->
            Log.d("SignalR", "Nueva solicitud recibida: ${job.id}")
            CoroutineScope(Dispatchers.IO).launch {
                _jobRequests.emit(job)
            }
        }, JobRequest::class.java)

        // Iniciar conexión
        try {
            hubConnection?.start()?.blockingAwait()
            Log.d("SignalR", "Conectado exitosamente. Estado: ${hubConnection?.connectionState}")
        } catch (e: Exception) {
            Log.e("SignalR", "Error al conectar: ${e.message}")
        }
    }

    fun stopConnection() {
        hubConnection?.stop()
    }

    // --- ENVIAR RESPUESTA AL BACKEND ---
    // Llama al método 'RespondToRequest' definido en tu ServiceRequestHub.cs
    fun respondToRequest(jobId: String, accepted: Boolean, proposedCost: Double) {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            try {
                // El backend espera: Guid jobId, bool accepted, decimal proposedCost
                // Convertimos Double a BigDecimal para el decimal de C#
                hubConnection?.send("RespondToRequest", jobId, accepted, BigDecimal.valueOf(proposedCost))
                Log.d("SignalR", "Respuesta enviada: Accepted=$accepted, Cost=$proposedCost")
            } catch (e: Exception) {
                Log.e("SignalR", "Error enviando respuesta: ${e.message}")
            }
        } else {
            Log.e("SignalR", "No conectado. No se pudo enviar respuesta.")
        }
    }
}