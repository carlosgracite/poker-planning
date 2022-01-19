package com.carlosgracite.planningpoker.ui.utils.extensions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
fun Modifier.bringIntoViewOnFocus(): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Modifier
        .bringIntoViewRequester(bringIntoViewRequester)
        .onFocusEvent {
            if (it.isFocused || it.hasFocus) {
                coroutineScope.launch {
                    delay(450)
                    bringIntoViewRequester.bringIntoView()
                }
            }
        }
}