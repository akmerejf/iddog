package com.akmere.iddog.data.login

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.login.model.User

interface LoginDataSourceContract {

    interface Local{
        fun loadUser(): Result<User>
        fun update(user: User)
        fun deleteUser()
    }
    interface Remote{
       suspend fun fetchUser(email: String): Result<User>
    }
}