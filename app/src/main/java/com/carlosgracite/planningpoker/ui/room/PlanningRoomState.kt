package com.carlosgracite.planningpoker.ui.room

import com.carlosgracite.planningpoker.entity.PokerCard
import com.carlosgracite.planningpoker.entity.Room
import com.carlosgracite.planningpoker.entity.User
import com.carlosgracite.planningpoker.entity.UserWithVote

data class PlanningRoomState(
    val room: Room? = null,
    val user: User? = null,
    val connecting: Boolean = true
) {

    fun roomStatus(): RoomStatus {
        return when {
            room == null || connecting -> RoomStatus.LOADING
            room.voteResult != null -> RoomStatus.SHOWING_RESULTS
            room.votes.isEmpty() -> RoomStatus.VOTING_EMPTY
            room.votes.isNotEmpty() -> RoomStatus.VOTING
            else -> RoomStatus.LOADING
        }
    }

    fun votedCard(): PokerCard? {
        return room?.votes?.find { it.user == user?.id }?.card
    }

    fun isLoading(): Boolean {
        return roomStatus() == RoomStatus.LOADING
    }

    fun showVoteResult(): Boolean {
        return roomStatus() == RoomStatus.SHOWING_RESULTS
    }

    fun usersWithVotes(): List<UserWithVote> {
        if (room == null) {
            return emptyList()
        }

        return if (room.voteResult != null) {
            room.voteResult.userWithVotes
        } else {

            val votes = room.votes
            val users = room.users

            users.map { user ->
                UserWithVote(user, votes.find { it.user == user.id }?.card)
            }
        }.sortedBy { it.user.name }
    }

    fun aggregatedVoteResult(): AggregatedVoteResult {
        val voteResult = room?.voteResult ?: return AggregatedVoteResult()

        val votes = voteResult.userWithVotes
            .groupingBy { it.vote }
            .eachCount()
            .mapNotNull { entry ->
                entry.key?.let { VoteCount(count = entry.value, it) }
            }

        return AggregatedVoteResult(
            userWithVotes = voteResult.userWithVotes,
            votes = votes,
            totalCount = votes.fold(0, { acc, item -> acc + item.count} )
        )
    }
}

data class AggregatedVoteResult(
    val userWithVotes: List<UserWithVote> = emptyList(),
    val votes: List<VoteCount> = emptyList(),
    val totalCount: Int = 0
) {
    fun maxCount() = votes.firstOrNull()?.count ?: 0
}

data class VoteCount(
    val count: Int,
    val pokerCard: PokerCard
)

enum class RoomStatus {
    LOADING, VOTING, VOTING_EMPTY, SHOWING_RESULTS
}