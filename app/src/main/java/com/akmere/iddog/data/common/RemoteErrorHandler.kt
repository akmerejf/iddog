package com.akmere.iddog.data.common

import android.accounts.NetworkErrorException
import android.util.Log
import retrofit2.HttpException
import java.lang.Exception

class RemoteErrorHandler {

    private val connectionErrorMessage = "Sem conexão."
    private val apiErrorMessage = "Falha ao processar requisição."
    private val clientErrorMessage = "Dados inválidos."
    private val unknownErrorMessage = "Erro desconhecido, tente novamente."
    private val unAuthorizedErrorMessage = "Necessário fazer login."

    fun handleRequestErrors(error: Exception): IdDogError {
        Log.d("error message", error.message ?: error.localizedMessage)
        return when (error) {
            is HttpException -> handleHttpError(error)
            is NetworkErrorException -> IdDogError.ConnectionError(connectionErrorMessage)
            else -> IdDogError.UnknownError(unknownErrorMessage)
        }
    }

    private fun handleHttpError(error: HttpException): IdDogError {
        return when (error.code()) {
            401 -> IdDogError.UnAuthorizedIdDogError(unAuthorizedErrorMessage)
            in 400..499 -> IdDogError.ClientError(clientErrorMessage)
            else -> IdDogError.ApiError(apiErrorMessage)
        }
    }
}