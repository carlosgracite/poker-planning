package com.carlosgracite.planningpoker.domain.usecase.room

import com.carlosgracite.planningpoker.api.PlanningPokerApi
import com.carlosgracite.planningpoker.domain.model.JoinRoomError
import com.carlosgracite.planningpoker.entity.CreateRoomBody
import com.carlosgracite.planningpoker.entity.MIN_USERNAME_CHARACTERS
import com.carlosgracite.planningpoker.repository.SettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class JoinRoomUseCase @Inject constructor(
    private val settingsStore: SettingsStore,
    private val planningPokerApi: PlanningPokerApi
) {

    suspend fun execute(username: String, roomId: String?): Result {
        return withContext(Dispatchers.IO) {
            try {
                if (username.length < MIN_USERNAME_CHARACTERS) {
                    return@withContext Result.Failure(JoinRoomError.InvalidUserName)
                }

                val user = settingsStore.getOrCreateUser(username)

                val result = if (roomId.isNullOrBlank()) {
                    planningPokerApi.createRoom(CreateRoomBody(user))
                } else {
                    planningPokerApi.joinRoom(roomId, user)
                }

                settingsStore.saveRoomId(result.roomId)

                Result.Success(result.roomId)

            } catch (e: Exception) {
                if (e is HttpException && e.code() == 400) {
                    Result.Failure(JoinRoomError.RoomNotFound)
                } else {
                    Result.Failure(JoinRoomError.NetworkError)
                }
            }
        }
    }

    sealed class Result {
        data class Success(val roomId: String) : Result()
        data class Failure(val cause: JoinRoomError) : Result()
    }

}