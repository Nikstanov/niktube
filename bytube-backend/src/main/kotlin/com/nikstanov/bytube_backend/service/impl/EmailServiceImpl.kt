package com.nikstanov.bytube_backend.service.impl

import com.nikstanov.bytube_backend.service.EmailService
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service


@Service
class EmailServiceImpl(val mailSender: MailSender): EmailService{
    override fun sendEmailWithVerificationCode(email: String, code: String) {
        val message = SimpleMailMessage()
        message.setTo(email)
        message.subject = "Email Verification"
        message.text = "Your verification code is: $code"
        mailSender.send(message)
    }
}