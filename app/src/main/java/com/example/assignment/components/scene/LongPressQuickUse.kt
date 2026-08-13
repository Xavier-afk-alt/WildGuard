package com.example.assignment.components.scene

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

fun Modifier.twoSecondQuickUse(
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    durationMillis: Long = 2_000L
): Modifier = pointerInput(Unit) {

    awaitEachGesture {

        awaitFirstDown(
            requireUnconsumed = false
        )

        try {

            // If released before 2 seconds,
            // this returns normally.
            withTimeout(durationMillis) {
                waitForUpOrCancellation()
            }

            // Released before timeout
            onTap()

        } catch (_: TimeoutCancellationException) {

            // Still holding after 2 seconds
            onLongPress()

            // Wait until the user releases the mouse/finger
            waitForUpOrCancellation()
        }
    }
}