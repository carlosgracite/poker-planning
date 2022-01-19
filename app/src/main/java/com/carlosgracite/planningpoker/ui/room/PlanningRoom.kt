package com.carlosgracite.planningpoker.ui.room

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlosgracite.planningpoker.R
import com.carlosgracite.planningpoker.entity.PokerCard
import com.carlosgracite.planningpoker.entity.User
import com.carlosgracite.planningpoker.entity.UserWithVote
import com.carlosgracite.planningpoker.ui.utils.extensions.pluralResource
import com.carlosgracite.planningpoker.ui.theme.Green400
import com.carlosgracite.planningpoker.ui.theme.Indigo400
import com.carlosgracite.planningpoker.ui.theme.PlanningPokerTheme
import java.util.*

@ExperimentalFoundationApi
@Composable
fun PlanningRoom() {
    val viewModel: PlanningRoomViewModel = viewModel()

    val planningRoomState = viewModel.uiState.collectAsState().value

    PlanningRoomContent(
        planningRoomState = planningRoomState,
        handleEvent = viewModel::handleEvent
    )
}

@ExperimentalFoundationApi
@Composable
fun PlanningRoomContent(
    planningRoomState: PlanningRoomState,
    handleEvent: (PlanningRoomEvent) -> Unit
) {

    Scaffold(
        topBar = {
            RoomTopAppBar(
                roomId = planningRoomState.room?.id ?: "",
                onLeaveRoomClick = { handleEvent(PlanningRoomEvent.LeaveRoom) }
            )
        },
        floatingActionButton = {
            RoomFabButton(
                roomStatus = planningRoomState.roomStatus(),
                handleEvent = handleEvent
            )
        }
    ) {

        if (planningRoomState.isLoading()) {
            RoomProgressIndicator()
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            PlanningRoomHeader(
                planningRoomState = planningRoomState,
                cards = planningRoomState.room?.cards ?: emptyList(),
                onCardClick = {
                    handleEvent(PlanningRoomEvent.Vote(it))
                }
            )

            UserList(
                usersWithVotes = planningRoomState.usersWithVotes(),
                showVoteResult = planningRoomState.showVoteResult()
            )
        }
    }
}

@Composable
fun RoomTopAppBar(
    roomId: String,
    onLeaveRoomClick: () -> Unit
) {
    TopAppBar(
        title = {
            if (roomId.isNotEmpty()) {
                Text(text = stringResource(id = R.string.room_title, roomId))
            }
        },
        actions = {
            IconButton(onClick = onLeaveRoomClick) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = stringResource(id = R.string.leave_room)
                )
            }
        }
    )
}

@Composable
fun RoomFabButton(
    roomStatus: RoomStatus,
    handleEvent: (PlanningRoomEvent) -> Unit
) {
    val actionText = when (roomStatus) {
        RoomStatus.VOTING -> stringResource(id = R.string.action_show_results)
        RoomStatus.SHOWING_RESULTS -> stringResource(id = R.string.action_start_voting)
        else -> return
    }

    ExtendedFloatingActionButton(
        text = {
            Text(text = actionText)
        },
        onClick = {
            when (roomStatus) {
                RoomStatus.VOTING -> handleEvent(PlanningRoomEvent.RevealResults)
                RoomStatus.SHOWING_RESULTS -> handleEvent(PlanningRoomEvent.StartVoting)
                else -> {}
            }
        }
    )
}

@ExperimentalFoundationApi
@Composable
fun PlanningRoomHeader(
    modifier: Modifier = Modifier,
    planningRoomState: PlanningRoomState,
    cards: List<PokerCard>,
    onCardClick: (PokerCard) -> Unit,
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f))
            .animateContentSize()
    ) {
        Crossfade(
            planningRoomState.showVoteResult()
        ) { showVoteResult ->
            if (showVoteResult) {
                ResultBoard(
                    voteResult = planningRoomState.aggregatedVoteResult()
                )
            } else {
                CardBoard(
                    cards = cards,
                    onCardClick = onCardClick,
                    votedCard = planningRoomState.votedCard()
                )
            }
        }
    }
}

