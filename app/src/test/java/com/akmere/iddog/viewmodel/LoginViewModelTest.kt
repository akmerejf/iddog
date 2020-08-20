package com.akmere.iddog.viewmodel

import androidx.lifecycle.Observer
import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.common.IdDogError
import com.akmere.iddog.data.login.LoginRepositoryContract
import com.akmere.iddog.data.login.model.User
import com.akmere.iddog.ui.login.LoggedInUserView
import com.akmere.iddog.ui.login.LoginResult
import com.akmere.iddog.ui.login.LoginViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.inject

@ExperimentalCoroutinesApi
class LoginViewModelTest : BaseTest() {

    @MockK(relaxed = true)
    private lateinit var loginRepository: LoginRepositoryContract

    private var mockEmail: String = "test@test.com"

    private var mockLoginUser = mockk<User>(relaxed = true)

    private val subject: LoginViewModel by inject()

    override fun setup() {
        super.setup()
        loadKoinModules(
            module {
                factory(override = true) { loginRepository }
            }
        )
    }

    @Test
    fun `When successful login then LoginResult should return LoginResult success`() =
        dispatcher.runBlockingTest {
            val observer = spyk<Observer<LoginResult>>()
            subject.loginResult.observeForever(observer)

            val expectedLoginResult = LoginResult(LoggedInUserView(mockEmail), null)

            coEvery { loginRepository.login(mockEmail) } returns Result.Success(
                mockLoginUser
            )
            every { loginRepository.isLoggedIn } answers { false }
            every { mockLoginUser.email } answers { mockEmail }

            subject.login(mockEmail)

            assert(subject.loginResult.value?.error.isNullOrEmpty())

            assertEquals(expectedLoginResult.success, subject.loginResult.value?.success)

            coVerifyOrder {
                loginRepository.isLoggedIn
                loginRepository.login(mockEmail)
                observer.onChanged(expectedLoginResult)
            }
        }


    @Test
    fun `When failed to login then LoginResult should return LoginResult error`() =
        dispatcher.runBlockingTest {
            val observer = spyk<Observer<LoginResult>>()
            subject.loginResult.observeForever(observer)

            val loginError = Result.Error(IdDogError.ApiError("error"))

            coEvery { loginRepository.login(mockEmail) } returns loginError

            every { loginRepository.isLoggedIn } answers { false }
            every { mockLoginUser.email } answers { mockEmail }

            subject.login(mockEmail)

            assert(subject.loginResult.value?.success?.displayName.isNullOrEmpty())

            assertEquals(loginError.exception.errorMessage, subject.loginResult.value?.error)

            coVerifyOrder {
                loginRepository.isLoggedIn
                loginRepository.login(mockEmail)
                observer.onChanged(LoginResult(error = loginError.exception.errorMessage))
            }
        }
}