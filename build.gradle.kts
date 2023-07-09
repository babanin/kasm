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
    implementation(group = "org.ow2.asm", name= "asm", version = "9.5")
    implementation(group = "org.ow2.asm", name= "asm-util", version = "9.5")
    
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}