package com.nikstanov.bytube_backend.exception

class NotFoundException(type: String, message: String): RuntimeException("$type not found: $message")