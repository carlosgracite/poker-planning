package com.carlosgracite.planningpoker.domain.usecase.voting

import com.carlosgracite.planningpoker.api.websocket.PokerPlanningSocketService
import com.carlosgracite.planningpoker.api.websocket.StartVotingMessage
import javax.inject.Inject

class StartVotingUseCase @Inject constructor(
    private val pokerPlanningSocketService: PokerPlanningSocketService
) {

    fun execute() {
        pokerPlanningSocketService.startVoting(StartVotingMessage())
    }

}