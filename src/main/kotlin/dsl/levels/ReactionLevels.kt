@file:Suppress("PackageDirectoryMismatch", "PropertyName", "UNUSED_PARAMETER")

package dsl

import dsl.TopLevel.Companion.circuit

abstract class ReactionLevel<R : DslReaction> internal constructor() {
    protected abstract val reaction: R
}

class TranscriptionLevel internal constructor(private val coder: DslGene) :
    ReactionLevel<DslTranscription>() {
    private lateinit var transcription: DslTranscription

    override val reaction
        get() = if (this::transcription.isInitialized) transcription
                else throw IllegalStateException("The target protein has not been set yet.")

    val the
        get() = The()

    val with
        get() = With()

    val regulatedBy
        get() = RegulatedBy()

    inner class The internal constructor() {
        infix fun protein(id: String) =
            ProteinLevel(id).run {
                transcription = DslTranscription(coder, entity).apply { circuit.putReaction(this) }
                EntityLevelWrapper(this)
            }
    }

    inner class With internal constructor() {
        infix fun a(dummy: basal_rate) = reaction.basalRate
    }

    inner class RegulatedBy internal constructor() {
        operator fun invoke(block: RegulationLevel.() -> Unit) =
            RegulationLevel(transcription).block()
    }
}

class RegulationLevel internal constructor(
    private val transcription: DslTranscription
) : ReactionLevel<DslRegulation>() {
    private lateinit var regulation: DslRegulation

    override val reaction
        get() = if (this::regulation.isInitialized) regulation
                else throw IllegalStateException("The regulator has not been set yet.")

    val the
        get() = The()

    val with
        get() = With()

    inner class The internal constructor() {
        infix fun protein(id: String) =
            ProteinLevel(id).wrapper

        infix fun molecule(id: String) =
            RegulatorLevel(id).wrapper

        private val<E : EntityLevel<DslRegulating>> E.wrapper
            get() = run {
                regulation = DslRegulation(transcription, entity).apply { circuit.putReaction(this) }
                EntityLevelWrapper(this)
            }
    }

    inner class With internal constructor() {
        infix fun a(dummy: regulated_rate) = reaction.regulatedRate

        infix fun a(dummy: binding_rate) = reaction.bindingRate

        infix fun an(dummy: unbinding_rate) = reaction.unbindingRate
    }
}