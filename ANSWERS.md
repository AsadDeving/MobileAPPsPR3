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

## Section 3 – KMP Advanced Project Structure (Lecture 10)

### Question 14 (Theoretical): `dependsOn` is not `dependencies {}`

**Why the claim is wrong:** `dependsOn` links **two source sets inside your own project** and builds the source-set hierarchy. The `dependencies {}` block brings in **libraries or other Gradle projects**. They are different relations, and `dependsOn` cannot point at a published library.

**When `A.dependsOn(B)` is declared, all four of these hold at once:**

1. **A sees B's API**, including its `internal` declarations.
2. **A can provide `actual` implementations** for B's `expect` declarations. Only source sets that depend on B, directly or indirectly, can do this.
3. **B must compile to every target A compiles to**, in addition to its own.
4. **A inherits all of B's regular dependencies.**

**Kind of relation:** `dependsOn` is a **visibility / actualization** relation, a compile-time view of the code. It is **not** a packaging or build relation: nothing is bundled or published because of it.

### Question 15 (Theoretical): Two misuses of `dependsOn`

- **`commonTest.dependsOn(commonMain)`:** `dependsOn` is reserved for building the main/test source-set **hierarchy** (platform → intermediate → common), and it also implies target inclusion and the right to provide actuals. Test code **already sees** production code, because the `test` compilation is *associated* with the `main` compilation. The call adds nothing useful, and it would wrongly tangle the test and production hierarchies.
- **`dependsOn` on a library's own `commonMain`:** the library's source sets are not part of your project. You only get its **compiled, published artifacts**. `dependsOn` cannot declare a dependency on a published library or another Gradle project.

**The right tool is the `dependencies {}` block**, inside the source set that needs it:

```kotlin
commonMain.dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2") // a library
    implementation(project(":other-module"))                              // another module
}
```

### Question 16 (Hands-on): Custom `jvmAndJsMain` source set

Implemented in [`structure-lab/build.gradle.kts`](structure-lab/build.gradle.kts):

```kotlin
kotlin {
    jvm()
    iosArm64()
    js { browser() }
    linuxX64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate() // see Q17

    sourceSets {
        val jvmAndJsMain by creating {
            dependsOn(commonMain.get())
        }
        jvmMain.get().dependsOn(jvmAndJsMain)
        jsMain.get().dependsOn(jvmAndJsMain)
    }
}
```

**Shared function moved into the new source set:** [`structure-lab/src/jvmAndJsMain/.../JvmAndJsGreeting.kt`](structure-lab/src/jvmAndJsMain/kotlin/com/asaddeving/structurelab/JvmAndJsGreeting.kt)

```kotlin
fun jvmAndJsGreeting(): String = "Shared by JVM and JS only"
```

| Caller | Result |
|---|---|
| [`jvmMain/JvmGreeting.kt`](structure-lab/src/jvmMain/kotlin/com/asaddeving/structurelab/JvmGreeting.kt) | ✅ compiles. `JvmGreetingTest` checks `"Shared by JVM and JS only (JVM)"` |
| [`jsMain/JsGreeting.kt`](structure-lab/src/jsMain/kotlin/com/asaddeving/structurelab/JsGreeting.kt) | ✅ compiles (`compileKotlinJs`) |
| [`linuxX64Main/LinuxGreeting.kt`](structure-lab/src/linuxX64Main/kotlin/com/asaddeving/structurelab/LinuxGreeting.kt) | ❌ would be *Unresolved reference*: `linuxX64` is not wired to `jvmAndJsMain`, so it only uses `commonGreeting()` |

**Verified:** `./gradlew :structure-lab:jvmTest :structure-lab:compileKotlinJs` → **BUILD SUCCESSFUL**. `linuxX64` is compiled and tested in CI.

**Why the three `dependsOn` calls are needed:** Kotlin has no built-in source set for the JVM + JS pair. A source set created with `by creating` starts out isolated and has no target labels. The edges give it a parent (`commonMain`) and children (`jvmMain`, `jsMain`), and those children are what give it its labels.

