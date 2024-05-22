package org.vivlaniv.nexohub.util.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

inline fun CoroutineScope.launchWithFixedDelay(
    delay: Long,
    crossinline action: () -> Unit
) {
    launch {
        while (true) {
            delay(delay)
            action()
        }
    }
}