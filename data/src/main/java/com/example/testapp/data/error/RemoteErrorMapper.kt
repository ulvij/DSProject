package com.example.testapp.data.error

import com.example.testapp.data.remote.error.ErrorResponse
import com.example.testapp.domain.exception.ErrorMapper
import com.example.testapp.domain.exception.NetworkError
import com.example.testapp.domain.exception.ServerError
import com.example.testapp.domain.exception.UnknownError
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class RemoteErrors(val code: String) {
    UNEXPECTED_ERROR("error.unexpected")
}

class RemoteErrorMapper(private val moshi: Moshi) : ErrorMapper {

    override fun mapError(e: Throwable): Throwable = when (e) {
        is HttpException -> mapHttpErrors(e)
        is SocketException,
        is SocketTimeoutException,
        is UnknownHostException,
        -> NetworkError(e)
        else -> UnknownError(e)
    }

    private fun mapHttpErrors(error: HttpException): Throwable {
        val description = try {
            error
                .response()
                ?.errorBody()
                ?.string()
                ?.let { moshi.adapter(ErrorResponse::class.java).fromJson(it) }

        } catch (ex: Throwable) {
            null
        } ?: ErrorResponse()

        return when (error.code()) {
            404 -> ServerError.NotFound(description.serverCode, description.errorMessage())
            in 500..600 -> ServerError.ServerIsDown(
                description.serverCode,
                description.errorMessage()
            )
            else -> {
                when (description.serverCode) {
                    RemoteErrors.UNEXPECTED_ERROR.code ->
                        ServerError.Unexpected(
                            description.serverCode,
                            description.errorMessage()
                        )
                    else -> ServerError.Unexpected(
                        description.serverCode,
                        description.errorMessage()
                    )
                }
            }
        }
    }
}