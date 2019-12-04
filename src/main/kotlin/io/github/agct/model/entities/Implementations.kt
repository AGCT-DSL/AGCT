package io.github.agct.model.entities

import io.github.agct.model.utils.checkEquals
import io.github.agct.model.utils.string
import io.github.agct.model.utils.type

internal abstract class AbstractEntity(parameters: EntityParameters) : Entity {
    override val id = parameters.id.apply {
        require(!startsWith('_')) { "The id \"$this\" starts with an underscore but it shouldn't" }
    }
    override val initialConcentration = parameters.initialConcentration
    override val aliases = parameters.aliases.toList()

    override fun toString() = "$type(${id.string})"

    override fun hashCode() =
        id.hashCode()

    override fun equals(other: Any?) =
        checkEquals(other) { id == it.id }
}

internal abstract class AbstractBoundEntity<out F : Entity, out S : Entity>(
    override val first: F,
    override val second: S
) : AbstractEntity(EntityParameters().apply { id = "${first.id}_${second.id}" }),
    BoundEntity<F, S>

internal class BasicDegradingEntity(parameters: EntityParameters) :
    AbstractEntity(parameters), DegradingEntity

internal class BasicRegulatingEntity(parameters: EntityParameters) :
    AbstractEntity(parameters), RegulatingEntity

internal class DegradingRegulatingEntity(parameters: EntityParameters) :
    AbstractEntity(parameters), DegradingEntity,
    RegulatingEntity

internal class BasicEntity(parameters: EntityParameters) :
    AbstractEntity(parameters)

internal class BasicGene(parameters: EntityParameters) :
    AbstractEntity(parameters), Gene

internal class BasicMRna(parameters: EntityParameters) :
    AbstractEntity(parameters), MRna

internal class BasicProtein(parameters: EntityParameters) :
    AbstractEntity(parameters), Protein

internal class RegulatedMRna(mRna: MRna, regulator: RegulatingEntity) :
    AbstractBoundEntity<MRna, RegulatingEntity>(mRna, regulator),
    MRna

internal class RegulatedGene(gene: Gene, regulator: RegulatingEntity) :
    AbstractBoundEntity<Gene, RegulatingEntity>(gene, regulator),
    Gene