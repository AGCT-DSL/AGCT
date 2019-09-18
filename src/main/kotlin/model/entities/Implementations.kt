package model.entities

import model.utils.checkEquals
import model.utils.string
import model.utils.type

internal abstract class AbstractEntity(parameters: EntityParameters) : Entity {
    override val id = parameters.id
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
) : AbstractEntity(EntityParameters().apply { id = "${first.id}_${second.id}" }), BoundEntity<F, S>

internal class BasicDegradingEntity(parameters: EntityParameters) :
    AbstractEntity(parameters), DegradingEntity

internal class BasicRegulatingEntity(parameters: EntityParameters) :
    AbstractEntity(parameters), RegulatingEntity

internal class DegradingRegulatingEntity(parameters: EntityParameters) :
    AbstractEntity(parameters), DegradingEntity, RegulatingEntity

internal class BasicEntity(parameters: EntityParameters) :
    AbstractEntity(parameters)

internal class BasicGene(parameters: EntityParameters) :
    AbstractEntity(parameters), Gene

internal class BasicMRna(parameters: EntityParameters) :
    AbstractEntity(parameters), MRna

internal class BasicProtein(parameters: EntityParameters) :
    AbstractEntity(parameters), Protein

internal class BasicMolecule(parameters: EntityParameters) :
    AbstractEntity(parameters), Molecule

internal class RegulatedMRna(mRna: MRna, regulator: RegulatingEntity) :
    AbstractBoundEntity<MRna, RegulatingEntity>(mRna, regulator), MRna

internal class RegulatedGene(gene: Gene, regulator: RegulatingEntity) :
    AbstractBoundEntity<Gene, RegulatingEntity>(gene, regulator), Gene