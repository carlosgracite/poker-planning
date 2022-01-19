package com.carlosgracite.planningpoker.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Room(
    val id: String,
    val users: List<User>,
    val cards: List<PokerCard>,
    val votes: Set<Vote>,
    val voteResult: VoteResult?
)

@JsonClass(generateAdapter = true)
data class PokerCard(
    val id: Int,
    val text: String
)

@JsonClass(generateAdapter = true)
data class Vote(
    val user: String,
    val card: PokerCard
)

@JsonClass(generateAdapter = true)
data class JoinRoomResult(
    val roomId: String
)

@JsonClass(generateAdapter = true)
data class CreateRoomBody(
    val user: User
)

@JsonClass(generateAdapter = true)
data class VoteResult(
    val userWithVotes: List<UserWithVote>
)

@JsonClass(generateAdapter = true)
data class UserWithVote(
    val user: User,
    val vote: PokerCard?
)