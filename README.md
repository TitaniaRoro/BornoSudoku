# বর্ণ-সুডোকু (Borno-Sudoku) — Bengali Word & Sudoku Puzzle

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue.svg)](https://developer.android.com/)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue.svg)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> **বাংলা বর্ণমালার সৌন্দর্য ও সুডোকুর বুদ্ধিবৃত্তিক কৌশলের এক অপূর্ব মিলন!**  
> An engaging, brain-teasing Bengali word and letter puzzle game built with modern Android, Jetpack Compose, and Material Design 3.

---

## 🌟 Highlights & Features

### 🧩 1. Multiple Grid Sizes & Game Modes
* **৬×৬ ক্লাসিক সুডোকু (6×6 Classic Wordoku)**:
  * 6 distinct Bengali consonants: **ক, খ, গ, ঘ, ঙ, চ**
  * Sub-grids composed of 2 rows × 3 columns.
* **শব্দ সুডোকু (6×6 Word Secret Mode)**:
  * Letters in each puzzle rearrange to spell iconic Bengali words like **বাংলাদেশ**, **আনন্দ**, **সূর্য**!
  * Solving the grid unveils the hidden Bengali word, its definition, and cultural trivia.
* **৪×৪ মিনি সুডোকু (4×4 Mini Mode)**:
  * Designed for children, beginners, or rapid mental warm-ups using 4 vowels (**অ, আ, ই, ঈ**) or consonants (**ক, খ, গ, ঘ**).
* **৯×৯ মাস্টার সুডোকু (9×9 Master Wordoku)**:
  * Full 3×3 blocks using 9 Bengali letters (**ক** through **ঝ**) for Sudoku masters.
* **শব্দ সন্ধান (Word Connect / Anagram Minigame)**:
  * Form meaningful Bengali words from dynamic circular letter tiles to uncover words and their meanings (e.g. *কলম*, *কমল*, *নদী*, *মধু*).

---

### ⌨️ 2. Native On-Screen Bengali Keypad
* **No external Bengali keyboard needed**: Players can seamlessly tap Bengali letters directly on any device.
* **Remaining Count Badges**: Real-time counter showing how many occurrences of each letter remain to be placed.
* **Auto-completion Dimming**: Letters that have all occurrences placed turn subtle green with a checkmark badge.

---

### 🧠 3. Advanced Strategy & Assistant Tools
* **টীকা / নোট (Pencil Notes Mode)**: Toggle miniature candidate annotations inside cells for deep Sudoku deduction.
* **ভুল শনাক্তকরণ (Auto-Conflict Detection)**: Instant visual indication if a duplicate letter violates a row, column, or block.
* **একই বর্ণ হাইলাইট (Same Letter Highlight)**: Highlights all instances of the selected character across the board.
* **পূর্বাবস্থা (Undo)** & **মুছুন (Erase)**: Step back through previous moves with a single tap.
* **বুদ্ধিদীপ্ত ইঙ্গিত (Smart Hints)**: Provides targeted cell assistance with remaining hint tracker.
* **টাইমার ও বিরতি (Timer & Pause)**: Displays elapsed time formatted in authentic Bengali numerals (`০১:৪৫`).

---

### 🏆 4. Gamification, Points & Player Ranks
* **Dynamic XP & Scoring**: Earn XP based on difficulty, completion time, mistake-free runs, and hint-free solves.
* **Player Ranks (পদবী)**:
  1. **লেভেল ১**: শিক্ষানবিস (Novice)
  2. **লেভেল ২**: বর্ণসন্ধানী (Letter Explorer)
  3. **লেভেল ৩**: ধাঁধা সাধক (Puzzle Adept)
  4. **লেভেল ৪**: সুডোকু পণ্ডিত (Sudoku Scholar)
  5. **লেভেল ৫**: শব্দ জাদুকর (Word Wizard)
  6. **লেভেল ৬**: বর্ণসম্রাট (Wordoku Emperor)
* **XP Progress Bar**: Visual progress tracking to level up.
* **Badges & Achievements (পদক ও অর্জন)**:
  * 🏆 **প্রথম বিজয় (First Victory)**
  * ⭐ **নিখুঁত সমাধান (Flawless Mind)**
  * ⚡ **গতি তারকা (Speed Star)**
  * 🧠 **শব্দ বিশারদ (Word Wizard)**
  * 🔥 **ধারাবাহিক বীর (Streak Champion)**
  * 🛡️ **স্বয়ংসম্পূর্ণ (Self Reliant)**
  * 👑 **৯×৯ মাস্টারমাইন্ড (9×9 Mastermind)**
  * 🌟 **পয়েন্ট সংগ্রাহক (Point Collector)**

---

### 📲 5. Social Media Sharing & Friend Challenge
* **Share Result Sheet**: Direct integration with the Android native share sheet to export formatted victory cards with completion time, streak, and scores.
* **Challenge a Friend**: One-tap invitation link for friends to test their Bengali vocabulary and Sudoku logic against your high scores.

---

### 🎨 6. Typography & Visual Identity
* Bundled local **Hind Siliguri** Google Font for authentic, crisp Bengali script rendering.
* Custom adaptive launcher icon with Material You styling.
* Cheerful celebration confetti effects upon puzzle completion.

---

## 🛠️ Architecture & Tech Stack

The application follows Google's recommended **Clean Architecture / MVVM (Model-View-ViewModel)** guidelines:

* **Language**: [Kotlin](https://kotlinlang.org/) 100%
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
* **Architecture**: MVVM with `ViewModel` and Kotlin Coroutines / `StateFlow`
* **Concurrency**: Kotlin Coroutines & `asStateFlow`
* **Persistence**: SharedPreferences / Data Layer via `GamePreferences`
* **Unit Testing**: JUnit 4 & [Robolectric](https://robolectric.org/) for fast JVM-based tests without an emulator

```
app/src/main/java/com/example/
├── MainActivity.kt               # Navigation router & main activity
├── model/
│   └── PuzzleModels.kt           # Data structures for grids, cells, badges, and scores
├── data/
│   ├── BengaliWordPuzzles.kt     # Solvable boards, anagrams, scoring engine & share helpers
│   └── GamePreferences.kt        # Persistence for stats, streaks, XP, and badges
├── viewmodel/
│   └── GameViewModel.kt          # Business logic, state management, timer & validation
└── ui/
    ├── theme/                    # Color, Type (Hind Siliguri), and Theme definitions
    ├── components/               # SudokuGrid, BengaliKeypad, Confetti, Victory & Rules dialogs
    └── screens/                  # HomeScreen, GameScreen, WordHuntScreen, StatsScreen
```

---

## 🚀 Getting Started & Building

### Prerequisites
* **Android Studio** (Ladybug or newer recommended)
* **JDK 17** or **JDK 21**
* **Android SDK**: `minSdk 24`, `targetSdk 36`, `compileSdk 36`

### Cloning the Repository
```bash
git clone https://github.com/your-username/borno-sudoku.git
cd borno-sudoku
```

### Running Tests
Execute JVM and Robolectric unit tests:
```bash
./gradlew testDebugUnitTest
```
*(On Windows: `gradlew.bat testDebugUnitTest`)*

### Building the Debug APK
```bash
./gradlew assembleDebug
```
The generated APK will be available at `app/build/outputs/apk/debug/app-debug.apk`.

### Installing on Device
Connect an Android device or start an emulator, then run:
```bash
./gradlew installDebug
```

---

## 🧪 Unit & Robolectric Tests

The repository contains automated unit tests testing:
* Board completion validation and conflict detection
* Bengali numeral parsing and formatting (`১, ২, ৩...` and `০১:৪৫`)
* XP scoring and bonus calculations
* Badges unlocking triggers and player rank evaluations
* Social media share text generators

Run tests with:
```bash
./gradlew test
```

---

## 🤝 Contributing

Contributions are warmly welcome! Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) for details on our code of conduct, development setup, and pull request process.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [`LICENSE`](LICENSE) file for details.
