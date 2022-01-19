package com.carlosgracite.planningpoker.ui.room

import app.cash.turbine.test
import com.carlosgracite.planningpoker.domain.usecase.connection.ObserveWebSocketStateUseCase
import com.carlosgracite.planningpoker.domain.usecase.profile.GetUserProfileUseCase
import com.carlosgracite.planningpoker.domain.usecase.room.LeaveRoomUseCase
import com.carlosgracite.planningpoker.domain.usecase.room.ObserveRoomUpdatesUseCase
import com.carlosgracite.planningpoker.domain.usecase.room.SubscribeToRoomUpdatesUseCase
import com.carlosgracite.planningpoker.domain.usecase.voting.RevealResultsUseCase
import com.carlosgracite.planningpoker.domain.usecase.voting.StartVotingUseCase
import com.carlosgracite.planningpoker.domain.usecase.voting.VoteUseCase
import com.carlosgracite.planningpoker.entity.Room
import com.carlosgracite.planningpoker.entity.User
import com.carlosgracite.planningpoker.fixtures.room
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
class PlanningRoomViewModelTest {

    private val observeRoomUpdatesUseCase: ObserveRoomUpdatesUseCase = mock()
    private val observeWebSocketStateUseCase: ObserveWebSocketStateUseCase = mock()
    private val leaveRoomUseCase: LeaveRoomUseCase = mock()
    private val subscribeToRoomUpdatesUseCase: SubscribeToRoomUpdatesUseCase = mock()
    private val voteUseCase: VoteUseCase = mock()
    private val getUserProfileUseCase: GetUserProfileUseCase = mock()
    private val startVotingUseCase: StartVotingUseCase = mock()
    private val revealResultsUseCase: RevealResultsUseCase = mock()

    private val webSocketStateFlow = MutableSharedFlow<ObserveWebSocketStateUseCase.Result>(replay = 0)
    private val observeRoomUpdatesFlow = MutableSharedFlow<Room>(replay = 0)
    private val getUserProfileFlow = MutableSharedFlow<User>(replay = 0)

    private lateinit var viewModel: PlanningRoomViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = PlanningRoomViewModel(
            observeRoomUpdatesUseCase,
            observeWebSocketStateUseCase,
            leaveRoomUseCase,
            subscribeToRoomUpdatesUseCase,
            voteUseCase,
            getUserProfileUseCase,
            startVotingUseCase,
            revealResultsUseCase,
        )

        runBlocking {
            whenever(observeWebSocketStateUseCase.execute()).thenReturn(webSocketStateFlow)
            whenever(observeRoomUpdatesUseCase.execute()).thenReturn(observeRoomUpdatesFlow)
            whenever(getUserProfileUseCase.execute()).thenReturn(getUserProfileFlow)
        }
    }

    @Test
    fun `assert correct events are triggered during initialization`() = runTest {
        viewModel.uiState.test {
            runCurrent()

            webSocketStateFlow.emit(ObserveWebSocketStateUseCase.Result.CONNECTING)
            webSocketStateFlow.emit(ObserveWebSocketStateUseCase.Result.CONNECTED)
            observeRoomUpdatesFlow.emit(room)
            getUserProfileFlow.emit(user1)

            assertEquals(PlanningRoomState(connecting = true), awaitItem())
            assertEquals(PlanningRoomState(connecting = false), awaitItem())
            assertEquals(PlanningRoomState(connecting = false, room = room), awaitItem())
            assertEquals(
                PlanningRoomState(connecting = false, room = room, user = user1),
                awaitItem()
            )
        }
    }

}