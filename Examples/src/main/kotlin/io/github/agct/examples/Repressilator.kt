package io.github.agct.examples

import agct.Create
import agct.basal
import agct.binding
import agct.codes
import agct.degradation
import agct.export
import agct.initial
import agct.regulating
import agct.unbinding
import io.github.agct.examples.utils.ExportableAlchemist
import io.github.agct.examples.utils.line

fun main() {
    Create circuit "Repressilator" containing {
        the gene "gTetR" that codes {
            the protein "TetR"
            regulated by { the protein "LacI" }
        }

        the gene "gLacI" that codes {
            the protein "LacI"
            regulated by { the protein "AcI" }
        }

        the gene "gAcI" that codes {
            the protein "AcI"
            regulated by { the protein "TetR" }
        }
    } with {
        a default initial.concentration of 1
        a default degradation.rate of 0.1
        a default basal.rate of 0
        a default regulating.rate of 10
        a default binding.rate of 0.01
        a default unbinding.rate of 0.01
    } then export to ExportableAlchemist {
        line("- time")
        line("- molecule: TetR")
        line("  aggregators: [mean]")
        line("- molecule: LacI")
        line("  aggregators: [mean]")
        line("- molecule: AcI")
        line("  aggregators: [mean]")
    }
}