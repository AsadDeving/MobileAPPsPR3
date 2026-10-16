# KMP Project Structure: Practice 3

Kotlin Multiplatform practice project for **Lectures 8–9** (What is KMP · Project structure fundamentals).

📄 **Sections 1–2 (Questions 1–13) are answered in [`ANSWERS.md`](ANSWERS.md).** Hands-on answers link to the code in this repository.

## Modules

```
.
├── shared/          KMP library: Android + iOS (Q5, Q11, Q13)
├── androidApp/      Jetpack Compose app that uses `shared`
├── structure-lab/   Source-set lab: JVM, JS, iOS (Q7, Q8, Q12)
└── gradle/libs.versions.toml   single version catalog
```

## Clean architecture in `shared`

Dependencies point inward only: `presentation → domain ← data → platform`.

```
shared/src/commonMain/kotlin/com/asaddeving/kmppractice/
├── domain/                     pure Kotlin, no platform or framework code
│   ├── model/                  Greeting, WelcomeMessage
│   ├── repository/             PlatformRepository (port / interface)
│   └── usecase/                GetWelcomeMessageUseCase
├── data/repository/            DevicePlatformRepository (implements the port)
├── platform/                   expect declarations: getPlatform(), currentTimestamp()
├── presentation/               WelcomePresenter + WelcomeUiState (shared UI logic)
└── di/                         SharedModule (composition root, manual DI)

shared/src/androidMain/…/platform/   actual implementations for Android
shared/src/iosMain/…/platform/       actual implementations for iOS (device + simulator)
```

Clean-code rules followed:

- **Dependency inversion:** the domain owns the `PlatformRepository` interface, and the data layer implements it.
- **Single responsibility:** one class per job (model, use case, repository, presenter, composition root).
- **Testability:** dependencies are passed in through constructors (`platform`, `clock`), so every layer is unit-tested with fakes in `commonTest`.
- **No magic values:** constants are named (`Greeting.TEXT`, `MILLIS_PER_SECOND`), and all versions live in one version catalog.
- **Thin UI:** `androidApp` only draws `WelcomeUiState`. All logic is shared.

## Build and test

Requirements: JDK 17+, the Android SDK (Android Studio sets it up), and macOS + Xcode for the iOS targets.

```bash
./gradlew :shared:testDebugUnitTest            # commonTest + androidUnitTest
./gradlew :shared:iosSimulatorArm64Test        # commonTest + iosTest (macOS)
./gradlew :androidApp:assembleDebug            # Android app
./gradlew :structure-lab:printSourceSets       # Q8 / Q12
./gradlew :structure-lab:jvmTest :structure-lab:compileKotlinJs   # Q7 / Q12
```

GitHub Actions ([`.github/workflows/build.yml`](.github/workflows/build.yml)) runs all of these on Ubuntu (Android, JVM, JS) and macOS (iOS).
