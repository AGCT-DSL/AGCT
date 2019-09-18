import agct.*
import utils.ExportableAlchemist
import utils.line

fun main() {
    Create circuit "Repressilator" containing {
        the gene "gTetR" that codes For {
            the protein "TetR"
            regulated by { the protein "LacI" }
        }

        the gene "gLacI" that codes For {
            the protein "LacI"
            regulated by { the protein "AcI" }
        }

        the gene "gAcI" that codes For {
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