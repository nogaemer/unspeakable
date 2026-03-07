# Unspeakable

A modernized, digital "forbidden-word" party game designed specifically for chaotic in-person play. Built for the [Hack Club Flavortown](https://flavortown.hackclub.com/) kitchen.

![Work in Progress](https://img.shields.io/badge/Status-Work_In_Progress-orange)
![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)

## About the Project

I am building this game to create party moments that physical cards just can't replicate. While you can play it normally on a single device (Pass & Play), the main twist is a **local multiplayer mode** where each team uses their own phone. 

By connecting multiple devices in the same room, the app ensures each team sees different information: The describer’s view is completely different from the opposing team’s (Currently in development)

### Planned Game Modes
* **Classic Mode:** The standard game. Get your team to guess the word without saying the 5 forbidden words.
* **Minefield Mode:** The describer only sees 3 of their forbidden words, but the opposing team sees all of them. The opposing team waits with a digital buzzer on their screen to catch the describer stepping on an "invisible mine."
* **Sabotage:** The opposing team can type new forbidden words into their phone mid-round to mess with the describer in real-time.

## Tech Stack

This project is built completely from scratch using modern Kotlin architecture:
* **[Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html):** Sharing logic, state machines, and networking across devices.
* **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/):** A fully custom, highly expressive Material 3 UI featuring variable fonts (`Roboto Flex`).
* **[Room (SQLite)](https://developer.android.com/kotlin/multiplatform/room):** Embedded local database to store and randomly generate thousands of game cards.
* **[Ktor](https://ktor.io/):** (In Progress) Using Ktor's embedded server and WebSockets to allow one phone to act as the "Host" while others join the local network.

## 📱 Platforms & Compiling

The architecture is fully cross-platform! Currently, I am actively developing and testing on:
* ✅ **Android**
* ✅ **Desktop (JVM)**

*Note on iOS:* The codebase is written to support iOS natively via Kotlin Multiplatform. However, because I don't currently have a Mac, I cannot compile or test the `.ipa` build. 

## How to Run (Development)

Clone the repository and open it in **Android Studio** or **IntelliJ**.

**To run on Android:**
```bash
./gradlew :composeApp:installDebug
