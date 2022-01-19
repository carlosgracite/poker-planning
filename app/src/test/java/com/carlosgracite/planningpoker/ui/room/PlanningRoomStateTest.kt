package com.carlosgracite.planningpoker.ui.room

import com.carlosgracite.planningpoker.entity.*
import com.carlosgracite.planningpoker.fixtures.*
import org.junit.Assert.*
import org.junit.Test

class PlanningRoomStateTest {

    private val state = PlanningRoomState(
        room = room,
        user = user1,
        connecting = false
    )

    @Test
    fun `expect user with votes from VoteResult when showing results`() {
        val currentState = state.copy(
            room = state.room?.copy(
                voteResult = VoteResult(listOf(UserWithVote(user1, card1)))
            )
        )

        assertEquals(currentState.roomStatus(), RoomStatus.SHOWING_RESULTS)
        assertEquals(currentState.usersWithVotes(), listOf(UserWithVote(user1, card1)))
    }

    @Test
    fun `expect user with votes from Room users when showing results`() {
        val currentState = state.copy(
            room = state.room?.copy(
                votes = setOf(Vote(user1.id, card1))
            )
        )

        assertEquals(currentState.roomStatus(), RoomStatus.VOTING)
        assertEquals(currentState.usersWithVotes(), listOf(UserWithVote(user1, card1)))
    }

    @Test
    fun `expect empty status when no vote were done and is not showing results`() {
        val currentState = state.copy(
            room = state.room?.copy(
                votes = emptySet(),
                voteResult = null
            )
        )

        assertEquals(currentState.roomStatus(), RoomStatus.VOTING_EMPTY)
    }

    @Test
    fun `expect correct aggregated vote result`() {
        val currentState = state.copy(
            room = state.room?.copy(
                voteResult = VoteResult(
                    listOf(UserWithVote(user1, card1), UserWithVote(user2, card2)),
                )
            )
        )

        val expected = AggregatedVoteResult(
            userWithVotes = listOf(UserWithVote(user1, card1), UserWithVote(user2, card2)),
            votes = listOf(VoteCount(1, card1), VoteCount(1, card2)),
            totalCount = 2
        )

        assertEquals(currentState.aggregatedVoteResult(), expected)
    }

    @Test
    fun `expect loading status when connecting or nonexistent room`() {
        assertEquals(state.copy(room = null).roomStatus(), RoomStatus.LOADING)
        assertEquals(state.copy(connecting = true).roomStatus(), RoomStatus.LOADING)
    }

}