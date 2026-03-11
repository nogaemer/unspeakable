package de.nogaemer.unspeakable.features.game.session

import de.nogaemer.unspeakable.core.model.GameEvent
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

abstract class HostAuthority(
    protected val scope: CoroutineScope
) : GameSession {
    protected val _state = MutableStateFlow(GameState(isHost = true))
    override val state = _state.asStateFlow()

    private val timer = Timer(scope, 10, onTick = { tick ->
        handleSideEffects(GameEvent.Tick(tick))
    })

    protected suspend fun applyEventAndBroadcast(finalEvent: GameEvent) {
        _state.update { it.applyEvent(finalEvent) }
        broadcast(finalEvent)
    }

    protected abstract suspend fun broadcast(event: GameEvent)


    protected suspend fun handleSideEffects(event: GameEvent) {
        when (event) {
            GameEvent.RequestNewRandomCard -> {
                timer.reset()
                timer.start()
                handleSideEffects(GameEvent.SendMaxRoundTime(timer.maxTime))

                try {
                    val newCard = Graph.dao.getRandomCard("en") ?: return
                    handleSideEffects(GameEvent.SendCard(newCard))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            else -> {
                applyEventAndBroadcast(event)
            }
        }
    }


    protected suspend fun applyEvent(event: GameEvent) {
        _state.update { it.applyEvent(event) }
    }
}

class Timer(
    private val scope: CoroutineScope,
    var maxTime: Int,
    var timeLeft: Int = maxTime,
    val onTick: suspend (Int) -> Unit = {},
    val onFinish: suspend () -> Unit = {},
    var isRunning: Boolean = false
) {
    private var timerJob: Job? = null

    init {
        reset()
    }

    fun start() {
        reset()
        resume()
        println(Clock.System.now())

        timerJob = scope.launch {
            while (isRunning) {
                onTick(timeLeft)
                tick()

                delay(1000)
                if (timeLeft <= 0) {
                    onFinish()
                    pause()
                    println(Clock.System.now())
                }
            }
        }
    }

    fun resume() {
        isRunning = true
    }

    fun pause() {
        isRunning = false
    }

    fun reset() {
        timeLeft = maxTime
        isRunning = false

        timerJob?.cancel()
        timerJob = null
    }

    fun tick() {
        timeLeft--
    }

    fun isFinished() = timeLeft <= 0
}