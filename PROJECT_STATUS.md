# PROJECT_STATUS

Last updated: 2026-11-15

## Build status: NOT BUILT. NOT COMPILED. NOT TESTED.
The authoring environment had no network, Gradle, or Kotlin compiler. Everything here is unverified source.
Open in Android Studio, sync, and fix any compile issues before continuing.

## Stages
- [x] 1 Architecture and folder structure (written, unverified)
- [x] 2 Database design: all 20 entities, Room DB v1, UserDao, SettingsDao (written, unverified)
- [x] 3 Login, roles, permissions, session timeout, first-run setup (written, unverified)
- [~] 4 Dashboard/navigation: role-filtered tiles only; tiles are placeholders
- [ ] 5 Students  [ ] 6 Classes/sections  [ ] 7 Attendance  [ ] 8 Fees  [ ] 9 Exams
- [ ] 10 Staff  [ ] 11 Reports/PDF  [ ] 12 Backup/restore  [ ] 13 UI polish/language switch
- [ ] 14 Testing  [ ] 15 APK/AAB (must be built by you)  [ ] 16 Final packaging

## Design decisions
- Money stored as Long (rupees x 100) to avoid float errors; dates ISO text.
- Fee payments use allocations table (payment -> charge) so partial payments/balances are exact.
- Unique index (studentId, date) prevents duplicate attendance.
- No default password: Super Admin is created on first launch.
- allowBackup=false, zero permissions, no network.
- Missing: gradle-wrapper.jar (generate with `gradle wrapper` or let Android Studio create it).
