package com.marshalldbrain.pulsar.core.empires.colonies.districts

import com.marshalldbrain.pulsar.core.empires.colonies.construction.BuildTask
import com.marshalldbrain.pulsar.core.empires.colonies.construction.BuildTaskImpl
import com.marshalldbrain.pulsar.core.empires.colonies.construction.BuildType
import com.marshalldbrain.pulsar.core.resources.ResourcePath
import com.marshalldbrain.pulsar.core.resources.ResourceType

class DistrictOverseer (private val DistrictTypes: Set<DistrictType>) {

    private val max = 25
    private val maxSlots = 4
    private val allocation = mutableMapOf<DistrictType, Int>()
    private val resourceDelta = mutableMapOf<ResourcePath, Int>()
    private val notImplementedMessage = "has not been implemented as possible construction types for " +
            "districts but plans to be implemented in the future."

    val allocated: Int
        get() = allocation.values.sum()
    val allocatedSlots: Int
        get() = allocation.size
    val districts: Map<DistrictType, Int>
        get() = allocation
    val delta: Map<ResourcePath, Int>
        get() {
            val delta = resourceDelta.toMap()
            resourceDelta.clear()
            return delta
        }

    init {

        val possible = getPossibleDistricts()
        val starting = possible.filter {
            it.starting()
        }

        starting.associateWithTo(allocation) { 0 }

    }

    private fun getPossibleDistricts(): List<DistrictType> = DistrictTypes.filter { it.possible() }

    internal fun check(target: DistrictType, type: BuildType, amount: Int): Boolean {

        if (target in getPossibleDistricts()) {
            return when(type) {
                BuildType.BUILD -> {
                    target in allocation && allocated + amount <= max
                }
                BuildType.DESTROY, BuildType.RETOOL, BuildType.UPGRADE -> {
                    throw NotImplementedError("$type $notImplementedMessage")
                }
                else -> false
            }
        }

        return false

    }

    internal fun createOrder(target: DistrictType, type: BuildType, amount: Int): BuildTaskImpl {

        return when(type) {
            BuildType.BUILD -> {
                BuildTaskImpl(target, type, target.buildTime, target.cost, amount) {
                    allocation[target] = allocation.getValue(target) + 1
                    incrementResources(target)
                }
            }
            BuildType.DESTROY, BuildType.RETOOL, BuildType.UPGRADE -> {
                throw NotImplementedError("$type $notImplementedMessage")
            }
            else -> throw UnsupportedOperationException("$type is not a supported build type for districts. " +
                    "This should also never be seen and is a bug if it is")
        }

    }

    private fun incrementResources(target: DistrictType) {
        target.production.forEach {
            val path = ResourcePath(it.key, target.id)
            resourceDelta[path] = resourceDelta.getOrPut(path) {0} + it.value
        }
        target.upkeep.forEach {
            val path = ResourcePath(it.key, target.id)
            resourceDelta[path] = resourceDelta.getOrPut(path) {0} - it.value
        }
    }

}