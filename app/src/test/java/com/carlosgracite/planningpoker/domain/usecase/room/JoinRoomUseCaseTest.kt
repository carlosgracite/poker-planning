package com.carlosgracite.planningpoker.domain.usecase.room

import com.carlosgracite.planningpoker.api.PlanningPokerApi
import com.carlosgracite.planningpoker.domain.model.JoinRoomError
import com.carlosgracite.planningpoker.entity.JoinRoomResult
import com.carlosgracite.planningpoker.entity.User
import com.carlosgracite.planningpoker.repository.SettingsStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class JoinRoomUseCaseTest {

    private val settingsStore: SettingsStore = mock()
    private val planningPokerApi: PlanningPokerApi = mock()

    private val joinRoomUseCase = JoinRoomUseCase(settingsStore, planningPokerApi)

    private val user = User("userId", "username")

    @Test
    fun `assert room is created with success`() = runTest {
        whenever(settingsStore.getOrCreateUser(any())).thenReturn(user)
        whenever(planningPokerApi.createRoom(any())).thenReturn(JoinRoomResult("roomId"))

        val result = joinRoomUseCase.execute("username", null)

        assertEquals(JoinRoomUseCase.Result.Success("roomId"), result)

        verify(settingsStore).saveRoomId("roomId")
    }

    @Test
    fun `assert room is joined with success`() = runTest {
        whenever(settingsStore.getOrCreateUser(any())).thenReturn(user)
        whenever(planningPokerApi.joinRoom("roomId", user)).thenReturn(JoinRoomResult("roomId"))

        val result = joinRoomUseCase.execute("username", "roomId")

        assertEquals(JoinRoomUseCase.Result.Success("roomId"), result)

        verify(settingsStore).saveRoomId("roomId")
    }

    @Test
    fun `assert failure occurs when trying to join an nonexistent room`() = runTest {
        whenever(settingsStore.getOrCreateUser(any())).thenReturn(user)

        given(planningPokerApi.joinRoom("UNKNOWN_ROOM_ID", user)).willAnswer {
            throw HttpException(Response.error<String>(400, "".toResponseBody()))
        }

        val result = joinRoomUseCase.execute("username", "UNKNOWN_ROOM_ID")

        assertEquals(JoinRoomUseCase.Result.Failure(JoinRoomError.RoomNotFound), result)
    }

    @Test
    fun `assert failure occurs when network error occurs on joining a room`() = runTest {
        whenever(settingsStore.getOrCreateUser(any())).thenReturn(user)
        given(planningPokerApi.joinRoom("roomId", user)).willAnswer { throw Exception() }

        val result = joinRoomUseCase.execute("username", "roomId")

        assertEquals(JoinRoomUseCase.Result.Failure(JoinRoomError.NetworkError), result)
    }

    @Test
    fun `assert failure occurs for invalid user name`() = runTest {
        val result = joinRoomUseCase.execute("us", "roomId")

        assertEquals(JoinRoomUseCase.Result.Failure(JoinRoomError.InvalidUserName), result)
    }

}