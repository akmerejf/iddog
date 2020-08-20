package com.akmere.iddog.data.login

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.common.RemoteErrorHandler
import com.akmere.iddog.data.login.model.User
import com.akmere.iddog.data.login.model.UserRequest

class LoginDataSourceRemote(
    private val loginService: LoginService,
    private val remoteErrorHandler: RemoteErrorHandler
) :
    LoginDataSourceContract.Remote {

    override suspend fun fetchUser(email: String): Result<User> {
        return try {
            val userRequest = UserRequest(email)
            Result.Success(loginService.signUp(userRequest).user)
        } catch (error: Exception) {
            Result.Error(remoteErrorHandler.handleRequestErrors(error))
        }
    }
}