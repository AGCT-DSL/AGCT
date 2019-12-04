package io.github.agct.examples

import agct.*
import io.github.agct.generation.target.Alchemist

fun main() {
    Create circuit "Circadian_Rhythm" containing {
        gene("creb") that codes {
            protein ("CREB_I")
        }

        gene ("ckle") that codes {
            protein ("CKLE")
        }
        gene ("scf_btrcp") that codes {
            protein ("SCF_BTRCP")
        }
        gene ("fbxl3") that codes {
            protein ("FBXL3")
        }
        gene ("ampk") that codes {
            protein ("AMPK")
        }
        gene ("per") that codes  {
            protein ("PER")
            regulated by { protein ("CREB") }
            regulated by {protein ("BMALL_CLOCK")}
        }
        gene ("cry") that codes {
            protein ("CRY")
            regulated by {protein ("BMALL_CLOCK")}
        }

        gene ("dec") that codes {
            protein ("DEC")
            regulated by {protein ("BMALL_CLOCK")}
        }
        gene ("rev-erba") that codes {
            protein ("REV-ERBA")
            regulated by {protein ("BMALL_CLOCK")}
        }
        gene ("ror") that codes {
            protein ("ROR")
            regulated by {protein ("BMALL_CLOCK")}
        }
        gene ("bmall_clock") that codes {
            protein ("BMALL_CLOCK")
            regulated by {protein ("ROR")}
            regulated by {
                protein ("REV-ERBA")
                with a regulating.rate of 5}
        }
        chemical reactions{
            "CREB_I + Ca2-ion_cAMP" to "CREB"
            "PER + CKLE" to "PER_p + CKLE"
            "PER + SCF_BTRCP" to "PER_u + SCF_BTRCP"
            "CRY + AMPK " to "CRY_p + AMPK"
            "CRY + FBXL3" to "CRY_u + FBXL3"
            "PER + CRY + CKLE" to "PER-CRY-CKLE"
            "PER_p + CRY + CKLE" to "PER-CRY-CKLE"
            "PER_u + CRY + CKLE" to "PER-CRY-CKLE"
            "PER + CRY_p + CKLE" to "PER-CRY-CKLE"
            "PER_p + CRY_p + CKLE" to "PER-CRY-CKLE"
            "PER_u + CRY_p + CKLE" to "PER-CRY-CKLE"
            "PER + CRY_u + CKLE" to "PER-CRY-CKLE"
            "PER_p + CRY_u + CKLE" to "PER-CRY-CKLE"
            "PER_u + CRY_u + CKLE" to "PER-CRY-CKLE"
            "BMALL_CLOCK + DEC" to "BMALL_CLOCK-DEC"
            "BMALL_CLOCK + PER-CRY-CKLE" to "BMALL_CLOCK-PER-CRY-CKLE"
        }
        "PER_p" {
            has a degradation.rate of 0.5
        }
        "PER_u"{
            has a degradation.rate of 0.01
        }

        "CRY_p"{
            has a degradation.rate of 0.5
        }

        "CRY_u" {
            has a degradation.rate of 0.01
        }

    } with {
        a default initial.concentration of 1
        a default degradation.rate of 0.1
        a default basal.rate of 0
        a default regulating.rate of 10
        a default binding.rate of 0.01
        a default unbinding.rate of 0.01
    } then export to Alchemist

}
