package com.carlosgracite.planningpoker.api

import com.carlosgracite.planningpoker.entity.CreateRoomBody
import com.carlosgracite.planningpoker.entity.JoinRoomResult
import com.carlosgracite.planningpoker.entity.User
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PlanningPokerApi {

    @POST("/api/room")
    suspend fun createRoom(
        @Body createRoomBody: CreateRoomBody
    ): JoinRoomResult

    @POST("/api/room/{roomId}/user")
    suspend fun joinRoom(
        @Path("roomId") roomId: String,
        @Body user: User
    ): JoinRoomResult

}