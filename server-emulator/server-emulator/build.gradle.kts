plugins {
    kotlin("jvm") version "1.9.24"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // jPOS (ISO8583)
    implementation("org.jpos:jpos:2.1.7")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Kotlinx Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.1")

    // YAML config support (configs/server.yml)
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.0")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}