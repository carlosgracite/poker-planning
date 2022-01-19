package com.carlosgracite.planningpoker.domain.usecase.voting

import com.carlosgracite.planningpoker.api.websocket.PokerPlanningSocketService
import com.carlosgracite.planningpoker.entity.PokerCard
import com.carlosgracite.planningpoker.api.websocket.VoteMessage
import javax.inject.Inject

class VoteUseCase @Inject constructor(
    private val pokerPlanningSocketService: PokerPlanningSocketService
) {

    fun execute(card: PokerCard) {
        pokerPlanningSocketService.vote(VoteMessage(card = card))
    }

}