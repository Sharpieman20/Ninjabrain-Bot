/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn more about Gradle by exploring our samples at https://docs.gradle.org/7.2/samples
 */

version="1.0.3"

plugins {
    application
    `maven-publish`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withSourcesJar()
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
//    test {
//        java {
//            setSrcDirs(listOf("test/java"))
//        }
//    }
//}

//tasks.named<Test>("test") {
//    useJUnitPlatform()
//}



dependencies {
    // Use JUnit test framework.
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testCompileOnly("junit:junit:4.13")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.github.kwhat:jnativehook:2.2.1")
    implementation("org.json:json:20211205")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.2.0.RELEASE")
}

application {
    mainClass.set("bot.Main")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "sharpie"
            artifactId = "Ninjabrain-Bot"
            version = "1.0.2"

            from(components["java"])
        }
    }
}
