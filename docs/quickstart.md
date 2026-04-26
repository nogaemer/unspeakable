# Quickstart

This is the shortest path to run Unspeakable locally and play a round.

## Prerequisites

- JDK 21 available to Gradle.
- Android Studio or IntelliJ with Kotlin Multiplatform support.
- At least one device/emulator for Android testing (optional for desktop runs).

## Run on Desktop (JVM)

From the repository root:

```powershell
.\gradlew.bat :composeApp:run
```

## Install on Android (Debug)

```powershell
.\gradlew.bat :composeApp:installDebug
```

## Fast Play Flow

1. Open the **Home** tab.
2. Choose one:
   - **Host a Game** — create a LAN lobby others can join.
   - **Join a Game** — connect to an existing host.
   - **Local** — pass-and-play on a single device.
3. Enter your player name.
4. In the lobby, join a team. In local mode, the host can add extra players and move them between teams.
5. Host opens lobby settings and configures:
   - round time,
   - round count,
   - enabled game modes (Sabotage, Survival, Chain Reaction),
   - selected card categories.
6. Host starts the game.
7. The explainer presses **Start Turn**, then play cards until the timer ends.

## Networking Notes

- Host session uses a WebSocket server on port `8080`.
- Join clients connect to `ws://<host-ip>:8080/game`.
- The lobby invite code is generated from the host IP in the UI.
- If a client disconnects mid-game, they can rejoin and resume from their last state.