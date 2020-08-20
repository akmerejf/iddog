package com.akmere.iddog.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.akmere.iddog.data.common.Result

import com.akmere.iddog.R
import com.akmere.iddog.data.login.LoginRepositoryContract
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepositoryContract) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    init {
        if (repository.isLoggedIn)
            _loginResult.value =
                LoginResult(
                    success = LoggedInUserView(
                        displayName = repository.user?.email ?: ""
                    )
                )
    }

    fun login(email: String) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            val result = repository.login(email)
            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = result.data.email))
            } else if (result is Result.Error) {
                _loginResult.value = LoginResult(error = result.exception.errorMessage)
            }
        }
    }

    fun loginDataChanged(email: String) {
        if (!isUserNameValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_username)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }
}