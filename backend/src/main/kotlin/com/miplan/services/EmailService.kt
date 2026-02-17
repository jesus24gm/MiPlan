package com.miplan.services

import io.ktor.server.config.*
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail

/**
 * Servicio para envío de emails usando SMTP
 */
class EmailService(config: ApplicationConfig) : IEmailService {
    
    private val host = config.property("email.host").getString()
    private val port = config.property("email.port").getString().toInt()
    private val username = config.property("email.username").getString()
    private val password = config.property("email.password").getString()
    private val from = config.property("email.from").getString()
    private val baseUrl = config.property("email.baseUrl").getString()
    
    /**
     * Envía un email de verificación
     */
    override suspend fun sendVerificationEmail(toEmail: String, name: String, token: String) {
        try {
            val email = SimpleEmail()
            email.hostName = host
            email.setSmtpPort(port)
            email.setAuthenticator(DefaultAuthenticator(username, password))
            email.isSSLOnConnect = false
            email.isStartTLSEnabled = true
            email.setFrom(from, "MiPlan")
            email.subject = "Verifica tu cuenta en MiPlan"
            email.setMsg("""
                Hola $name,
                
                Gracias por registrarte en MiPlan.
                
                Para verificar tu cuenta, haz clic en el siguiente enlace:
                $baseUrl/api/auth/verify/$token
                
                Si no creaste esta cuenta, puedes ignorar este mensaje.
                
                Saludos,
                El equipo de MiPlan
            """.trimIndent())
            email.addTo(toEmail)
            
            email.send()
            println("Email de verificación enviado a: $toEmail")
        } catch (e: Exception) {
            println("Error al enviar email: ${e.message}")
            // En desarrollo, no fallar si no se puede enviar email
        }
    }
    
    /**
     * Envía un email de recordatorio de tarea
     */
    override suspend fun sendTaskReminderEmail(toEmail: String, name: String, taskTitle: String, dueDate: String) {
        try {
            val email = SimpleEmail()
            email.hostName = host
            email.setSmtpPort(port)
            email.setAuthenticator(DefaultAuthenticator(username, password))
            email.isSSLOnConnect = false
            email.isStartTLSEnabled = true
            email.setFrom(from, "MiPlan")
            email.subject = "Recordatorio: $taskTitle"
            email.setMsg("""
                Hola $name,
                
                Te recordamos que tienes una tarea pendiente:
                
                Tarea: $taskTitle
                Fecha límite: $dueDate
                
                ¡No olvides completarla!
                
                Saludos,
                El equipo de MiPlan
            """.trimIndent())
            email.addTo(toEmail)
            
            email.send()
            println("Email de recordatorio enviado a: $toEmail")
        } catch (e: Exception) {
            println("Error al enviar email: ${e.message}")
        }
    }
}
