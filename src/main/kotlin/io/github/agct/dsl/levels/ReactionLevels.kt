@file:Suppress("PackageDirectoryMismatch", "PropertyName", "UNUSED_PARAMETER")

package agct

import io.github.agct.dsl.AgctDslComponent
import io.github.agct.model.utils.lateVal

@AgctDslComponent
abstract class ReactionLevel<R : DslReaction> internal constructor() {
    internal abstract val reaction: R
}

class TranscriptionLevel internal constructor(private val coder: DslGene) :
    ReactionLevel<DslTranscription>() {
    private var transcription: DslTranscription by lateVal("target protein")

    override val reaction
        get() = transcription

    val the
        get() = this

    val with
        get() = With()

    val regulated
        get() = RegulatedBy()

    infix fun protein(id: String) =
        ProteinLevel(id).run {
            transcription = DslTranscription(coder, entity).apply { TopLevel.circuit.putReaction(this) }
            EntityLevelWrapper(this)
        }

    inner class With internal constructor() {
        infix fun a(dummy: basal.Rate) = reaction.basalRate
    }

    inner class RegulatedBy internal constructor() {
        infix fun by(block: RegulationLevel.() -> Unit) = And().and(block)

        inner class And internal constructor() {
            infix fun and(block: RegulationLevel.() -> Unit) =
                RegulationLevel(transcription).block().let { this }
        }
    }
}

class RegulationLevel internal constructor(
    private val transcription: DslTranscription
) : ReactionLevel<DslRegulation>() {
    private var regulation: DslRegulation by lateVal("regulator")

    override val reaction
        get() = regulation

    val the
        get() = this

    val with
        get() = With()

    infix fun protein(id: String) =
        ProteinLevel(id).wrapper

    infix fun molecule(id: String) =
        RegulatorLevel(id).wrapper

    private val<E : EntityLevel<DslRegulating>> E.wrapper
        get() = run {
            regulation = DslRegulation(transcription, entity).apply { TopLevel.circuit.putReaction(this) }
            EntityLevelWrapper(this)
        }

    inner class With internal constructor() {
        infix fun a(dummy: regulating.Rate) = reaction.regulatingRate
        infix fun a(dummy: binding.Rate) = reaction.bindingRate
        infix fun an(dummy: unbinding.Rate) = reaction.unbindingRate
    }
}

class CustomReactionsLevel internal constructor() {
    infix fun String.to(products: String) =
        ChemicalReactionLevel(DslChemicalReaction(this.toEntityMap(), products.toEntityMap()))

    private fun String.toEntityMap(): Map<DslEntity, Int>  =
        split("+").map { string -> Pair(string, string.indexOfFirst { it.isLetter() }) }
            .filter { (_, firstChar) -> firstChar >= 0 }
            .map { (string, firstChar) -> Pair(string.substring(firstChar), string.substring(0 until firstChar)) }
            .map { (entity, coefficient) -> Pair(entity.trim(), coefficient.trim()) }
            .toMap()
            .mapKeys { (entity, _) -> TopLevel.circuit.getOrPutEntity(entity) { DslGenericEntity(this) } }
            .mapValues { (_, coefficient) -> coefficient.toIntOrNull() ?: 1 }

    inner class ChemicalReactionLevel internal constructor(
        override val reaction: DslChemicalReaction
    ) : ReactionLevel<DslChemicalReaction>() {
        init {
            TopLevel.circuit.putReaction(reaction)
        }

        infix fun having(dummy: reaction.Rate) = reaction.reactionRate
    }
}