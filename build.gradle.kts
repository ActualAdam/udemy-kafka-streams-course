import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    val kotlinVersion = "1.4.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

group = "com.actualadam.udemy.kafka.streams"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.kafka:kafka-streams:2.6.0")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.3")
    // implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-log4j12:1.7.30")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.0")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
