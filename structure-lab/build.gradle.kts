/*
 * Lab module for the source-set exercises (Lecture 9).
 * It has no Android target, so it builds without an Android SDK.
 *
 *   Q7  jvmMain may use java.io.File; commonMain may not
 *   Q8  jvm() + iosArm64()   -> commonMain, jvmMain, iosArm64Main (+ *Test)
 *   Q12 + js { browser() }   -> jsMain / jsTest appear with no manual wiring
 */
plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    iosArm64()
    js { browser() } // On older Kotlin versions: js(IR) { browser() }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

val sourceSetNames = kotlin.sourceSets.names.sorted()

tasks.register("printSourceSets") {
    group = "structure lab"
    description = "Prints every source set Gradle registered for this module (Q8, Q12)."
    doLast {
        sourceSetNames.forEach { println("source set -> $it") }
    }
}
