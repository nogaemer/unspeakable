package de.nogaemer.unspeakable.features.game.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Runs a cancellable second-based countdown used for round timing.
 */
class Timer(
    private val scope: CoroutineScope,
    val maxTime: Int,
    val onTick: suspend (Int) -> Unit = {},
    val onFinish: suspend () -> Unit = {},
) {
    var timeLeft: Int = maxTime
        private set
    var isRunning: Boolean = false
        private set
    private var timerJob: Job? = null

    fun start() {
        reset()
        isRunning = true
        timerJob = scope.launch {
            while (isRunning) {
                onTick(timeLeft)
                timeLeft--
                delay(1000)
                if (timeLeft <= 0) {
                    onFinish()
                    isRunning = false
                }
            }
        }
    }

    fun reset() {
        isRunning = false
        timeLeft = maxTime
        timerJob?.cancel()
        timerJob = null
    }
}
