package com.marshalldbrain.pulsar.core.universe

data class Body(
    val id: String,
    val size: Int,
    val orbit: Int,
    val habitable: Boolean
)