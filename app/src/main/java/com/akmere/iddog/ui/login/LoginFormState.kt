package com.akmere.iddog.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val emailError: Int? = null,
    val isDataValid: Boolean = false
)