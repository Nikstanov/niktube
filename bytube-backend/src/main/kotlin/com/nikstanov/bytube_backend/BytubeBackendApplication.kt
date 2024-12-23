package com.nikstanov.bytube_backend

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
@OpenAPIDefinition
class BytubeBackendApplication

fun main(args: Array<String>) {
	runApplication<BytubeBackendApplication>(*args)
}