### Question 17 (Hands-on): Manual `dependsOn` vs the default hierarchy template

Step 4 of the lab adds `iosArm64()` + `iosSimulatorArm64()` to the Q16 project. Here is what `printSourceSets` showed in both situations:

**A. With the manual `jvmAndJsMain` wiring and no `applyDefaultHierarchyTemplate()`:**

```
commonMain, commonTest, iosArm64Main, iosArm64Test, iosSimulatorArm64Main, iosSimulatorArm64Test,
jsMain, jsTest, jvmAndJsMain, jvmMain, jvmTest, linuxX64Main, linuxX64Test
```

Gradle also printed:

```
w: ⚠️ Default Kotlin Hierarchy Template Not Applied Correctly
The Default Kotlin Hierarchy Template was not applied to 'project ':structure-lab'':
Explicit .dependsOn() edges were configured for the following source sets: ...
```

There was **no `iosMain`, `appleMain` or `nativeMain`.** The manual `dependsOn` calls from Q16 switched the default hierarchy template off for the whole project, so the two iOS targets have no shared iOS source set.

**B. With `applyDefaultHierarchyTemplate()` called before the custom edges (the committed version):**

```
appleMain, appleTest, commonMain, commonTest, iosArm64Main, iosArm64Test, iosMain,
iosSimulatorArm64Main, iosSimulatorArm64Test, iosTest, jsMain, jsTest, jvmAndJsMain,
jvmMain, jvmTest, linuxMain, linuxTest, linuxX64Main, linuxX64Test, nativeMain, nativeTest,
webMain, webTest
```

The intermediate source sets are **back**, and the custom `jvmAndJsMain` is still there. [`structure-lab/src/iosMain/.../IosGreeting.kt`](structure-lab/src/iosMain/kotlin/com/asaddeving/structurelab/IosGreeting.kt) depends on `iosMain` existing.

**Explanation:** Kotlin normally creates intermediate source sets such as `appleMain` through the **default hierarchy template**. Any manual `dependsOn` call opts the project out of that template **unless you call `applyDefaultHierarchyTemplate()` explicitly** and then add your own edges.

### Question 18 (Theoretical): Dependency propagation

**Propagation:** a dependency declared in `commonMain` is automatically added to **every source set that depends on `commonMain`**, directly or indirectly. With targets `android`, `iosArm64` and `iosSimulatorArm64`, the source sets `androidMain`, `iosMain`, `iosArm64Main` and `iosSimulatorArm64Main` all receive `kotlinx-coroutines-core:1.7.3` through their `dependsOn` edges. **You do not declare it again** in `androidMain` or `iosMain`.

**Scoping it to iOS only:** declare it in the iOS source set instead of `commonMain`:

```kotlin
kotlin {
    sourceSets {
        iosMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        }
    }
}
```

It then reaches `iosMain` and the iOS source sets below it (`iosArm64Main`, `iosSimulatorArm64Main`), but not Android. The trade-off is that `commonMain` can no longer use it.

### Question 19 (Theoretical): Why `asExecutor()` is red but `Dispatchers.Main` is not

**Compatibility rule:** a source set from a dependency is visible to your source set only if it **compiles to at least the same targets** as yours. Your `commonMain` compiles to `android`, `iosArm64` and `iosSimulatorArm64`, so a library source set must cover all three.

`kotlinx-coroutines-core` is published with its own internal source sets:

| Call | Library source set | Targets it covers | Visible from your `commonMain`? |
|---|---|---|---|
| `Dispatchers.Main` | `commonMain` | all targets | ✅ yes |
| `runBlocking` | `concurrentMain` | every target except JS (JVM/Android + Native) | ✅ yes: covers Android and both iOS targets |
| `dispatcher.asExecutor()` | JVM-only source set (`jvmMain`), because `Executor` is a `java.util.concurrent` type | JVM/Android only | ❌ no: doesn't cover the iOS targets |

So exactly one of the three fails: `asExecutor()` is the only call whose source set doesn't cover every target your `commonMain` compiles to.

### Question 20 (Theoretical): Resolving version conflicts

