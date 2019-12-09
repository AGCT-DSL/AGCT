dependencies {
    implementation(rootProject)
    implementation("it.unibo.alchemist:alchemist:9.0.0") {
        exclude(module = "alchemist-incarnation-protelis")
        exclude(module = "alchemist-incarnation-sapere")
        exclude(module = "alchemist-incarnation-scafi")
        exclude(module = "alchemist-grid")
        exclude(module = "alchemist-cognitive-agents")
        exclude(module = "alchemist-maps")
        exclude(module = "alchemist-projectview")
    }
    testImplementation("io.github.classgraph:classgraph:4.8.58")
    runtimeOnly("org.slf4j:slf4j-nop:1.7.26")
}

tasks {
    register<JavaExec>("helpAlchemist") {
        classpath = project.sourceSets["main"].runtimeClasspath
        main = "it.unibo.alchemist.Alchemist"
        args("--help")
    }

    register<JavaExec>("exportCircuit") {
        classpath = project.sourceSets["main"].runtimeClasspath
        main = "${simulation}Kt"
    }

    register<JavaExec>("runCircuit") {
        val arguments = arrayOf(
            "-y", "export/${simulation.toLowerCase()}/alchemist.yml",
            "-e", "export/${simulation.toLowerCase()}/alchemistdata",
            "-t", "$time",
            "-i", "$step"
        )
        classpath = project.sourceSets["main"].runtimeClasspath
        main = "it.unibo.alchemist.Alchemist"
        args(*arguments, *vars)
        mustRunAfter("exportCircuit")
    }

    register<Exec>("plotCircuit") {
        commandLine("py", "src/main/python/$simulation.py")
        mustRunAfter("runCircuit")
    }

    register<Task>("executeCircuit") {
        dependsOn("exportCircuit", "runCircuit", "plotCircuit")
    }
}

val Task.simulation
    get() = project.properties["sim"]?.toString() ?: ""

val Task.time
    get() = project.properties["time"]?.toString()?.toDoubleOrNull() ?: 100.0

val Task.step
    get() = time / 1000.0

val Task.vars
    get() = project.properties["vars"]?.run {
        toString().split("_")
            .map { listOf("-var", it) }
            .flatten()
            .toMutableList()
            .apply { add("-b") }
            .toTypedArray()
    } ?: arrayOf("-hl")
