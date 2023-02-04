package com.example.testapp.domain.exception

fun interface ErrorMapper {
    fun mapError(e: Throwable): Throwable
}