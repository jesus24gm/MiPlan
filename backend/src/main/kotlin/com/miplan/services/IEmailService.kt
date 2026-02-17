package com.miplan.services

/**
 * Interfaz para servicios de email
 */
interface IEmailService {
    suspend fun sendVerificationEmail(toEmail: String, name: String, token: String)
    suspend fun sendTaskReminderEmail(toEmail: String, name: String, taskTitle: String, dueDate: String)
}
