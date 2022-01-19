package com.carlosgracite.planningpoker.ui.authentication

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlosgracite.planningpoker.R
import com.carlosgracite.planningpoker.domain.model.JoinRoomError
import com.carlosgracite.planningpoker.ui.utils.extensions.bringIntoViewOnFocus
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalFoundationApi
@Composable
fun Authentication() {
    val viewModel: AuthenticationViewModel = viewModel()

    AuthenticationContent(
        modifier = Modifier.fillMaxWidth(),
        authenticationState = viewModel.uiState.collectAsState().value,
        handleEvent = viewModel::handleEvent
    )
}

@ExperimentalFoundationApi
@Composable
fun AuthenticationContent(
    modifier: Modifier = Modifier,
    authenticationState: AuthenticationState,
    handleEvent: (AuthenticationEvent) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsWithImePadding(),
        contentAlignment = Alignment.Center
    ) {
        if (authenticationState.isLoading) {
            CircularProgressIndicator()
        } else {
            AuthenticationForm(
                modifier = Modifier.fillMaxSize(),
                isJoinMode = authenticationState.isJoinMode(),
                username = authenticationState.username,
                roomId = authenticationState.roomId,
                enableAuthentication = authenticationState.isFormValid(),
                onUserNameChanged = { username ->
                    handleEvent(AuthenticationEvent.UserNameChanged(username))
                },
                onRoomIdChanged = { roomId ->
                    handleEvent(AuthenticationEvent.RoomIdChanged(roomId))
                },
                onAuthenticate = {
                    handleEvent(AuthenticationEvent.Authenticate)
                },
            )
        }

        authenticationState.error?.let { error ->
            AuthenticationErrorDialog(error = error) {
                handleEvent(AuthenticationEvent.DismissError)
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun AuthenticationForm(
    modifier: Modifier = Modifier,
    isJoinMode: Boolean,
    username: String?,
    roomId: String?,
    enableAuthentication: Boolean,
    onUserNameChanged: (username: String) -> Unit,
    onRoomIdChanged: (roomId: String) -> Unit,
    onAuthenticate: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val roomIdFocusRequester = FocusRequester()

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                UserNameInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewOnFocus(),
                    username = username,
                    onUserNameChanged = onUserNameChanged,
                    onNextClicked = {
                        roomIdFocusRequester.requestFocus()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                RoomIdInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewOnFocus()
                        .focusRequester(roomIdFocusRequester),
                    roomId = roomId,
                    onRoomIdChanged = onRoomIdChanged,
                    onDoneClicked = onAuthenticate,
                )

                Spacer(modifier = Modifier.height(12.dp))

                AuthenticationButton(
                    isJoinMode = isJoinMode,
                    enableAuthentication = enableAuthentication,
                    onAuthenticate = onAuthenticate,
                )
            }
        }

    }
}

@Composable
fun UserNameInput(
    modifier: Modifier,
    username: String?,
    onUserNameChanged: (username: String) -> Unit,
    onNextClicked: () -> Unit
) {
    TextField(
        modifier = modifier,
        value = username ?: "",
        onValueChange = {
            onUserNameChanged(it)
        },
        label = {
            Text(text = stringResource(id = R.string.label_username))
        },
        singleLine = true,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Person, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                onNextClicked()
            }
        ),
    )
}

@Composable
fun RoomIdInput(
    modifier: Modifier = Modifier,
    roomId: String?,
    onRoomIdChanged: (roomId: String) -> Unit,
    onDoneClicked: () -> Unit,
) {
    val clickLabel = stringResource(id = R.string.qr_code)

    TextField(
        modifier = modifier,
        value = roomId ?: "",
        onValueChange = {
            onRoomIdChanged(it)
        },
        singleLine = true,
        label = {
            Text(text = stringResource(id = R.string.label_room_id))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.MeetingRoom,
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable(
                    onClickLabel = clickLabel
                ) {
                    // TODO
                },
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = clickLabel
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDoneClicked()
            }
        )
    )
}

@Composable
fun AuthenticationButton(
    isJoinMode: Boolean,
    enableAuthentication: Boolean,
    onAuthenticate: () -> Unit
) {
    Button(
        onClick = onAuthenticate,
        enabled = enableAuthentication,
    ) {
        Text(
            text = when (isJoinMode) {
                true -> stringResource(id = R.string.join_room)
                false -> stringResource(id = R.string.create_room)
            }
        )
    }
}

@Composable
fun AuthenticationErrorDialog(
    modifier: Modifier = Modifier,
    error: JoinRoomError,
    dismissError: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = dismissError,
        confirmButton = {
            TextButton(onClick = dismissError) {
                Text(
                    text = stringResource(id = android.R.string.ok)
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.error_title),
                fontSize = 18.sp,
            )
        },
        text = {
            val errorMessage = when (error) {
                JoinRoomError.NetworkError -> stringResource(id = R.string.network_error)
                JoinRoomError.RoomNotFound -> stringResource(id = R.string.room_not_found)
                JoinRoomError.InvalidUserName -> stringResource(id = R.string.invalid_user_name)
            }

            Text(text = errorMessage)
        }
    )
}