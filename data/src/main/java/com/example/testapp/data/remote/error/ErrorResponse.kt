package com.example.testapp.data.remote.error


data class ErrorResponse(
    val serverCode: String = "",
    val serverMessage: String = "",
    val errors: List<String> = emptyList()
) {
    fun errorMessage(): String {
        if (serverMessage.isNotEmpty()) return serverMessage

        return errors.joinToString(",")
    }
}
