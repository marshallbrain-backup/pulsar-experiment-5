package com.marshalldbrain.pulsar.core

import com.marshalldbrain.pulsar.core.empires.colonies.districts.DistrictType
import com.marshalldbrain.pulsar.core.resources.ResourceType

fun initDistrictTypes(): Map<String, DistrictType> {

    return setOf(
        DistrictType(
            "possibleTrue",
            0,
            emptyMap(),
            emptyMap(),
            emptyMap(),
            0,
            emptyMap(),
            returnTrue,
            returnTrue
        ),
        DistrictType(
            "possibleFalse",
            0,
            emptyMap(),
            emptyMap(),
            emptyMap(),
            0,
            emptyMap(),
            returnFalse,
            returnFalse
        ),
        DistrictType(
            "tool",
            0,
            emptyMap(),
            emptyMap(),
            emptyMap(),
            10,
            mapOf(Pair(minerals, 10)),
            returnTrue,
            returnFalse
        ),
        DistrictType(
            "blocking",
            10,
            emptyMap(),
            emptyMap(),
            emptyMap(),
            10,
            mapOf(Pair(minerals, 10)),
            returnTrue,
            returnTrue
        )
    ).associateBy { it.id }

}

fun initConstructionTest(): Map<String, DistrictType> {

    return setOf(
        DistrictType(
            "free",
            10,
            emptyMap(),
            emptyMap(),
            emptyMap(),
            0,
            emptyMap(),
            returnTrue,
            returnTrue
        ),
        DistrictType(
            "0time",
            0,
            emptyMap(),
            emptyMap(),
            emptyMap(),
            0,
            emptyMap(),
            returnTrue,
            returnTrue
        )
    ).associateBy { it.id }

}

fun initResourceTest(): Map<String, DistrictType> {

    return setOf(
        DistrictType(
            "production",
            0,
            emptyMap(),
            mapOf(Pair(energy, 10)),
            emptyMap(),
            0,
            emptyMap(),
            returnTrue,
            returnTrue
        ),
        DistrictType(
            "upkeep",
            0,
            emptyMap(),
            emptyMap(),
            mapOf(Pair(energy, 10)),
            0,
            emptyMap(),
            returnTrue,
            returnTrue
        ),
        DistrictType(
            "operation",
            0,
            emptyMap(),
            mapOf(Pair(food, 10)),
            mapOf(Pair(energy, 10)),
            0,
            emptyMap(),
            returnTrue,
            returnTrue
        )
    ).associateBy { it.id }

}

val returnTrue: () -> Boolean = {
    true
}

val returnFalse: () -> Boolean = {
    false
}

val energy = ResourceType("energy")
val food = ResourceType("food")
val minerals = ResourceType("minerals")