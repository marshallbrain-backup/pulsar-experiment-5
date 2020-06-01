package com.marshalldbrain.pulsar.core.empires.colonies.construction

import com.marshalldbrain.pulsar.core.resources.ResourceType

interface BuildTask {

    val target: Buildable
    val type: BuildType
    val timeUnit: Int
    val costUnit: Map<ResourceType, Int>
    val amount: Int
    val amountLeft: Int
    val timeUnitLeft: Int
    val timeLeft: Int
    val isDone: Boolean

}

internal class BuildTaskImpl(
    override val target: Buildable,
    override val type: BuildType,
    override val timeUnit: Int,
    override val costUnit: Map<ResourceType, Int>,
    override val amount: Int,
    private val onComplete: () -> Unit
) : BuildTask {

    override var timeUnitLeft: Int = timeUnit
        private set
    override var amountLeft: Int = amount
        private set

    override val timeLeft: Int = (amountLeft - 1) * timeUnit + timeUnitLeft

    override val isDone: Boolean
        get() {
            return amountLeft == 0
        }

    fun build(buildAmount: Int): Int {
        timeUnitLeft -= buildAmount
        while (timeUnitLeft <= 0 && !isDone) {
            onComplete.invoke()
            amountLeft--
            if (!isDone) {
                timeUnitLeft += timeUnit
            }
        }
        //TODO change to number.flipSign
        return timeUnitLeft * -1
    }

}


