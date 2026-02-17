package com.miplan.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * Servicio para envío de emails usando Mailtrap API
 */
class MailtrapEmailService(config: ApplicationConfig) : IEmailService {
    
    private val apiToken = config.propertyOrNull("mailtrap.api_token")?.getString()
        ?: config.propertyOrNull("email.mailtrap_api_token")?.getString() 
        ?: config.property("email.password").getString()
    private val from = config.propertyOrNull("email.from")?.getString()
        ?: "MiPlan <noreply@miplan.com>"
    private val baseUrl = config.property("email.baseUrl").getString()
    
    private val client = HttpClient(CIO)
    private val json = Json { ignoreUnknownKeys = true }
    
    @Serializable
    data class MailtrapEmailRequest(
        val from: MailtrapAddress,
        val to: List<MailtrapAddress>,
        val subject: String,
        val html: String
    )
    
    @Serializable
    data class MailtrapAddress(
        val email: String,
        val name: String? = null
    )
    
    /**
     * Envía un email de verificación
     */
    override suspend fun sendVerificationEmail(toEmail: String, name: String, token: String) {
        try {
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #2196F3; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 8px 8px; }
                        .button { display: inline-block; padding: 12px 30px; background: #2196F3; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎯 MiPlan</h1>
                        </div>
                        <div class="content">
                            <h2>¡Hola $name!</h2>
                            <p>Gracias por registrarte en MiPlan.</p>
                            <p>Para verificar tu cuenta, haz clic en el siguiente botón:</p>
                            <div style="text-align: center;">
                                <a href="$baseUrl/api/auth/verify/$token" class="button">Verificar mi cuenta</a>
                            </div>
                            <p>O copia y pega este enlace en tu navegador:</p>
                            <p style="word-break: break-all; color: #2196F3;">$baseUrl/api/auth/verify/$token</p>
                            <p>Si no creaste esta cuenta, puedes ignorar este mensaje.</p>
                            <p>Saludos,<br>El equipo de MiPlan</p>
                        </div>
                        <div class="footer">
                            <p>Este es un email automático, por favor no respondas.</p>
                        </div>
                    </div>
                </body>
                </html>
            """.trimIndent()
            
            // Extraer email y nombre del campo "from"
            val fromEmail = extractEmail(from)
            val fromName = extractName(from)
            
            val request = MailtrapEmailRequest(
                from = MailtrapAddress(email = fromEmail, name = fromName),
                to = listOf(MailtrapAddress(email = toEmail, name = name)),
                subject = "Verifica tu cuenta en MiPlan",
                html = htmlContent
            )
            
            withContext(Dispatchers.IO) {
                val response = client.post("https://send.api.mailtrap.io/api/send") {
                    header("Authorization", "Bearer $apiToken")
                    header("Content-Type", "application/json")
                    setBody(json.encodeToString(request))
                }
                
                if (response.status.isSuccess()) {
                    println("✅ Email de verificación enviado a: $toEmail")
                } else {
                    val errorBody = try { response.bodyAsText() } catch (e: Exception) { "No response body" }
                    println("❌ Error al enviar email: ${response.status} - $errorBody")
                }
            }
        } catch (e: Exception) {
            println("❌ Error al enviar email: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Envía un email de recordatorio de tarea
     */
    override suspend fun sendTaskReminderEmail(toEmail: String, name: String, taskTitle: String, dueDate: String) {
        try {
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #FF9800; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 8px 8px; }
                        .task-box { background: white; padding: 15px; border-left: 4px solid #FF9800; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>⏰ Recordatorio de Tarea</h1>
                        </div>
                        <div class="content">
                            <h2>¡Hola $name!</h2>
                            <p>Te recordamos que tienes una tarea pendiente:</p>
                            <div class="task-box">
                                <h3>$taskTitle</h3>
                                <p><strong>Fecha límite:</strong> $dueDate</p>
                            </div>
                            <p>¡No olvides completarla!</p>
                            <p>Saludos,<br>El equipo de MiPlan</p>
                        </div>
                        <div class="footer">
                            <p>Este es un email automático, por favor no respondas.</p>
                        </div>
                    </div>
                </body>
                </html>
            """.trimIndent()
            
            val fromEmail = extractEmail(from)
            val fromName = extractName(from)
            
            val request = MailtrapEmailRequest(
                from = MailtrapAddress(email = fromEmail, name = fromName),
                to = listOf(MailtrapAddress(email = toEmail, name = name)),
                subject = "Recordatorio: $taskTitle",
                html = htmlContent
            )
            
            withContext(Dispatchers.IO) {
                val response = client.post("https://send.api.mailtrap.io/api/send") {
                    header("Authorization", "Bearer $apiToken")
                    header("Content-Type", "application/json")
                    setBody(json.encodeToString(request))
                }
                
                if (response.status.isSuccess()) {
                    println("✅ Email de recordatorio enviado a: $toEmail")
                } else {
                    val errorBody = try { response.bodyAsText() } catch (e: Exception) { "No response body" }
                    println("❌ Error al enviar email: ${response.status} - $errorBody")
                }
            }
        } catch (e: Exception) {
            println("❌ Error al enviar email: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun extractEmail(fromField: String): String {
        // Extraer email de "Name <email@domain.com>" o devolver el campo completo
        val regex = "<(.+?)>".toRegex()
        return regex.find(fromField)?.groupValues?.get(1) ?: fromField
    }
    
    private fun extractName(fromField: String): String? {
        // Extraer nombre de "Name <email@domain.com>"
        val regex = "(.+?)\\s*<".toRegex()
        return regex.find(fromField)?.groupValues?.get(1)?.trim()
    }
    
    fun close() {
        client.close()
    }
}
