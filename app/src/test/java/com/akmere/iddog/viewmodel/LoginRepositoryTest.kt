package com.akmere.iddog.viewmodel

import com.akmere.iddog.data.common.Result
import com.akmere.iddog.data.common.IdDogError
import com.akmere.iddog.data.login.LoginDataSourceContract
import com.akmere.iddog.data.login.LoginRepositoryContract
import com.akmere.iddog.data.login.model.User
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.inject

@ExperimentalCoroutinesApi
class LoginRepositoryTest : BaseTest() {

    @MockK(relaxed = true)
    private lateinit var loginDataSourceRemote: LoginDataSourceContract.Remote

    @MockK(relaxed = true)
    private lateinit var loginDataSourceLocal: LoginDataSourceContract.Local

    private var mockEmail: String = "test@test.com"

    private var mockLoginUser = mockk<User>(relaxed = true)

    private val subject: LoginRepositoryContract by inject()

    private val expectedResult = Result.Success(mockLoginUser)

    override fun setup() {
        super.setup()
        loadKoinModules(
            module {
                factory(override = true) {
                    loginDataSourceLocal
                }
                factory(override = true) {
                    loginDataSourceRemote
                }
            }
        )

        coEvery { loginDataSourceRemote.fetchUser(mockEmail) } returns expectedResult
        every { loginDataSourceLocal.update(expectedResult.data) } returns Unit
        every { loginDataSourceLocal.deleteUser() } returns Unit
    }

    @Test
    fun `When saved user exists then should return user from localDataSource `() =
        runBlockingTest {
            every { loginDataSourceLocal.loadUser() } returns expectedResult

            assertEquals(expectedResult.data, subject.user)
            assert(subject.isLoggedIn)

            verify(exactly = 0) {
                loginDataSourceLocal.update(expectedResult.data)
                loginDataSourceLocal.deleteUser()
            }

            verify(exactly = 1) {
                loginDataSourceLocal.loadUser()
            }

            coVerify {
                loginDataSourceRemote wasNot Called
            }

        }


    @Test
    fun `When login and saved user don't exists should fetch user from remoteDataSource`() =
        runBlockingTest {
            every { loginDataSourceLocal.loadUser() } returns Result.Error(
                IdDogError.IdDogNotFoundError(
                    ""
                )
            )

            subject.login(mockEmail)

            assertEquals(expectedResult.data, subject.user)
            assertTrue(subject.isLoggedIn)

            verify(exactly = 0) {
                loginDataSourceLocal.deleteUser()
            }

            coVerifySequence {
                loginDataSourceLocal.loadUser()
                loginDataSourceLocal.loadUser()
                loginDataSourceRemote.fetchUser(mockEmail)
                loginDataSourceLocal.update(expectedResult.data)
            }

        }

    @Test
    fun `When logout should remove user from localDataSource and in memory cache`() =
        runBlockingTest {
            subject.logout()

            assertNull(subject.user)
            assertFalse(subject.isLoggedIn)

            verify(exactly = 1) {
                loginDataSourceLocal.deleteUser()
            }
        }
}