**What Gradle does:** common code must be compiled against **one** version of each multiplatform dependency. `androidMain` pulls in `navigation-compose:2.7.7`, which needs coroutines ≥ 1.8.0. Gradle resolves the conflict in favour of the **higher** version, and the Kotlin Gradle plugin aligns the source sets, so **`commonMain` effectively uses `kotlinx-coroutines-core:1.8.0`** even though it declared 1.7.3. To keep shared code consistent, the same 1.8.0 is applied to **`iosMain`** and every other `*Main` source set. Every platform ends up on 1.8.0.

**Why upgrading in `iosTest` doesn't affect `*Main`:** `*Main` and `*Test` source sets are aligned **separately**. A `*Test` configuration includes all the dependencies of its `*Main`, but not the other way round. An upgrade in `iosTest` spreads only through the tree of `*Test` source sets and never changes the version used by production (`*Main`) code. That lets you test against a newer library version without touching production.

### Question 21 (Hands-on): Compilations

Implemented as a Gradle task in [`structure-lab/build.gradle.kts`](structure-lab/build.gradle.kts):

```kotlin
val jvmCompilations = kotlin.jvm().compilations
val jvmMainCompilationName = jvmCompilations.getByName("main").name
val jvmCompilationNames = jvmCompilations.names.sorted()

tasks.register("printJvmCompilations") {
    group = "structure lab"
    doLast {
        println("JVM main compilation: $jvmMainCompilationName")
        println("JVM compilations: $jvmCompilationNames")
    }
}
```

**Recorded output of `./gradlew :structure-lab:printJvmCompilations`:**

```
JVM main compilation: main
JVM compilations: [main, test]
```

This confirms that a **`test`** compilation is registered for `jvm()` alongside `main`.

**Source set vs compilation:** a source set is a group of Kotlin files with its own targets and dependencies. A compilation is a single compiler run for one target that takes one or more source sets and produces an artifact (`main` for production code, `test` for tests).

---

## Section 4 – Choosing a KMP Project Configuration (Lecture 11)

### Question 22 (Theoretical): One shared module vs several

| | **Single shared module** | **Several shared modules** |
|---|---|---|
| **Pros** | Simple setup. One place for shared code. **One iOS framework.** No dependency-duplication problems. Fine for small projects. | Clear boundaries and ownership. Loosely coupled, self-contained parts. Faster incremental builds. Features can be reused or released separately. |
| **Cons** | As it grows it becomes *"yet another monolith"*: slower builds, blurred boundaries, unclear ownership, everything rebuilt for any change. | Every module adds build complexity and boilerplate. Harder to keep configuration consistent. On iOS you need an **umbrella module** (Q23) to avoid duplicated dependencies. |

**When to switch:** start with **one** module and choose granularity based on the size and complexity of the codebase. Move to several modules once the single module becomes the **bottleneck**: build times, team size or tangled code start to hurt. If the project isn't expected to grow, the scalability and build-time gains won't apply, so don't split. When you do split, do it **gradually**: find the boundaries, extract core/shared code first, then move one feature at a time. Avoid many tiny modules without clear boundaries.

*This repository uses a single `shared` module, the right size for a small project. Clean architecture is enforced with **packages** (`domain`, `data`, `presentation`, `platform`, `di`) inside it, so each package can later become its own Gradle module without rewriting code.*

### Question 23 (Theoretical): Why one framework per feature module breaks on iOS, and the umbrella module

**Why it breaks:** on iOS each Kotlin framework is a separate, self-contained binary. If every feature module becomes its own framework, each one **bundles its own copy** of the shared dependencies: the Kotlin runtime/stdlib, coroutines, a common `core` module, and so on. The same dependency then exists several times inside the app.

**The concrete failure mode, state not shared through "the same" dependency:** each copy has its own singletons and objects. Something stored in one framework (a cache, a logged-in session, an `object` instance) is invisible to the others, and a type from `core` in framework A is a *different* type from the "same" type in framework B. The result is bugs, a bigger app and slower builds.

