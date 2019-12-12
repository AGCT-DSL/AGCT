val kotlinVersion = "1.3.50"
val kotlinTestVersion = "3.3.3"

plugins {
    kotlin("jvm") version "1.3.50"
}

allprojects {
    apply(plugin = "kotlin")

    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
        implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
        implementation("org.redundent:kotlin-xml-builder:+")
        testCompile("io.kotlintest:kotlintest-runner-junit5:$kotlinTestVersion")
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}
