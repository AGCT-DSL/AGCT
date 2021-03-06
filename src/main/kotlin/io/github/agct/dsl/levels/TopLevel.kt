@file:Suppress("PackageDirectoryMismatch", "UNUSED_PARAMETER")

package agct

import io.github.agct.dsl.AgctDslComponent
import io.github.agct.generation.Generator
import io.github.agct.generation.exportTo
import io.github.agct.model.circuit.GeneticCircuit

val Create = TopLevel()


class TopLevel internal constructor() {
    companion object {
        private var companionCircuit: DslCircuit? = null

        internal val circuit
            get() = companionCircuit ?: throw IllegalStateException("No circuit is running.")
    }

    infix fun circuit(name: String) =
        CircuitWrapper(name)

    class CircuitWrapper internal constructor(private val name: String) {
        private val defaultRoutines = mutableListOf<DefaultsLevel.() -> Unit>()
        private val circuitRoutines = mutableListOf<ContainingLevel.() -> Unit>()

        infix fun with(block: DefaultsLevel.() -> Unit) =
            apply { defaultRoutines.add(block) }

        infix fun containing(block: ContainingLevel.() -> Unit) =
            apply { circuitRoutines.add(block) }

        infix fun then(dummy: export) =
            CircuitExport(geneticCircuit)

        private val geneticCircuit
            get() = MutableDefaultValues().also { defaults ->
                DefaultsLevel(defaults).run {
                    for (routine in defaultRoutines) {
                        routine()
                    }
                }
            }.geneticCircuit(name) { circuit ->
                companionCircuit = circuit
                ContainingLevel(circuit).run {
                    for (routine in circuitRoutines) {
                        routine()
                    }
                }
                companionCircuit = null
            }

        private fun MutableDefaultValues.geneticCircuit(name: String, block: (DslCircuit) -> Unit = { }) =
            BasicDslCircuit(name, immutable).also(block).geneticCircuit
    }
}

@AgctDslComponent
class ContainingLevel internal constructor(private val circuit: DslCircuit) {
    val the
        get() = this

    val chemical
        get() = ChemicalReactions()

    infix fun gene(id: String) =
        GeneLevelWrapper(GeneLevel(id))

    operator fun String.invoke(block: GenericEntityLevel.() -> Unit) =
        EntityLevelWrapper(GenericEntityLevel(this)).that(block)

    inner class ChemicalReactions internal constructor() {
        infix fun reactions(block: CustomReactionsLevel.() -> Unit) =
            CustomReactionsLevel().run(block)
    }
}

class DefaultsLevel internal constructor(private val defaults: MutableDefaultValues) {
    val a
        get() = A()

    inner class A internal constructor() {
        infix fun default(dummy: initial.Concentration) = defaults.initialConcentration
        infix fun default(dummy: degradation.Rate) = defaults.degradationRate
        infix fun default(dummy: basal.Rate) = defaults.basalRate
        infix fun default(dummy: regulating.Rate) = defaults.regulatingRate
        infix fun default(dummy: binding.Rate) = defaults.bindingRate
        infix fun default(dummy: unbinding.Rate) = defaults.unbindingRate
        infix fun default(dummy: reaction.Rate) = defaults.reactionRate
    }
}

class CircuitExport internal constructor(private val circuit: GeneticCircuit) {
    infix fun to(generator: Generator) =
        And().and(generator)

    infix fun to(dummy: each) = Each()

    inner class And internal constructor() {
        infix fun and(generator: Generator) =
            apply { circuit.exportTo(generator) }
    }

    inner class Each internal constructor() {
        infix fun one(generators: Collection<Generator>) =
            circuit.exportTo(*generators.toTypedArray())
    }
}