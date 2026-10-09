/*
 * Lab module for the source-set exercises (Lectures 9 and 10).
 * It has no Android target, so it builds without an Android SDK.
 *
 *   Q8  jvm() + iosArm64()                -> commonMain, jvmMain, iosArm64Main (+ *Test)
 *   Q12 + js { browser() }                -> jsMain / jsTest appear with no manual wiring
 *   Q16 + linuxX64() and jvmAndJsMain     -> custom intermediate source set for JVM + JS only
 *   Q17 + iosSimulatorArm64()             -> applyDefaultHierarchyTemplate() brings back iosMain/appleMain
 *   Q21 compilations.getByName("main")    -> see the printJvmCompilations task
 */
plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    iosArm64()
    js { browser() } // On older Kotlin versions: js(IR) { browser() }
    linuxX64()
    iosSimulatorArm64()

    // Manual dependsOn edges switch the default template off; re-apply it explicitly first (Q17).
    applyDefaultHierarchyTemplate()

    sourceSets {
        val jvmAndJsMain by creating {
            dependsOn(commonMain.get())
        }
        jvmMain.get().dependsOn(jvmAndJsMain)
        jsMain.get().dependsOn(jvmAndJsMain)

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

val sourceSetNames = kotlin.sourceSets.names.sorted()

tasks.register("printSourceSets") {
    group = "structure lab"
    description = "Prints every source set Gradle registered for this module (Q8, Q12, Q17)."
    doLast {
        sourceSetNames.forEach { println("source set -> $it") }
    }
}

val jvmCompilations = kotlin.jvm().compilations
val jvmMainCompilationName = jvmCompilations.getByName("main").name
val jvmCompilationNames = jvmCompilations.names.sorted()

tasks.register("printJvmCompilations") {
    group = "structure lab"
    description = "Prints the compilations registered for the jvm() target (Q21)."
    doLast {
        println("JVM main compilation: $jvmMainCompilationName")
        println("JVM compilations: $jvmCompilationNames")
    }
}
