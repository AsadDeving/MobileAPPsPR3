# Practice 3: Kotlin Multiplatform Project Structure (Answers)

Answers to all 25 questions in *Practice 3: KMP Project Structure* (Lectures 8–11).

- **Theoretical** questions are answered here.
- **Hands-on** questions are answered here **and** implemented in this repository. Every hands-on answer links to the real files and says how to check them.

| Module | Used for |
|---|---|
| [`shared/`](shared) | Q5, Q11, Q13: KMP module for Android and iOS, laid out with clean architecture |
| [`androidApp/`](androidApp) | Android app that uses `shared` (shows the shared code being used) |
| [`structure-lab/`](structure-lab) | Q8, Q12, Q16, Q17, Q21: source-set and compilation exercises (JVM, JS, Linux, iOS) |

Versions: Kotlin 2.2.20, Gradle 8.14.3, AGP 8.11.1.

---

## Contents

- [Section 1: What Is Kotlin Multiplatform (Lecture 8)](#section-1--what-is-kotlin-multiplatform-lecture-8): Q1–Q6
- [Section 2: KMP Project Structure Fundamentals (Lecture 9)](#section-2--kmp-project-structure-fundamentals-lecture-9): Q7–Q13
- [Section 3: KMP Advanced Project Structure (Lecture 10)](#section-3--kmp-advanced-project-structure-lecture-10): Q14–Q21
- [Section 4: Choosing a KMP Project Configuration (Lecture 11)](#section-4--choosing-a-kmp-project-configuration-lecture-11): Q22–Q25

---

## Section 1 – What Is Kotlin Multiplatform (Lecture 8)

### Question 1 (Theoretical): KMP is neither a bridge nor a VM

KMP is a **compiler technology**. Shared Kotlin code is compiled natively for each platform, with no bridge or virtual machine in between. The lecture ("Not a bridge, not a VM") compares three approaches:

| Technology | How it reaches the platform |
|---|---|
| **Flutter** | Renders **its own widget tree through Skia/Impeller**. It does not use native platform views. |
| **React Native** | **Talks to native views across a JavaScript bridge** (or the newer JSI), so a translation layer runs at runtime. |
| **Kotlin Multiplatform** | Has no middle layer. **Kotlin/Native uses LLVM to produce real ARM64 binaries for iOS**, and on Android the same code compiles to JVM bytecode. The UI can stay fully native or be shared with Compose Multiplatform. |

A polite way to put it to the teammate: *"The goal is the same (less duplicated code), but KMP isn't a bridge or a VM. The shared code compiles straight to native code on each platform, and each app can keep its own native UI."*

### Question 2 (Theoretical): "Two codebases, one product"

**The problem:** Android and iOS teams often build the same logic twice, in two languages. The Android team writes validation, networking and business rules in Kotlin. The iOS team writes the same validation, networking and business rules again in Swift.

**The three costs the slide lists:**

1. **Duplicated bugs.** The same bug gets written, and fixed, twice.
2. **Inconsistent behaviour between platforms.** The two versions drift apart.
3. **Double the QA effort** for every business-logic change.

**KMP's answer:** write the shared logic once in Kotlin (`commonMain`) and compile it natively for both apps.

### Question 3 (Theoretical): What can realistically be shared

**Typical share:** most production teams share **60–80 % of the codebase**: *"the logic, not always the pixels."*

| Typically shared (`commonMain`) | Usually kept native (and why) |
|---|---|
| **Networking / API clients**, e.g. Ktor as one shared HTTP client | **Camera, biometrics, sensors**: they need deep integration with the platform's hardware |
| **Validation and business rules**: they must behave the same on both platforms | **Push notifications**: each platform has its own delivery service (FCM / APNs) |
| Local storage and caching (SQLDelight, Room KMP) | Platform UI, unless you adopt Compose Multiplatform |
| Shared ViewModels / state | Some navigation: platform-style transitions and deep links |

### Question 4 (Theoretical): Platform stability

| Status | Targets |
|---|---|
| **Stable** | Android · iOS (Arm64 + Simulator) · Desktop (Windows, macOS, Linux) · Server/backend (e.g. with Ktor) · Web via **Kotlin/JS** |
| **Beta** | Web via **Kotlin/Wasm** |

**What "Beta" means for production:** the target works but is not final ("still maturing"). APIs and behaviour can still change, so expect rough edges and possible migration work in later releases. If you ship a production feature on it, put it behind an abstraction, pin versions and budget time for upgrades. Keep anything business-critical on a Stable target.

### Question 5 (Hands-on): `expect fun getPlatform(): Platform`

The project targets Android (`androidTarget()`) and iOS (`iosArm64()`, `iosSimulatorArm64()`). See [`shared/build.gradle.kts`](shared/build.gradle.kts).

**Common declaration:** [`shared/src/commonMain/.../platform/Platform.kt`](shared/src/commonMain/kotlin/com/asaddeving/kmppractice/platform/Platform.kt)

```kotlin
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
```

**Android actual:** [`shared/src/androidMain/.../platform/Platform.android.kt`](shared/src/androidMain/kotlin/com/asaddeving/kmppractice/platform/Platform.android.kt)

```kotlin
class AndroidPlatform : Platform {
    override val name: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()
```

**iOS actual:** [`shared/src/iosMain/.../platform/Platform.ios.kt`](shared/src/iosMain/kotlin/com/asaddeving/kmppractice/platform/Platform.ios.kt)

```kotlin
class IOSPlatform : Platform {
    override val name: String = "iOS"
}

actual fun getPlatform(): Platform = IOSPlatform()
```

**Proof that both actuals resolve:** the tests [`AndroidPlatformTest`](shared/src/androidUnitTest/kotlin/com/asaddeving/kmppractice/platform/AndroidPlatformTest.kt) and [`IOSPlatformTest`](shared/src/iosTest/kotlin/com/asaddeving/kmppractice/platform/IOSPlatformTest.kt) call `getPlatform()` and check the name. The [CI workflow](.github/workflows/build.yml) runs them with `./gradlew :shared:testDebugUnitTest` (Ubuntu) and `./gradlew :shared:iosSimulatorArm64Test` (macOS).

**Screenshot:** open the three files above in Android Studio, split the editor (*Window ▸ Editor Tabs ▸ Split Right*) and take the screenshot. The expect/actual gutter icons appear and nothing is underlined in red.

*Design note:* the `expect` is on a **function**, and `Platform` is an ordinary interface. The lecture notes that `expect`/`actual` **classes are still Beta**, while `expect` functions are stable.

### Question 6 (Theoretical): Kotlin Multiplatform vs Compose Multiplatform

- **Kotlin Multiplatform** shares **logic**: networking, data, business rules and state. The UI can stay 100 % native (Jetpack Compose on Android, SwiftUI on iOS). This repository does exactly that: [`androidApp`](androidApp) draws its own Compose UI from state produced by the shared [`WelcomePresenter`](shared/src/commonMain/kotlin/com/asaddeving/kmppractice/presentation/WelcomePresenter.kt).
- **Compose Multiplatform** is JetBrains' declarative UI framework. It is **built on top of the KMP foundation** and also lets you **share the UI layer**.
- **Stability:** Compose Multiplatform is **Stable on Android, iOS and desktop**. The **web (Wasm) target is Beta**.
- **Why it only works one way:** Compose Multiplatform code lives in KMP source sets and reaches each platform through KMP targets, so it cannot exist without KMP. KMP, however, works fine with native UIs. In the slide's words: *"You can use Kotlin Multiplatform without Compose Multiplatform, but not the other way around."*

So it is not a separate tool. It is a layer you can add on top of a KMP project.

---

## Section 2 – KMP Project Structure Fundamentals (Lecture 9)

### Question 7 (Theoretical): Why `java.io.File` fails in `commonMain`

**Common code** is the Kotlin code shared across platforms. It lives in `commonMain` and is **one file compiled separately for every declared target**, not copied per platform. Where a file lives decides which platforms it compiles to.

`java.io.File` is part of the **JDK**. `commonMain` also compiles to targets that have no JDK (Kotlin/Native for iOS, Kotlin/JS), so the compiler cannot resolve the reference and the IDE marks it as unresolved. A plain JVM project has only one target, the JVM, so the same import works there.

**Rule:** `commonMain` can only use APIs that exist on **every** target it compiles to. Platform-only APIs belong in a platform source set. Even multiplatform libraries have limits: `kotlinx.coroutines` works in `commonMain`, but its JVM-only extra `CoroutineDispatcher.asExecutor()` does not.

In this repo, [`structure-lab/src/jvmMain/.../JvmGreeting.kt`](structure-lab/src/jvmMain/kotlin/com/asaddeving/structurelab/JvmGreeting.kt) uses `java.io.File` without problems, because `jvmMain` compiles only to the JVM.

### Question 8 (Hands-on): `jvm()` + `iosArm64()` source sets

Lab file: [`structure-lab/build.gradle.kts`](structure-lab/build.gradle.kts). Step 1 of the lab declares exactly the two targets from the lecture:

```kotlin
kotlin {
    jvm()
    iosArm64()
}
```

To list the registered source sets without relying on the IDE tree, the module has a task:

```bash
./gradlew :structure-lab:printSourceSets
```

**Recorded source set names (Kotlin 2.2.20, with only `jvm()` + `iosArm64()`):**

```
commonMain, commonTest, iosArm64Main, iosArm64Test, jvmMain, jvmTest
```

That is `commonMain` (the one predefined source set) plus one `…Main` source set per declared target, each with its `…Test` partner. Declaring a target attaches a "label" to every common file.

> Observation: with a **single** Apple target, Kotlin 2.2.20 did **not** create `iosMain`/`appleMain`/`nativeMain`. The default hierarchy template only creates those intermediate source sets once they have something to group (see Q17, where a second iOS target is added). In the Project view a source set only appears as a folder once `src/<name>/kotlin` exists.

### Question 9 (Theoretical): What makes a source set

**The four defining properties of a source set:**

1. It has a **name** that is unique within the project.
2. It contains **source files**, usually in a directory with the same name (`src/<name>/kotlin`).
3. It specifies **which targets** its code compiles to.
4. It has its **own dependencies and compiler options**.

**Root vs platform (leaf) source set:**

- **`commonMain` is the root.** It is predefined in every KMP project and compiles to **all** declared targets, and every other source set branches off it. That is why it can only use APIs available everywhere.
- **`androidMain` is a leaf (platform) source set.** It compiles to **one** target only, so it can use platform-only APIs (Android SDK, `java.*`). It sits at the bottom of the tree: *"shared code up top, platform code at the leaves."*

Between them are **intermediate** source sets (e.g. `appleMain`, `iosMain`), which compile to some targets but not all.

### Question 10 (Theoretical): The merge rule and the four golden rules

**Merge rule:** to compile for a target, Kotlin gathers **every source set labelled for that target** and compiles them together as one unit. For `iosArm64` that chain is `commonMain` + `appleMain` (+ `iosMain`) + `iosArm64Main`, merged into one native binary. Source sets without that label (e.g. `jsMain`) are left out.

**The four golden rules:**

1. **Declare a target** before Kotlin will compile anything to it.
2. Code shared by everything goes in **`commonMain`**.
3. Code for one target goes in **that target's own source set**.
4. **Platform source sets can see `commonMain`, but `commonMain` can never see into them.**

**Direction of information flow:** one way only, from general (`commonMain`) down to specific (platform source sets). Never the reverse. That is why the shared code in this repo reaches platform APIs only through `expect`/`actual`.

### Question 11 (Hands-on): `expect fun currentTimestamp(): Long`

**Common:** [`shared/src/commonMain/.../platform/Timestamp.kt`](shared/src/commonMain/kotlin/com/asaddeving/kmppractice/platform/Timestamp.kt)

```kotlin
/** Milliseconds since 1970-01-01 UTC, read from each platform's own clock. */
expect fun currentTimestamp(): Long
```

**Android:** [`Timestamp.android.kt`](shared/src/androidMain/kotlin/com/asaddeving/kmppractice/platform/Timestamp.android.kt)

```kotlin
actual fun currentTimestamp(): Long = System.currentTimeMillis()
```

**iOS:** [`Timestamp.ios.kt`](shared/src/iosMain/kotlin/com/asaddeving/kmppractice/platform/Timestamp.ios.kt)

```kotlin
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

private const val MILLIS_PER_SECOND = 1_000

actual fun currentTimestamp(): Long = (NSDate().timeIntervalSince1970 * MILLIS_PER_SECOND).toLong()
```

**Gotcha:** `NSDate().timeIntervalSince1970` returns **seconds as a `Double`**, while `System.currentTimeMillis()` returns **milliseconds as a `Long`**. Without `* 1000` and `.toLong()` the iOS actual would not match the `Long` signature (compile error), and the value would be 1000× too small. [`IOSPlatformTest.timestampIsInMillisecondsNotSeconds`](shared/src/iosTest/kotlin/com/asaddeving/kmppractice/platform/IOSPlatformTest.kt) guards against that.

**Building for both targets:**

```bash
./gradlew :shared:compileDebugKotlinAndroid          # Android
./gradlew :shared:compileKotlinIosSimulatorArm64     # iOS (macOS only)
```

The iOS actual lives in the intermediate **`iosMain`** source set, so both `iosArm64` (device) and `iosSimulatorArm64` (simulator) use it. Neither target reports an "expected declaration has no actual" error.

### Question 12 (Hands-on): Adding `js()`

Step 2 of the lab adds a third target:

```kotlin
kotlin {
    jvm()
    iosArm64()
    js { browser() }   // on older Kotlin versions: js(IR) { browser() }
}
```

**Recorded output of `printSourceSets` after re-syncing:**

```
commonMain, commonTest, iosArm64Main, iosArm64Test, jsMain, jsTest, jvmMain, jvmTest
```

**`jsMain` and `jsTest` appeared without any manual wiring.** Declaring the target is enough: the Kotlin Gradle plugin creates the matching source sets and links them to `commonMain` itself. The source code did not change. The common files simply gained a third label, so they now compile three ways (JVM class files, a native binary and JavaScript).

In the repo, [`structure-lab/src/jsMain/.../JsGreeting.kt`](structure-lab/src/jsMain/kotlin/com/asaddeving/structurelab/JsGreeting.kt) lives in that source set. `./gradlew :structure-lab:compileKotlinJs` builds it.

### Question 13 (Hands-on): `Greeting` + `GreetingTest`

**Main:** [`shared/src/commonMain/.../domain/model/Greeting.kt`](shared/src/commonMain/kotlin/com/asaddeving/kmppractice/domain/model/Greeting.kt)

```kotlin
class Greeting {
    fun greet(): String = TEXT

    companion object {
        const val TEXT = "Hello, Kotlin Multiplatform!"
    }
}
```

**Test (same package, `commonTest`):** [`shared/src/commonTest/.../domain/model/GreetingTest.kt`](shared/src/commonTest/kotlin/com/asaddeving/kmppractice/domain/model/GreetingTest.kt)

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals

class GreetingTest {
    @Test
    fun greetReturnsExpectedText() {
        assertEquals("Hello, Kotlin Multiplatform!", Greeting().greet())
    }
}
```

`kotlin.test` is added once, in `commonTest.dependencies { implementation(libs.kotlin.test) }` ([`shared/build.gradle.kts`](shared/build.gradle.kts)).

**Running it:** click the green gutter icon next to `GreetingTest` in Android Studio, or run

```bash
./gradlew :shared:testDebugUnitTest        # Android/JVM
./gradlew :shared:iosSimulatorArm64Test    # iOS simulator (macOS)
```

Result: **passed**. The full shared suite, 6 tests (`GreetingTest`, `GetWelcomeMessageUseCaseTest`, `DevicePlatformRepositoryTest`, `WelcomePresenterTest`, and 2 tests in `AndroidPlatformTest`), ran green with 0 failures.

**Main/Test pairing:** every `…Main` source set has a `…Test` partner that is wired to it automatically. `commonTest` sees `commonMain` without any extra configuration and compiles to every target, so one test runs on every platform.

---

