package io.github.agct.generation.target

import agct.reaction
import io.github.agct.generation.AbstractGenerator
import io.github.agct.generation.defaultDirectory
import io.github.agct.generation.utils.Level.Companion.start
import io.github.agct.model.circuit.GeneticCircuit
import io.github.agct.model.entities.BoundEntity
import io.github.agct.model.entities.Entity
import org.redundent.kotlin.xml.xml

object SBOL : SBOLGenerator({ "$defaultDirectory/sbol.xml" })

open class SBOLGenerator(filename: GeneticCircuit.() -> String) : AbstractGenerator({ file ->
    val species = this.entities
    val name = this.name
    val reactions = this.reactions
    var idGenerator = 0
    file[filename()] = xml("sbml") {
        globalProcessingInstruction("xml", "version" to "1.0", "encoding" to "UTF-8", "standalone" to "no")
        val compartmentName = "cell"
        "model" {
            attribute("id", name)
            "listOfCompartments" {
                "compartment" {
                    attribute("id", compartmentName)
                    attribute("constant",true)
                }
            }
            "listOfSpecies" {
                for (specie in species) {
                    "species" {
                        attribute("id", specie.id)
                        attribute("constant", false)
                        require(specie.initialConcentration.values.size == 1)
                        attribute("initialAmount", specie.initialConcentration.values.random())
                        attribute("compartment", compartmentName)
                    }
                }
            }
            "listOfReactions" {
                for (reaction in reactions.flatMap { it.reactions }) {
                    "reaction" {
                        attribute("id", "reaction${idGenerator++}")
                        attribute("reversible", "false")
                        attribute("compartment", compartmentName)
                        fun generateChemicalSpeciesFor(name: String, stoichiometries: Map<Entity, Int>) = name {
                            for ((reactant, stoichiometry) in stoichiometries) {
                                "speciesReference" {
                                    attribute("constant", true)
                                    attribute("species", reactant.id)
                                    attribute("stoichiometry", stoichiometry)
                                }
                            }
                        }
                        generateChemicalSpeciesFor("listOfReactants", reaction.reagents)
                        generateChemicalSpeciesFor("listOfProducts", reaction.products)
                        "kineticLaw" {
                            "math" {
                                attribute("xmlns", "http://www.w3.org/1998/Math/MathML")
                                "apply" {
                                    "times"()
                                    require(reaction.rate.values.size == 1)
                                    "ci" { -"${reaction.rate.values.random()}" }
                                    for ((reactant, _) in reaction.reagents) {
                                        "ci" { -"${reactant.id}" }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }.toString()
})
