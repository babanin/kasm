plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
}

group = "net.babanin"
version = "1.0-SNAPSHOT"

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