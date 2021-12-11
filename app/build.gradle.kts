/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn more about Gradle by exploring our samples at https://docs.gradle.org/7.2/samples
 */

plugins {
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

//sourceSets {
//    main {
//        java {
//            setSrcDirs(listOf("src/java"))
//        }
//        resources {
//            setSrcDirs(listOf("src/resources"))
//        }
//    }
//}



dependencies {
    // Use JUnit test framework.
    testImplementation("junit:junit:4.13.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.github.kwhat:jnativehook:2.2.1")
    implementation("org.json:json:20211205")
}

application {
    mainClass.set("ninjabrainbot.Main")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

