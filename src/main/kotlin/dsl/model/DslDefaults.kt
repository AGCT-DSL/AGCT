@file:Suppress("PackageDirectoryMismatch", "UNUSED_PARAMETER", "PropertyName")

package dsl

import model.utils.create

data class ValuesContainer internal constructor(
    val initialConcentration: DslConcentration = DslConcentration(),
    val degradationRate: DslRate = DslRate(),
    val basalRate: DslRate = DslRate(),
    val regulatedRate: DslRate = DslRate(),
    val bindingRate: DslRate = DslRate(),
    val unbindingRate: DslRate = DslRate()
)

abstract class DefaultValues internal constructor() {
    internal val initialConcentration
        get() = container.initialConcentration.get

    internal val degradationRate
        get() = container.degradationRate.get

    internal val basalRate
        get() = container.basalRate.get

    internal val regulatedRate
        get() = container.regulatedRate.get

    internal val bindingRate
        get() = container.bindingRate.get

    internal val unbindingRate
        get() = container.unbindingRate.get

    protected abstract val container: ValuesContainer

    protected abstract val <T : DslVariable> T.get : T
}

class ImmutableDefaultValues internal constructor(override val container: ValuesContainer) : DefaultValues() {
    override val <T : DslVariable> T.get: T
        get() = javaClass.kotlin.create(value)
}

class MutableDefaultValues internal constructor() : DefaultValues() {
    internal val immutable
        get() = ImmutableDefaultValues(container)

    override val <T : DslVariable> T.get
        get() = this

    override val container: ValuesContainer = ValuesContainer()
}