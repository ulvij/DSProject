package com.example.testapp.domain.exception

fun interface ErrorConverter {
    fun convert(t: Throwable): Throwable
}