**Umbrella module:** a thin module whose only job is to depend on (and `export`) all the feature modules and produce **one** iOS framework. Each shared dependency is included exactly once, so the app has one runtime and one copy of everything.

```kotlin
// umbrella/build.gradle.kts
kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "Shared"
            export(project(":feature-a"))
            export(project(":feature-b"))
            export(project(":feature-c"))
        }
    }
    sourceSets.commonMain.dependencies {
        api(project(":feature-a"))
        api(project(":feature-b"))
        api(project(":feature-c"))
    }
}
```

*In this repository the single `shared` module already acts as the umbrella. It produces one `Shared` framework for both iOS targets ([`shared/build.gradle.kts`](shared/build.gradle.kts)).*

### Question 24 (Theoretical): Monorepo vs 2 repos vs 3 repos

| Layout | Advantage | Disadvantage |
|---|---|---|
| **Monorepo** (Android + iOS + shared) | **Atomic changes**: one commit/PR updates shared code and both apps, with no version juggling. | One big repository with heavier CI and builds. Every team shares the same repo and tooling. |
| **Two repos** (Android + shared \| iOS) | Android uses shared code **from source** with no publishing. iOS developers keep a smaller, Xcode-focused repo. | iOS must consume shared code as a **built binary**, so there is a release step and iOS lags behind shared changes. |
| **Three repos** (Android \| iOS \| shared) | Clean separation and ownership. Shared code has its own lifecycle and can serve several apps. | Every shared change needs a **publish + version bump** before either app can use it. Debugging across repos is harder. |

**Versioning difference (2 vs 3 repos):** in the two-repo setup the shared modules live with the Android code, so **only the iOS side** consumes a versioned artifact. In the three-repo setup the shared modules must be **versioned and published as artifacts for both platforms** (e.g. Maven for Android and an XCFramework / Swift Package for iOS), and **each app repo pins a version** of them.

### Question 25 (Theoretical): Configuration for a 200-engineer, five-app company

**1. Repositories: "many repositories" layout**

- **One repository per shared library**: `logging`, `networking` and `analytics` (3 repos), plus **one repository per app** (5 repos). Add a small `core` library repo only if the libraries really share code.
- Each library has its own version, CI and release cadence, so no app is forced onto a shared monolith and no team is blocked by another team's release.

**2. Each app uses only what it needs**

- Every app declares **only** the libraries it uses, **pinned by version**:
  ```kotlin
  commonMain.dependencies {
      implementation("com.company:networking:2.3.0")
      implementation("com.company:logging:1.8.1")
      // no analytics: this app doesn't need it
  }
  ```
- On iOS each app has **its own umbrella module** that `export`s only the libraries it chose. It ships one framework with no duplicated dependencies and no unused code (Q23).
- Libraries prefer **`implementation` over `api`**, so an app that uses `networking` doesn't inherit its transitive dependencies. `api` is used only for types that are part of the library's public surface (e.g. types exported in the iOS umbrella framework).

**3. Distribution: hybrid, remote by default**

- **Remote (default):** publish versioned artifacts to an internal Maven repository (Android/JVM) and as XCFramework / Swift Package (iOS). With 200 engineers and five apps, teams need **stable, reproducible builds** and should not have to clone and build every library repo.
- **Local (when needed):** composite builds (`includeBuild`) or `mavenLocal()`, used only while a developer is changing a library and wants to try it inside an app before releasing.
- **Why hybrid:** pure local doesn't scale at this size (everyone would need every repo checked out, and builds would be slow). Pure remote makes the library-to-app feedback loop slow, because every small change needs a publish. Hybrid gives stable releases by default and a fast loop during development.

**4. Keeping many repositories consistent**

- Share a **Version Catalog** and **convention plugins** across all library repos, so every library uses the same Kotlin/Gradle versions and the same build and publishing setup. Without that, configuration drifts between repos. (This repo already keeps every version in one [`gradle/libs.versions.toml`](gradle/libs.versions.toml).)

**Summary:** many library repos, version-pinned consumption, a per-app umbrella module, and a hybrid (remote-first) workflow. The cost is the release overhead of many repositories, which is acceptable at this scale.
