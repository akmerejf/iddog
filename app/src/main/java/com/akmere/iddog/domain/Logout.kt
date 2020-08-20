package com.akmere.iddog.domain

import com.akmere.iddog.data.login.LoginRepositoryContract

class Logout(private val loginRepositoryContract: LoginRepositoryContract) : LogoutCommand{
    // this command / aka interactor / aka usecase encapsulates user logout logic
    // without exposing loginRepositoryContract internals

    override fun execute() {
        loginRepositoryContract.logout()
    }

}