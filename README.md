# Unspeakable

A modernized, digital "forbidden-word" party game designed specifically for chaotic in-person play. Built for the [Hack Club Flavortown](https://flavortown.hackclub.com/) kitchen.

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)

## About the Project

I am building this game to create party moments that physical cards just can't replicate. While you can play it normally on a single device (Pass & Play), the main twist is a **local multiplayer mode** where each team uses their own phone. 

By connecting multiple devices in the same room, the app ensures each team sees different information: The describer’s view is completely different from the opposing team’s

### Game Modes
* **Classic:** Get your team to guess the word without saying the 5 forbidden words.
* **Sabotage:** The opposing team can inject new forbidden words mid-round to mess with the describer in real-time.
* **Survival:** The round timer speeds up on wrong guesses and slows down on correct ones — stay alive as long as possible.
* **Chain Reaction:** Every correctly guessed word becomes an additional forbidden word for the rest of the round.
* **Minefield** *(planned):* The describer only sees 3 of their forbidden words, but the opposing team sees all of them and waits with a digital buzzer to catch them stepping on an invisible mine.
## Tech Stack

This project is built completely from scratch using modern Kotlin architecture:
* **[Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html):** Sharing logic, state machines, and networking across devices.
* **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/):** A fully custom, highly expressive Material 3 UI featuring variable fonts (`Roboto Flex`).
* **[Room (SQLite)](https://developer.android.com/kotlin/multiplatform/room):** Embedded local database to store and randomly generate thousands of game cards.
* **[Ktor](https://ktor.io/):** (In Progress) Using Ktor's embedded server and WebSockets to allow one phone to act as the "Host" while others join the local network.

## Platforms & Compiling

The architecture is fully cross-platform! Currently, I am actively developing and testing on:
* ✅ **Android**
* ✅ **Desktop (JVM)**

*Note on iOS:* The codebase is written to support iOS natively via Kotlin Multiplatform. However, because I don't currently have a Mac, I cannot compile or test the `.ipa` build. 

## How to Run (Development)

Clone the repository and open it in **Android Studio** or **IntelliJ**.

**To run on Android:**
```bash
./gradlew :composeApp:installDebug
```

**To run on Desktop (JVM):**
```bash
./gradlew :composeApp:run
```

## Documentation

- `docs/README.md` - docs index.
- `docs/quickstart.md` - fast local run and first match setup.
- `docs/user_tutorial.md` - full user walkthrough.

## Credits & Acknowledgements

### AI Usage
- **[GitHub Copilot](https://github.com/features/copilot)** — code completion throughout the project.
- **[Perplexity](https://www.perplexity.ai)** — research, debugging assistance, and README formatting.
- All target words and forbidden word sets in the game were generated using Perplexity.

### Open Source Libraries

| Library | Author | License |
|---|---|---|
| [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) | JetBrains | Apache 2.0 |
| [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) | JetBrains | Apache 2.0 |
| [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) | JetBrains | Apache 2.0 |
| [Ktor](https://github.com/ktorio/ktor) | JetBrains | Apache 2.0 |
| [Material 3](https://m3.material.io) | Google | Apache 2.0 |
| [AndroidX Room](https://developer.android.com/jetpack/androidx/releases/room) | Google | Apache 2.0 |
| [AndroidX Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) | Google | Apache 2.0 |
| [Decompose](https://github.com/arkivanov/Decompose) | Arkadii Ivanov | Apache 2.0 |
| [Lucide Icons](https://lucide.dev) | Lucide Contributors | ISC |
| [compose-icons (lucide-cmp)](https://github.com/composablehorizons/compose-icons) | Composable Horizons | ISC |
| [MaterialKolor](https://github.com/jordond/materialkolor) | Jordon Boyd | MIT |
| [Lyricist](https://github.com/adrielcafe/lyricist) | Adriel Café | MIT |
| [QR Kit](https://github.com/ChainTechNetwork/QRKitComposeMultiplatform) | ChainTech Network | MIT |
| [colormath](https://github.com/ajalt/colormath) | AJ Alt | MIT |
| [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) | Russell Wolf | Apache 2.0 |
| [Kermit](https://github.com/touchlab/Kermit) | Touchlab | Apache 2.0 |
| [SQLDelight](https://github.com/cashapp/sqldelight) | Cash App | Apache 2.0 |