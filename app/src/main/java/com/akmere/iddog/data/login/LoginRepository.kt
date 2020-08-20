package com.akmere.iddog.data.login

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.login.model.User

/**
 * Class that requests authentication and user information from the local/remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    private val local: LoginDataSourceContract.Local,
    private val remote: LoginDataSourceContract.Remote
) : LoginRepositoryContract {

    // in-memory cache of the loggedInUser object
    private var _user: User? = null
    override val user: User?
        get() = _user

    override val isLoggedIn: Boolean
        get() = user != null

    init {
        val userResult = local.loadUser()
        _user = if (userResult is Result.Success) {
            userResult.data
        } else {
            null
        }
    }

    override fun logout() {
        local.deleteUser()
        _user = null
    }

    override suspend fun login(email: String): Result<User> {
        // handle login
        val localUser = local.loadUser()

        return if (localUser is Result.Success) {
            localUser
        } else {
            val remoteUser = remote.fetchUser(email)
            if (remoteUser is Result.Success) {
                local.update(remoteUser.data)
                _user = remoteUser.data
            }

            remoteUser
        }


    }
}