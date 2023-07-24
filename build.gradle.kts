plugins {
    kotlin("jvm") version "1.9.0"
    jacoco
    `java-library`
}

group = "net.babanin"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        url = uri("https://repository.ow2.org/nexus/content/repositories/releases/")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

jacoco {
    toolVersion = "0.8.9"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}