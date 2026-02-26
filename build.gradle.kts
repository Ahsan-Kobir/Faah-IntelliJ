plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "com.github.faah"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Target IntelliJ IDEA Community; change to androidStudio("2024.1.1") for Android Studio
        intellijIdeaCommunity("2024.3")
        instrumentationTools()
        // Soft-depend on Android plugin for Logcat support (optional)
        // plugin("org.jetbrains.android")
    }
    // MP3 decoder — javax.sound.sampled has no built-in MP3 support
    implementation("javazoom:jlayer:1.0.1")
}

intellijPlatform {
    pluginConfiguration {
        id = "com.github.faah"
        name = "Faah"
        version = "1.0.0"
        ideaVersion {
            sinceBuild = "243"       // IntelliJ 2024.3
            // untilBuild left unset — compatible with all future versions
        }
    }
    publishing {
        token = providers.gradleProperty("intellijPlatformPublishingToken")
    }
}

kotlin {
    jvmToolchain(17)
}
