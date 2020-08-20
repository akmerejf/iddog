package com.akmere.iddog.viewmodel

import com.akmere.iddog.data.login.LoginRepositoryContract
import com.akmere.iddog.domain.LogoutCommand
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.inject

@ExperimentalCoroutinesApi
class LogoutCommandTest : BaseTest() {

    @MockK
    private lateinit var loginRepository: LoginRepositoryContract

    private val subject: LogoutCommand by inject()

    override fun setup() {
        super.setup()
        loadKoinModules(module {
            factory(override = true) {
                loginRepository
            }
        })
    }

    @Test
    fun `When execute logout then should call login repository logout`() {

        every { loginRepository.logout() } returns Unit

        subject.execute()

        verify(exactly = 1) { loginRepository.logout() }

        coVerify(exactly = 0) {
            loginRepository.login("test@test.com")
        }
    }
}