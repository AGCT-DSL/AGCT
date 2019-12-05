package io.github.agct.model.entities

import io.github.agct.model.variables.Concentration

/**
 * The pattern an entity id should match to be valid.
 */
val ID_PATTERN = Regex("""^[a-z]((_|-)?[a-z0-9]+)*$""", RegexOption.IGNORE_CASE)

/**
 * An entity inside the genetic circuit, identified by its [id].
 * 
 * The [initial concentration][initialConcentration] is the quantity of molecule present at the beginning.
 */
interface Entity {
    val id: String
    val aliases: List<String>
    val initialConcentration: Concentration
}

/**
 * An entity that can degrade.
 */
interface DegradingEntity : Entity

/**
 * An entity that can behave as gene regulator.
 *
 * I.e. a protein can regulate (activating or inhibiting the transcription of another protein by a gene) so it should
 * extend this interface, while a gene should not as it can not behave as a regulator.
 */
interface RegulatingEntity : Entity

/**
 * An entity that can be a coder in either a transcription or a translation reaction.
 */
interface TranscribingEntity : Entity

/**
 * An entity that can be a target in either a transcription or a translation reaction.
 */
interface TranscribableEntity : Entity

/**
 * A gene that codes for an arbitrary number of proteins, passing through the phases of transcription and translation.
 */
interface Gene : Entity, TranscribingEntity

/**
 * A molecule of mRNA which is the step between the gene and the protein that it codes for.
 */
interface MRna : DegradingEntity, TranscribingEntity,
    TranscribableEntity

/**
 * A protein which can be coded by a gene.
 */
interface Protein : DegradingEntity, RegulatingEntity,
    TranscribableEntity

/**
 * A [genetic entity][Entity] composed of two bound single entities.
 */
interface BoundEntity<out F : Entity, out S : Entity> :
    Entity {
    val first: F
    val second: S
}