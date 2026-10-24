# Contributing to বর্ণ-সুডোকু (Borno-Sudoku)

Thank you for your interest in contributing to **বর্ণ-সুডোকু (Borno-Sudoku)**! We welcome contributions ranging from bug fixes and code optimizations to new Bengali puzzles and design enhancements.

---

## 🛠️ Development Setup

1. **Prerequisites**:
   * Android Studio (Ladybug or newer)
   * JDK 17 or higher
   * Android SDK (API Level 36)

2. **Fork and Clone**:
   ```bash
   git clone https://github.com/your-username/borno-sudoku.git
   cd borno-sudoku
   ```

3. **Verify Setup**:
   Run the test suite to ensure your environment is configured correctly:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 📋 Contribution Guidelines

### Adding New Bengali Puzzles
* Puzzles are located in `app/src/main/java/com/example/data/BengaliWordPuzzles.kt`.
* Every puzzle must follow standard Sudoku constraints (no duplicates in rows, columns, or blocks).
* For **Word Secret** puzzles, the letters must form a meaningful Bengali word or phrase, accompanied by Bengali definition and hint text.

### Code Style
* Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and [Android Jetpack Compose Guidelines](https://developer.android.com/develop/ui/compose/architecture).
* Use Jetpack Compose Material 3 components and centralized theming from `Theme.kt`.
* Avoid hardcoded strings in user-facing UI; use descriptive Bengali copy.

### Submitting a Pull Request
1. Branch from `main` with a descriptive name: `feature/new-word-puzzles` or `fix/grid-padding`.
2. Make your changes in concise, logical commits.
3. Ensure all tests pass before pushing:
   ```bash
   ./gradlew testDebugUnitTest assembleDebug
   ```
4. Push your branch and open a Pull Request against `main`. Fill in the PR template with a summary of changes and testing notes.
