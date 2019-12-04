@file:Suppress("PackageDirectoryMismatch")

package agct

import io.github.agct.model.entities.*
import io.github.agct.model.entities.BasicGene
import io.github.agct.model.entities.BasicProtein
import io.github.agct.model.entities.BasicRegulatingEntity
import io.github.agct.model.entities.DegradingRegulatingEntity
import io.github.agct.model.entities.EntityParameters

abstract class DslEntity internal constructor() {
    internal abstract val modelEntity: Entity

    internal abstract val id: String

    internal val initialConcentration = TopLevel.circuit.default.initialConcentration
}

abstract class DslDegradable internal constructor() : DslEntity() {
    internal abstract var degradationRate: DslRate?
}

abstract class DslRegulating internal constructor() : DslDegradable() {
    abstract override val modelEntity: RegulatingEntity
}

class DslGene(override val id: String) : DslEntity() {
    override val modelEntity: Gene
        get() = BasicGene(parameters)
}

class DslProtein(override val id: String) : DslRegulating() {
    override val modelEntity: Protein
        get() = BasicProtein(parameters)
    override var degradationRate: DslRate? = TopLevel.circuit.default.degradationRate
}

class DslRegulator(override val id: String) : DslRegulating() {
    override val modelEntity: RegulatingEntity
        get() = modelEntity(doesDegrade = degradationRate != null, doesRegulate = true)
    override var degradationRate: DslRate? = null
}

class DslGenericEntity(override val id: String) : DslDegradable() {
    override val modelEntity: Entity
        get() = modelEntity(doesDegrade = degradationRate != null, doesRegulate = false)
    override var degradationRate: DslRate? = null
}

private val DslEntity.parameters: EntityParameters
    get() = EntityParameters().also {
        it.id = id
        it.initialConcentration = initialConcentration.value
    }

private inline fun<reified T: Entity> DslEntity.modelEntity(doesDegrade: Boolean, doesRegulate: Boolean): T = when {
    doesDegrade && doesRegulate -> DegradingRegulatingEntity(parameters)
    doesDegrade && !doesRegulate -> BasicDegradingEntity(parameters)
    !doesDegrade && doesRegulate -> BasicRegulatingEntity(parameters)
    else -> BasicEntity(parameters)
}.run {
    if (this is T) this
    else throw IllegalArgumentException("The model entity $this can not be casted to ${T::class.simpleName}")
}