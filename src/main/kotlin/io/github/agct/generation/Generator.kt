package io.github.agct.generation

import io.github.agct.model.circuit.GeneticCircuit
import java.io.File

val GeneticCircuit.defaultDirectory
    get() = buildString {
        append(System.getProperty("user.dir").replace("Examples", ""))
        append("/Examples/export/")
        append(name.toLowerCase())
    }

interface Generator {
    fun from(circuit: GeneticCircuit)
}

abstract class AbstractGenerator(
    protected open val files: GeneticCircuit.() -> Map<String, String>
) : Generator {
    constructor(filesMap: GeneticCircuit.(MutableMap<String, String>) -> Unit) : this(files = {
        mutableMapOf<String, String>().also { filesMap(it) }.toMap()
    })

    override fun from(circuit: GeneticCircuit) =
        files(circuit).forEach { (filename, data) ->
            File(filename).run {
                parentFile.mkdirs()
                writeText(data)
            }
        }
}

fun GeneticCircuit.exportTo(vararg generators: Generator) {
    checkRules()

    for (generator in generators) {
        generator.from(this)
    }
}