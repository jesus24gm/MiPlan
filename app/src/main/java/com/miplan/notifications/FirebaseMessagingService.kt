package com.miplan.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.miplan.MainActivity
import com.miplan.R
/**
 * Servicio para manejar notificaciones push de Firebase
 */
class MiPlanFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "miplan_notifications"
        private const val CHANNEL_NAME = "MiPlan Notificaciones"
        private const val CHANNEL_DESCRIPTION = "Notificaciones de tareas y colaboración"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Se llama cuando llega un nuevo token de FCM
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Enviar token al servidor para poder enviar notificaciones push
        sendTokenToServer(token)
    }

    /**
     * Se llama cuando llega una notificación push
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Extraer datos de la notificación
        val title = message.notification?.title ?: message.data["title"] ?: "MiPlan"
        val body = message.notification?.body ?: message.data["message"] ?: ""
        val taskId = message.data["taskId"]?.toIntOrNull()
        val type = message.data["type"] ?: "general"

        // Mostrar notificación
        showNotification(title, body, taskId, type)
    }

    /**
     * Muestra una notificación local
     */
    private fun showNotification(title: String, message: String, taskId: Int?, type: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            taskId?.let { putExtra("taskId", it) }
            putExtra("notificationType", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            taskId ?: System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Usar icono de notificación genérico
        // Los iconos específicos se pueden agregar después si es necesario

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(taskId ?: System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    /**
     * Crea el canal de notificaciones (Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Envía el token FCM al servidor
     */
    private fun sendTokenToServer(token: String) {
        // Guardar token localmente
        val prefs = getSharedPreferences("miplan_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()

        // El token se enviará al backend automáticamente después del login
        // a través del AuthViewModel.updateFcmToken()
        // Aquí solo lo guardamos localmente para uso posterior
    }
}
