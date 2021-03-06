@file:Suppress("PackageDirectoryMismatch", "UNUSED_PARAMETER", "PropertyName")

package agct

import io.github.agct.model.utils.create

data class ValuesContainer internal constructor(
    val initialConcentration: DslConcentration = DslConcentration("initial concentration"),
    val degradationRate: DslRate = DslRate("degradation rate"),
    val basalRate: DslRate = DslRate("basal rate"),
    val regulatingRate: DslRate = DslRate("regulating rate"),
    val bindingRate: DslRate = DslRate("binding rate"),
    val unbindingRate: DslRate = DslRate("unbinding rate"),
    val reactionRate: DslRate = DslRate("reaction rate")
)

abstract class DefaultValues internal constructor() {
    internal val initialConcentration
        get() = container.initialConcentration.get

    internal val degradationRate
        get() = container.degradationRate.get

    internal val basalRate
        get() = container.basalRate.get

    internal val regulatingRate
        get() = container.regulatingRate.get

    internal val bindingRate
        get() = container.bindingRate.get

    internal val unbindingRate
        get() = container.unbindingRate.get

    internal val reactionRate
        get() = container.reactionRate.get

    protected abstract val container: ValuesContainer

    protected abstract val <T : DslVariable<*>> T.get : T
}

class ImmutableDefaultValues internal constructor(override val container: ValuesContainer) : DefaultValues() {
    override val <T : DslVariable<*>> T.get: T
        get() = javaClass.kotlin.create(variableName, value)
}

class MutableDefaultValues internal constructor() : DefaultValues() {
    internal val immutable
        get() = ImmutableDefaultValues(container)

    override val <T : DslVariable<*>> T.get
        get() = this

    override val container: ValuesContainer = ValuesContainer()
}