import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version "0.33.0"
    kotlin("multiplatform") version "1.4.20-M1"
}

group = "com.marshalldbrain"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

kotlin {

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
                implementation(kotlin("stdlib-common"))
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                dependsOn(commonMain)
                implementation(kotlin("stdlib-jdk7"))
                implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
                implementation("org.slf4j:slf4j-log4j12:2.0.0-alpha1")
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                dependsOn(commonMain)
                implementation(kotlin("stdlib-jdk8"))
                implementation("io.kotest:kotest-runner-junit5-jvm:4.3.1")
                implementation("io.kotest:kotest-assertions-core-jvm:4.3.1")
                implementation("io.kotest:kotest-property-jvm:4.3.1")
                implementation("org.assertj:assertj-core:3.18.0")
                implementation("io.mockk:mockk:1.10.2")
            }

            tasks.withType<Test> {
                useJUnitPlatform()
                testLogging {
                    outputs.upToDateWhen {false}
                    showStandardStreams = true
                }
            }

        }

    }

}