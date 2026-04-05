# User Tutorial

This guide explains how to set up and play Unspeakable with local or LAN multiplayer.

## 1) Choose How You Want to Play

From the **Home** screen:

- **Local**: one-device play.
- **Host a Game**: host creates a LAN lobby for others.
- **Join a Game**: player joins an existing host.

## 2) Enter Player Name

- Type your name.
- Continue to start or join the session.

## 3) Join a Team in Lobby

In the lobby:

- Tap the **+** card in a team to join that team.
- Host can open lobby settings (gear icon).
- Host can open QR/invite info (QR icon) for others.

## 4) Configure Lobby Settings (Host)

Host can change these in lobby settings:

- **Round time** (anchored slider).
- **Round count** (preset or custom).
- **Game modes** (toggle supported modes).
- **Categories** (select included card categories).

### Category Names by ID

Categories are stored in DB by ID and displayed using localized strings.

- Source map: `strings.game.categoryStrings`
- Helper: `strings.categoryName(id)`

If an ID is missing in localization, UI falls back to showing the raw ID.

## 5) Start the Match

Host presses **Start Game**. Then each round follows this flow:

1. **Ready phase**: current explainer starts turn.
2. **Playing phase**:
   - Explainer sees target word + forbidden words.
   - Explainer marks outcomes: correct, skipped, wrong.
   - Opponent can report forbidden-word violation.
   - If Sabotage mode is enabled, opponent can open sabotage sheet, enter a sabotage word (max 20 chars), and send it.
3. **Round summary**: review all cards by outcome.
4. Host continues to next round or ends when max rounds reached.

## 6) Game Over

At the end of the match, the game-over screen shows:

- team ranking,
- total points,
- card outcome stats,
- pace/time stats,
- back-to-home action.

## 7) Troubleshooting

- If join fails, verify host IP and that both devices are on same LAN.
- If host disconnects, clients are moved to a connection-lost screen.
- If no category is selected, app treats it as "all categories".

