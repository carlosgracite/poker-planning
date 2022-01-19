package com.carlosgracite.planningpoker.domain.usecase.voting

import com.carlosgracite.planningpoker.api.websocket.PokerPlanningSocketService
import com.carlosgracite.planningpoker.api.websocket.RevealResultsMessage
import javax.inject.Inject

class RevealResultsUseCase @Inject constructor(
    private val pokerPlanningSocketService: PokerPlanningSocketService
) {

    fun execute() {
        pokerPlanningSocketService.revealResults(RevealResultsMessage())
    }

}