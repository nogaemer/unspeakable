# User Tutorial

This guide explains how to set up and play Unspeakable with local or LAN multiplayer.

## 1) Choose How You Want to Play

From the **Home** screen:

- **Local** — pass-and-play on one device; the device is passed between players each turn.
- **Host a Game** — host creates a LAN lobby others can join from their own device.
- **Join a Game** — enter a code or IP to join an existing host.

## 2) Enter Your Player Name

Type your name and continue to start or join the session.

## 3) Set Up the Lobby

In the lobby:

- Tap the **+** card in a team to join it.
- In **local mode**, the host can tap **Add Player** to add more players and drag them between teams.
- Host can open **lobby settings** (gear icon) to configure the match.
- Host can share the **invite code** (QR icon) so others can join quickly.

## 4) Configure Lobby Settings (Host)

- **Round time** — set with an anchored slider.
- **Round count** — choose a preset or enter a custom value. The total scales automatically based on team count.
- **Game modes** — toggle any combination of Sabotage, Survival, and Chain Reaction.
- **Categories** — select which card decks to include. Leaving all unselected uses all categories.

## 5) Start the Match

Host presses **Start Game**. Each round follows this flow:

1. **Ready phase** — the current explainer taps **Start Turn** (in local mode, the device is passed to them first).
2. **Playing phase**:
   - Explainer sees the target word and forbidden words.
   - Explainer marks each card as correct, skipped, or wrong.
   - Opponents can report a forbidden-word violation at any time.
   - If **Sabotage** is enabled, opponents can open the sabotage sheet and inject a new forbidden word (max 20 chars) mid-round.
   - If **Survival** is enabled, the timer adjusts dynamically based on card outcomes.
   - If **Chain Reaction** is enabled, correctly guessed words are added to the forbidden list for the rest of the round.
3. **Round summary** — review all cards and their outcomes.
4. Host continues to the next round, or the game ends when the max round count is reached.

## 6) Game Over

The game-over screen shows:

- final team ranking,
- total points per team,
- card outcome stats (correct / skipped / wrong),
- pace and time stats,
- a button to return home.

## 7) Custom Cards

Open the **Words** tab from the main screen to:

- Browse, edit, or delete existing cards.
- Create new categories with a custom name and icon.
- Add individual cards to any category.
- Bulk import cards and categories via JSON (paste or file picker).

## 8) Troubleshooting

- If joining fails, verify the host IP and that both devices are on the same Wi-Fi network.
- If the host disconnects mid-game, clients are moved to a reconnecting screen and will automatically restore their state when the host is back.
- If no category is selected in lobby settings, the game uses all available categories.