plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.2.1"
    kotlin("jvm") version "2.1.10"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("org.openapitools:openapi-generator-cli:7.11.0")
    intellijPlatform {
        intellijIdeaCommunity("2024.3")
    }
}