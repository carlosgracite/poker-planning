package com.carlosgracite.planningpoker.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosgracite.planningpoker.domain.usecase.connection.ObserveWebSocketStateUseCase
import com.carlosgracite.planningpoker.domain.usecase.profile.GetUserProfileUseCase
import com.carlosgracite.planningpoker.domain.usecase.room.LeaveRoomUseCase
import com.carlosgracite.planningpoker.domain.usecase.room.ObserveRoomUpdatesUseCase
import com.carlosgracite.planningpoker.domain.usecase.room.SubscribeToRoomUpdatesUseCase
import com.carlosgracite.planningpoker.domain.usecase.voting.RevealResultsUseCase
import com.carlosgracite.planningpoker.domain.usecase.voting.StartVotingUseCase
import com.carlosgracite.planningpoker.domain.usecase.voting.VoteUseCase
import com.carlosgracite.planningpoker.entity.PokerCard
import com.carlosgracite.planningpoker.entity.Room
import com.carlosgracite.planningpoker.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanningRoomViewModel @Inject constructor(
    private val observeRoomUpdatesUseCase: ObserveRoomUpdatesUseCase,
    private val observeWebSocketStateUseCase: ObserveWebSocketStateUseCase,
    private val leaveRoomUseCase: LeaveRoomUseCase,
    private val subscribeToRoomUpdatesUseCase: SubscribeToRoomUpdatesUseCase,
    private val voteUseCase: VoteUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val startVotingUseCase: StartVotingUseCase,
    private val revealResultsUseCase: RevealResultsUseCase
) : ViewModel() {

    val uiState = MutableStateFlow(PlanningRoomState())

    init {

        viewModelScope.launch {
            observeWebSocketStateUseCase.execute().collect { result ->
                val connecting = when (result) {
                    ObserveWebSocketStateUseCase.Result.CONNECTED -> false
                    ObserveWebSocketStateUseCase.Result.CONNECTING -> true
                }

                uiState.value = uiState.value.copy(
                    connecting = connecting
                )
            }
        }

        viewModelScope.launch {
            observeRoomUpdatesUseCase.execute().collect {
                updateRoom(it)
            }
        }

        viewModelScope.launch {
            subscribeToRoomUpdatesUseCase.execute()
        }

        viewModelScope.launch {
            getUserProfileUseCase.execute().collect {
                updateUserProfile(it)
            }
        }
    }

    private fun updateUserProfile(user: User?) {
        uiState.value = uiState.value.copy(
            user = user
        )
    }

    fun handleEvent(event: PlanningRoomEvent) {
        when (event) {
            PlanningRoomEvent.LeaveRoom -> {
                leaveRoom()
            }
            is PlanningRoomEvent.Vote -> {
                vote(event.card)
            }
            PlanningRoomEvent.RevealResults -> {
                revealResults()
            }
            PlanningRoomEvent.StartVoting -> {
                startVoting()
            }
        }
    }

    private fun leaveRoom() {
        viewModelScope.launch {
            leaveRoomUseCase.execute()
        }
    }

    private fun vote(card: PokerCard) {
        voteUseCase.execute(card)
    }

    private fun revealResults() {
        revealResultsUseCase.execute()
    }

    private fun startVoting() {
        startVotingUseCase.execute()
    }

    private fun updateRoom(room: Room) {
        uiState.value = uiState.value.copy(
            room = room
        )
    }

}