# Baon Tracker

Allowance tracker for college students. Android Studio, Java, Room (SQLite). Works offline.

## How to run
1. Unzip this folder.
2. Open Android Studio, click **Open**, and pick the `BaonTracker` folder.
3. Wait for Gradle sync to finish (needs internet the first time).
4. Pick an emulator or plug in your phone, then click **Run**.

Needs Android Studio Koala (2024.1) or newer. Min Android version is 7.0 (API 24).

## Screens
- **Set Up Account**: first open only. Name, weekly allowance, allowance day, 4 digit PIN.
- **Enter PIN**: every time the app opens.
- **Home**: remaining balance, spent today, daily limit, 3 latest expenses, 80% warning.
- **Expenses**: all expenses by day, filter by category, tap to edit or delete.
- **Add Expense**: amount, category, date, note, with validation.
- **Summary**: total spent this week and a bar per category.
- **Profile**: add allowance, change PIN, warning switch, lock app.

## Project files
- `data/`: Room entities (User, Allowance, Category, Expense), DAOs, and the database
- `util/`: date, money, PIN hashing, and settings helpers
- `ui/`: all activities, fragments, and the list adapter
- `res/values/colors.xml`: the pastel yellow and purple theme colors

## Not done yet
- FR10, export to CSV (Nice to Have)
