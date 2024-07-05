import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val json_version: String by project

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"

    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/space/maven")
}

dependencies {
    implementation("com.slack.api:slack-api-client:1.36.1")
    implementation("org.slf4j:slf4j-nop:2.0.7")

    implementation("org.jetbrains:space-sdk-jvm:168099")
    implementation("io.ktor:ktor-client-cio-jvm:2.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$json_version")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

//tasks.withType<KotlinCompile> {
//    kotlinOptions.freeCompilerArgs = listOf("-Xdebug")  //do not use in prod
//}
