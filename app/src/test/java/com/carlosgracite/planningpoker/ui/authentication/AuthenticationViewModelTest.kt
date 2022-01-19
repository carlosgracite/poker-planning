package com.carlosgracite.planningpoker.ui.authentication

import app.cash.turbine.Event
import app.cash.turbine.test
import com.carlosgracite.planningpoker.domain.model.JoinRoomError
import com.carlosgracite.planningpoker.domain.usecase.profile.GetUserProfileUseCase
import com.carlosgracite.planningpoker.domain.usecase.room.JoinRoomUseCase
import com.carlosgracite.planningpoker.entity.User
import com.carlosgracite.planningpoker.fixtures.user1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AuthenticationViewModelTest {

    private val getUserProfileUseCase: GetUserProfileUseCase = mock()
    private val joinRoomUseCase: JoinRoomUseCase = mock()

    private val webSocketStateFlow = MutableSharedFlow<User?>(replay = 0)

    private lateinit var viewModel: AuthenticationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = AuthenticationViewModel(getUserProfileUseCase, joinRoomUseCase)

        runBlocking {
            whenever(getUserProfileUseCase.execute()).thenReturn(webSocketStateFlow)
        }
    }

    @Test
    fun `assert saved user name is loaded during initialization`() = runTest {
        viewModel.uiState.test {
            runCurrent()

            webSocketStateFlow.emit(user1)

            assertEquals(AuthenticationState(), awaitItem())
            assertEquals(AuthenticationState(username = user1.name), awaitItem())
        }
    }

    @Test
    fun `assert user joins room successfully when entering correct inputs`() = runTest {
        viewModel.uiState.test {
            runCurrent()

            viewModel.handleEvent(AuthenticationEvent.UserNameChanged("User"))
            viewModel.handleEvent(AuthenticationEvent.RoomIdChanged("roomId"))
            viewModel.handleEvent(AuthenticationEvent.Authenticate)

            whenever(joinRoomUseCase.execute("User", "roomId"))
                .thenReturn(JoinRoomUseCase.Result.Success("roomId"))

            assertEquals(AuthenticationState(), awaitItem())
            assertEquals(AuthenticationState(username = "User"), awaitItem())
            assertEquals(AuthenticationState(username = "User", roomId = "roomId"), awaitItem())
            assertEquals(AuthenticationState(username = "User", roomId = "roomId", isLoading = true), awaitItem())
            assertEquals(AuthenticationState(username = "User", roomId = "roomId", isLoading = false), awaitItem())
        }
    }

    @Test
    fun `assert finishes at error state when room id is not found`() = runTest {
        viewModel.uiState.test {
            viewModel.handleEvent(AuthenticationEvent.UserNameChanged("User"))
            viewModel.handleEvent(AuthenticationEvent.RoomIdChanged("roomId"))
            viewModel.handleEvent(AuthenticationEvent.Authenticate)

            whenever(joinRoomUseCase.execute("User", "roomId"))
                .thenReturn(JoinRoomUseCase.Result.Failure(JoinRoomError.RoomNotFound))

            runCurrent()

            val actual = cancelAndConsumeRemainingEvents().last()

            val expected = AuthenticationState(
                username = "User",
                roomId = "roomId",
                isLoading = false,
                error = JoinRoomError.RoomNotFound
            )

            assertEquals(Event.Item(expected), actual)
        }
    }

}