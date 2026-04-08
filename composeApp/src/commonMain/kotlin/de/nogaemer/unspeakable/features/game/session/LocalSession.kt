package de.nogaemer.unspeakable.features.game.session

import de.nogaemer.unspeakable.core.model.GameClientEvent
import de.nogaemer.unspeakable.core.model.Player
import de.nogaemer.unspeakable.core.model.ProfilePicture
import de.nogaemer.unspeakable.db.UnspeakableCardsDao
import de.nogaemer.unspeakable.features.game.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 * Runs a same-device game session by routing events directly through authority.
 */
class LocalSession(
    playerName: String,
    profilePicture: ProfilePicture,
    scope: CoroutineScope,
    cardDao: UnspeakableCardsDao,
    lang: String,
) : GameSession {

    private val me = Player(
        "",
        playerName,
        isHost = true,
        profilePicture = profilePicture,
        teamId = "")
    private val authority = GameAuthority(scope, cardDao, lang, me, true)

    override val state: StateFlow<GameState> = authority.state

    override suspend fun start() {
        authority.processEvent(GameClientEvent.JoinGame(me).toHostBoundEvent(me.id))
    }

    override suspend fun sendEvent(event: GameClientEvent) =
        authority.processEvent(event.toHostBoundEvent(me.id))

    override suspend fun sendEventAs(event: GameClientEvent, playerId: String) =
        authority.processEvent(event.toHostBoundEvent(playerId))

    override fun close() = authority.close()
}
