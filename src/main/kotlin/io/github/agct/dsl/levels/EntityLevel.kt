@file:Suppress("PackageDirectoryMismatch", "ClassName", "UNUSED_PARAMETER", "PropertyName", "FunctionName")

package agct

import io.github.agct.dsl.AgctDslComponent
import io.github.agct.model.utils.string
import io.github.agct.model.utils.type

@AgctDslComponent
open class EntityLevelWrapper<out E: EntityLevel<*>> internal constructor(protected val entityLevel: E) {
    infix fun that(block: E.() -> Unit) =
        entityLevel.run(block)
}

@AgctDslComponent
class GeneLevelWrapper internal constructor(entityLevel: GeneLevel): EntityLevelWrapper<GeneLevel>(entityLevel) {
    infix fun that(codes: codes.Block) =
        entityLevel.Codes().invoke(codes.block)
}

abstract class EntityLevel<out E : DslEntity> internal constructor(
    id: String,
    ifAbsent: String.() -> E
) {
    @Suppress("UNCHECKED_CAST")
    internal val entity = TopLevel.circuit.getOrPutEntity(id, ifAbsent) as E

    open val has
        get() = Has()

    open inner class Has internal constructor() {
        infix fun an(concentration: initial.Concentration) = entity.initialConcentration
    }

}

abstract class DegradingEntityLevel<out E : DslDegradable> internal constructor(
    id: String,
    ifAbsent: String.() -> E
) : EntityLevel<E>(id, ifAbsent) {
    override val has: Has
        get() = Has()

    inner class Has internal constructor() : EntityLevel<E>.Has() {
        infix fun a(dummy: degradation.Rate) =
            entity.degradationRate ?: DslRate("degradation rate").also { entity.degradationRate = it }
    }
}

class GeneLevel internal constructor(id: String) :
    EntityLevel<DslGene>(id, { DslGene(id) }) {
    val codes
        get() = Codes()

    inner class Codes internal constructor() {
        operator fun invoke(block: TranscriptionLevel.() -> Unit) = And().and(block)

        inner class And internal constructor() {
            infix fun and(block: TranscriptionLevel.() -> Unit) =
                TranscriptionLevel(entity).block().let { this }
        }
    }
}

class ProteinLevel internal constructor(id: String) : DegradingEntityLevel<DslProtein>(id, { DslProtein(id) })

class RegulatorLevel internal constructor(id: String) : DegradingEntityLevel<DslRegulating>(id, { DslRegulator(id) })

class GenericEntityLevel internal constructor(id: String) : EntityLevel<DslEntity>(id, {
        throw IllegalArgumentException("Entity ${id.string} has not been set before.")
    }) {
    override val has: Has
        get() = Has()

    inner class Has internal constructor() : EntityLevel<*>.Has() {
        infix fun a(dummy: degradation.Rate) =
            if (entity is DslDegradable)
                entity.degradationRate ?: DslRate("degradation rate").also { entity.degradationRate = it }
            else
                throw IllegalStateException("\"${entity.id}\" is a ${entity.type.decapitalize()} so it cannot degrade.")
    }
}

