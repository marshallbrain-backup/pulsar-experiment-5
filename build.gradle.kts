plugins {
    kotlin("multiplatform") version "1.3.72"
}

group = "com.marshalldbrain"
version = "0.1"

repositories {
    mavenCentral()
}

kotlin {

    jvm()

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
                implementation(kotlin("stdlib-common"))
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk7"))
                implementation("org.slf4j:slf4j-api:1.7.5")
                implementation("org.slf4j:slf4j-log4j12:1.7.5")
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation("io.kotlintest:kotlintest-runner-junit5:3.3.0")
                implementation("org.assertj:assertj-core:3.11.1")
                implementation("io.mockk:mockk:1.9.3")
            }
        }

    }

}