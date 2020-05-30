package com.marshalldbrain.pulsar.core.empires.colonies.districts

import com.marshalldbrain.pulsar.core.empires.colonies.construction.Buildable
import com.marshalldbrain.pulsar.core.resources.ResourceType

data class DistrictType (
    val id: String,
    val buildTime: Int,
    val cost: Map<ResourceType, Int>,
    val production: Map<ResourceType, Int>,
    val upkeep: Map<ResourceType, Int>,
    private val possible: () -> Boolean,
    private val starting: () -> Boolean
) : Buildable {

    fun possible(): Boolean {
        return possible.invoke()
    }

    fun starting(): Boolean {
        return starting.invoke()
    }

}