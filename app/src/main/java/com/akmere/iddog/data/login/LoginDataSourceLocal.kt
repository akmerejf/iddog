package com.akmere.iddog.data.login

import android.content.SharedPreferences
import android.util.Log
import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.common.IdDogError
import com.akmere.iddog.data.login.model.User
import com.google.gson.Gson

class LoginDataSourceLocal(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : LoginDataSourceContract.Local {
    private val userKey = "USER"

    private val userNotFoundErrorMessage = "Usuário não encontrado"
    private val unknownErrorMessage = "Tivemos um problema, tente novamente."

    override fun loadUser(): Result<User> {
        return try {
            val userJson = sharedPreferences.getString(userKey, null)
            Result.Success(gson.fromJson(userJson, User::class.java))
        } catch (error: Exception) {
            Log.d("error message", error.message ?: error.localizedMessage)
            Result.Error(handleLoadUserError(error))
        }
    }

    private fun handleLoadUserError(exception: Exception): IdDogError {
        return when (exception) {
            is NullPointerException -> IdDogError.IdDogNotFoundError(userNotFoundErrorMessage)
            else -> IdDogError.UnknownError(unknownErrorMessage)
        }
    }

    override fun deleteUser() {
        sharedPreferences.edit().putString(userKey, null).apply()
    }

    override fun update(user: User) {
        val userJson = gson.toJson(user, User::class.java)
        sharedPreferences.edit().putString(userKey, userJson).apply()
    }
}