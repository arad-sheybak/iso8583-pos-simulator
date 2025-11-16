pluginManagement {
    repositories {
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo1.maven.org/maven2/") }
    }
}

rootProject.name = "server-emulator"