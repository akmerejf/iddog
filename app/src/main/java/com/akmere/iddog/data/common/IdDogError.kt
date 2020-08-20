package com.akmere.iddog.data.common

sealed class IdDogError(val errorMessage: String) {
    data class ConnectionError(val message: String) : IdDogError(message)
    data class ClientError(val message: String) : IdDogError(message)
    data class ApiError(val message: String) : IdDogError(message)
    data class UnknownError(val message: String) : IdDogError(message)
    data class IdDogNotFoundError(val message: String) : IdDogError(message)
    data class UnAuthorizedIdDogError(val message: String) : IdDogError(message)
}