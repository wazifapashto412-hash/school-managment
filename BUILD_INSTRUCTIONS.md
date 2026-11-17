# Build Instructions
1. Install Android Studio (current stable) with JDK 17 and Android SDK 36.
2. File > Open this folder. Let Gradle sync (needs internet once).
   If `gradlew` is missing, run `gradle wrapper --gradle-version 8.11.1` or use Studio's embedded Gradle.
3. Run unit tests: `./gradlew testDebugUnitTest`
4. Debug APK: `./gradlew assembleDebug` -> app/build/outputs/apk/debug/
5. Release signing (keep the key OUTSIDE the project):
   - `keytool -genkeypair -v -keystore ~/school-release.jks -alias school -keyalg RSA -keysize 2048 -validity 10000`
   - Create `keystore.properties` in the project root (git-ignored):
     storeFile=/home/you/school-release.jks / storePassword=... / keyAlias=school / keyPassword=...
   - `./gradlew assembleRelease bundleRelease`
   - APK: app/build/outputs/apk/release/ ; AAB: app/build/outputs/bundle/release/
   - Back up the keystore: losing it means you cannot update the app on Play. (Play App Signing is recommended.)
6. If sync fails on a plugin/library version, update the version numbers in the build files to the latest stable.
