package com.carlosgracite.planningpoker.fixtures

import com.carlosgracite.planningpoker.entity.PokerCard
import com.carlosgracite.planningpoker.entity.Room
import com.carlosgracite.planningpoker.entity.User

val user1 = User("1", "user1")
val user2 = User("2", "user2")

val card1 = PokerCard(1, "XS")
val card2 = PokerCard(2, "S")

val room = Room(
    id = "roomId",
    users = listOf(user1),
    cards = listOf(card1, card2),
    votes = emptySet(),
    voteResult = null
)