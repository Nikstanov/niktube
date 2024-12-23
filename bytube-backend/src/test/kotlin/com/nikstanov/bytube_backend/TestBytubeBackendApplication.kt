package com.nikstanov.bytube_backend

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<BytubeBackendApplication>().with(TestcontainersConfiguration::class).run(*args)
}
