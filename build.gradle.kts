val kotlinVersion = "1.3.72"
val kotlinTestVersion = "3.4.2"

plugins {
    java
    kotlin("jvm") version "1.3.72"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    testCompile("io.kotlintest:kotlintest-runner-junit5:$kotlinTestVersion")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}

defaultTasks("test")