@Composable
fun ResultBoard(
    modifier: Modifier = Modifier,
    voteResult: AggregatedVoteResult
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            fontWeight = FontWeight.SemiBold,
            fontStyle = MaterialTheme.typography.h5.fontStyle,
            fontSize = MaterialTheme.typography.h5.fontSize,
            text = stringResource(id = R.string.voting_complete)
        )

        for (vote in voteResult.votes) {
            Row(
                modifier = Modifier
                    .alpha(if (vote.count == voteResult.maxCount()) 1f else .6f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(weight = 0.25f)
                        .padding(end = 16.dp),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    text = vote.pokerCard.text
                )

                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .fillMaxWidth(fraction = 0.4f)
                        .height(10.dp)
                        .clip(shape = RoundedCornerShape(percent = 100)),
                    progress = vote.count / voteResult.totalCount.toFloat()
                )

                Text(
                    modifier = Modifier
                        .weight(weight = 0.25f)
                        .padding(start = 16.dp),
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold,
                    text = pluralResource(
                        R.plurals.vote_count,
                        vote.count,
                        vote.count
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun CardBoard(
    modifier: Modifier = Modifier,
    cards: List<PokerCard>,
    onCardClick: (PokerCard) -> Unit,
    votedCard: PokerCard?
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(),
        cells = GridCells.Adaptive(minSize = 84.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(cards) { card ->
            PokerCard(
                modifier = Modifier
                    .padding(2.dp),
                card = card,
                onCardClick = onCardClick,
                isVoted = card == votedCard
            )
        }
    }
}

@Composable
fun PokerCard(
    modifier: Modifier = Modifier,
    card: PokerCard,
    onCardClick: (PokerCard) -> Unit,
    isVoted: Boolean
) {
    Box(
        modifier = modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isVoted) Green400 else Indigo400)
            .padding(3.dp)
            .border(2.dp, color = Color.White, RoundedCornerShape(7.dp))
            .clickable {
                onCardClick(card)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = card.text,
                color = Color.White,
                style = TextStyle(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(0f, 0f),
                        blurRadius = 20f
                    )
                ),
            )
        }
    }
}

@Composable
fun UserList(
    modifier: Modifier = Modifier,
    usersWithVotes: List<UserWithVote>,
    showVoteResult: Boolean
) {
    LazyColumn(
        modifier = modifier
    ) {
        for (userWithVote in usersWithVotes) {
            item(userWithVote.user.id) {
                UserRow(userWithVote = userWithVote, showVoteResult = showVoteResult)
            }
        }
    }
}

@Composable
fun UserRow(
    modifier: Modifier = Modifier,
    userWithVote: UserWithVote,
    showVoteResult: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                style = TextStyle(color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                text = userWithVote.user.name.take(2).uppercase()
            )
        }

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = userWithVote.user.name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        val pokerCard = userWithVote.vote

        if (showVoteResult && pokerCard != null) {
            VoteResultText(text = pokerCard.text)
        } else {
            AnimatedVisibility(visible = pokerCard != null, enter = fadeIn(), exit = fadeOut()) {
                VoteResultText(text = stringResource(id = R.string.voted))
            }
        }
    }
}

@Composable
fun VoteResultText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier.padding(start = 16.dp),
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        text = text,
        color = Green400
    )
}

@Composable
fun RoomProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun VoteResultPreview() {
    PlanningPokerTheme {
        ResultBoard(
            voteResult = AggregatedVoteResult(
                userWithVotes = listOf(
                    UserWithVote(User("1", "User1"), PokerCard(1, "X")),
                    UserWithVote(User("2", "User3"), PokerCard(1, "XXL")),
                ),
                votes = listOf(
                    VoteCount(5, PokerCard(2, "XXL")),
                    VoteCount(2, PokerCard(1, "X")),
                ),
                totalCount = 7
            )
        )
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun CardBoardPreview() {
    PlanningPokerTheme {
        CardBoard(
            cards = listOf(
                PokerCard(1, "XS"),
                PokerCard(2, "S"),
                PokerCard(3, "XXL"),
                PokerCard(4, "?"),
                PokerCard(5, "☕"),
            ),
            onCardClick = {},
            votedCard = PokerCard(2, "S")
        )
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun UserRowPreview() {
    PlanningPokerTheme {
        UserRow(
            userWithVote = UserWithVote(
                User("1", "User 1"), vote = PokerCard(1, "XS")
            ),
            showVoteResult = true
        )
    }
}