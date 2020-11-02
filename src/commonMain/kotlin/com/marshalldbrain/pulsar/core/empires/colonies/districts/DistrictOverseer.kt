package com.marshalldbrain.pulsar.core.empires.colonies.districts

import com.marshalldbrain.ion.collections.Queue
import com.marshalldbrain.pulsar.core.empires.colonies.construction.BuildTask
import com.marshalldbrain.pulsar.core.empires.colonies.construction.BuildTaskImpl
import com.marshalldbrain.pulsar.core.empires.colonies.construction.BuildType
import com.marshalldbrain.pulsar.core.resources.ResourcePath
import com.marshalldbrain.pulsar.core.resources.ResourceType

class DistrictOverseer (
    private val DistrictTypes: Set<DistrictType>,
    private val properties: DistrictProperties
) {

    private var allocationPlanned = 0

    private val allocation = mutableMapOf<DistrictType, Int>()
    private val resourceDelta = mutableMapOf<ResourcePath, Int>()
    private val notImplementedMessage = "has not been implemented as possible construction types for " +
            "districts but plans to be implemented in the future."

    val allocated: Int
        get() = allocation.values.sum() + allocationPlanned
    val allocatedSlots: Int
        get() = allocation.size
    val districts: Map<DistrictType, Int>
        get() = allocation
    val delta: Map<ResourcePath, Int>
        get() = resourceDelta.toMap()

    init {

        val possible = getPossibleDistricts()
        val starting = possible.filter {
            it.starting()
        }

        starting.associateWithTo(allocation) { 0 }

    }

    private fun getPossibleDistricts(): List<DistrictType> = DistrictTypes.filter { it.possible() }

    internal fun check(target: DistrictType, type: BuildType, amount: Int, replace: DistrictType?): Boolean {

        if (target.possible()) {
            return when(type) {
                BuildType.BUILD -> {
                    target in allocation
                            && allocated + amount <= properties.districtCount
                }
                BuildType.DESTROY -> {
                    target in allocation
                            && allocation.getValue(target) - amount >= 0
                }
                BuildType.TOOL -> {
                    (target !in allocation)
                            && ((replace == null && allocatedSlots + 1 < properties.districtSlots)
                                    || replace in allocation)
                }
                BuildType.REPLACE -> {
                    (target in allocation)
                            && (replace != null && replace in allocation && allocation.getValue(replace) - amount >= 0)
                }
                else -> false
            }
        }

        return false

    }

    internal fun createOrder(target: DistrictType, type: BuildType, amount: Int, replace: DistrictType? = null): BuildTaskImpl {

        //TODO update other types with same logic used in build type
        return when(type) {
            BuildType.BUILD -> {

                allocationPlanned += amount

                val onComplete = {
                    allocation[target] = allocation.getValue(target) + 1
                    allocationPlanned--

                    updateResources(target.id, target.production)
                    updateResources(target.id, target.upkeep, -1)
                }

                BuildTaskImpl(type, target.buildTime, target.cost, amount, onComplete)

            }
            BuildType.DESTROY -> {
                BuildTaskImpl(type, target.buildTime, target.cost, amount) {
                    allocation[target] = allocation.getValue(target) - 1
                    updateResources(target.id, target.production, -1)
                    updateResources(target.id, target.upkeep)
                }
            }
            BuildType.TOOL -> {
                if (replace == null) {
                    BuildTaskImpl(type, 0, emptyMap(),1) {
                        allocation[target] = 0
                    }
                } else {
                    BuildTaskImpl(type, target.toolTime, target.toolCost,1) {
                        allocation.remove(replace)
                        allocation[target] = 0
                    }
                }
            }
            BuildType.REPLACE -> {
                if (replace != null) {
                    BuildTaskImpl(type, target.buildTime, target.cost, amount) {
                        allocation[target] = allocation.getValue(target) + 1
                        updateResources(target.id, target.production)
                        updateResources(target.id, target.upkeep, -1)
                        allocation[replace] = allocation.getValue(replace) - 1
                        updateResources(replace.id, replace.production, -1)
                        updateResources(replace.id, replace.upkeep)
                    }
                } else {
                    throw NullPointerException("Replace is null")
                }
            }
            BuildType.UPGRADE, BuildType.DOWNGRADE -> {
                throw NotImplementedError("$type $notImplementedMessage")
            }
            else -> throw UnsupportedOperationException("$type is not a supported build type for districts.")
        }

    }

    private fun updateResources(
        id: String,
        resources: Map<ResourceType, Int>,
        multiplier: Int = 1
    ) {
        resources.forEach {
            val path = ResourcePath(it.key, id)
            resourceDelta[path] = resourceDelta.getOrPut(path) {0} + it.value * multiplier
        }
    }

}