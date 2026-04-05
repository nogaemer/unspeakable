package de.nogaemer.unspeakable.core.mode

/**
 * Marker for a capability that a game mode can claim exclusively or share.
 *
 * Exclusive behavioral slots. Only ONE active mode may claim each capability.
 * If two modes claim the same capability → incompatible.
 */
enum class Capability {
    // Timer control — only one mode can own the timer logic
    TIMER_CONTROL,

    // Card mutation — only one mode may rewrite the card mid-round
    CARD_MUTATION,

    // Scoring override — replaces the standard correct=1pt rule
    SCORING_OVERRIDE,

    // Language rule — restricts how the describer may speak
    LANGUAGE_RESTRICTION,

    // Persistent turn state — accumulates data across cards in a single turn
    TURN_ACCUMULATION,

    // Round framing — changes how a round is structured/initiated
    ROUND_FRAMING,
}