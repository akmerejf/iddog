package com.akmere.iddog.data.login

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.login.model.User

interface LoginRepositoryContract {
    val user: User?
    val isLoggedIn: Boolean
    suspend fun login(email: String): Result<User>
    fun logout()
}
