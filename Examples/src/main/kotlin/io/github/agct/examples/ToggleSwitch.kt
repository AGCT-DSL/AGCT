package io.github.agct.examples

import agct.Create
import agct.basal
import agct.binding
import agct.codes
import agct.degradation
import agct.export
import agct.initial
import agct.reaction
import agct.regulating
import agct.unbinding
import io.github.agct.generation.target.Alchemist
import io.github.agct.generation.target.SBOL

fun main() {
    Create circuit "Genetic Toggle Switch" containing {
        the gene "promoter1" that codes {
            the protein "repressor2"
            regulated by {
                the protein "repressor1"
                with a regulating.rate of 0.1
            }
        }

        the gene "promoter2" that codes {
            the protein "repressor1"
            regulated by {
                the protein "repressor2"
                with a regulating.rate of 0.1
            }
        }
        
        chemical reactions {
            "repressor1 + inducer1" to "repressor1_inducer1"
            "repressor1_inducer1" to "repressor1 + inducer1"
            "repressor2 + inducer2" to "repressor2_inducer2"
            "repressor2_inducer2" to "repressor2 + inducer2"
        }

        "promoter1" {
            has an initial.concentration of 1
        }

        "promoter2" {
            has an initial.concentration of 1
        }
    } with {
        a default initial.concentration of 0
        a default degradation.rate of 1
        a default basal.rate of 1
        a default binding.rate of 1
        a default unbinding.rate of 1
        a default reaction.rate of 1
    } then export to SBOL
}