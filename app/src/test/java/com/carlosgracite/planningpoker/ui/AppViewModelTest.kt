package com.carlosgracite.planningpoker.ui

import app.cash.turbine.test
import com.carlosgracite.planningpoker.domain.usecase.room.CurrentRoomStreamUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AppViewModelTest {

    private val currentRoomStreamUseCase: CurrentRoomStreamUseCase = mock()

    private lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = AppViewModel(currentRoomStreamUseCase)
    }

    @Test
    fun `should redirect to room screen if there is a roomId saved`() = runTest {
        whenever(currentRoomStreamUseCase.execute()).thenReturn(flowOf("roomId"))

        viewModel.uiState.asStateFlow().test {
            runCurrent()
            assertEquals(AppState(screen = Screen.SPLASH), awaitItem())
            assertEquals(AppState(screen = Screen.ROOM), awaitItem())
        }
    }

    @Test
    fun `should redirect to authentication screen if there is no roomId saved`() = runTest {
        whenever(currentRoomStreamUseCase.execute()).thenReturn(flowOf(null))

        viewModel.uiState.asStateFlow().test {
            runCurrent()
            assertEquals(AppState(screen = Screen.SPLASH), awaitItem())
            assertEquals(AppState(screen = Screen.AUTHENTICATION), awaitItem())
        }
    }

}