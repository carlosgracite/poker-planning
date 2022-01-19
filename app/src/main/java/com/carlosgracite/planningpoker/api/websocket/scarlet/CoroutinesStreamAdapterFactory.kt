package com.carlosgracite.planningpoker.api.websocket.scarlet

import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.utils.getRawType
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type

private const val DEFAULT_BUFFER = 128

/**
 * Configuration extracted from pending Scarlet pull request to support coroutines version 1.6+.
 *
 * https://github.com/Tinder/Scarlet/pull/200
 */
class CoroutinesStreamAdapterFactory(
    private val bufferSize: Int = DEFAULT_BUFFER
) : StreamAdapter.Factory {

    override fun create(type: Type): StreamAdapter<Any, Any> {
        return when (type.getRawType()) {
            Flow::class.java -> FlowStreamAdapter(bufferSize)
            ReceiveChannel::class.java -> ReceiveChannelStreamAdapter(bufferSize)
            else -> throw IllegalArgumentException()
        }
    }
}