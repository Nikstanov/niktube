package com.nikstanov.bytube_backend.service

interface EmailService {
    fun sendEmailWithVerificationCode(email: String, code: String)
}