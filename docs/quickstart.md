# Quickstart

This is the shortest path to run Unspeakable locally and play a round.

## Prerequisites

- JDK 11 available to Gradle.
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

1. Open **Home** tab.
2. Choose one:
   - **Host a Game** for LAN host.
   - **Join a Game** to connect to host.
   - **Local** for pass-and-play style session on one device.
3. Enter your player name.
4. In the lobby, join a team.
5. Host opens lobby settings and configures:
   - round time,
   - round count,
   - enabled game modes,
   - selected categories.
6. Host starts the game.
7. Explainer presses **Start turn**, then play cards until timer ends.

## Networking Notes

- Host session uses WebSocket server on port `8080`.
- Join clients connect to `ws://<host-ip>:8080/game`.
- Lobby invite code is generated from the host IP in the UI.

