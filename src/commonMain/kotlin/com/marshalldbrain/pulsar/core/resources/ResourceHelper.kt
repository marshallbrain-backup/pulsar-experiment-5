package com.marshalldbrain.pulsar.core.resources

internal class ResourceHelper {

    private val modifiers = mutableMapOf<ResourcePath, MutableList<Float>>()
    private val amounts = mutableMapOf<ResourcePath, Int>()

    val resourceModifiers: Map<ResourcePath, List<Float>>
        get() = modifiers
    val resourceAmounts: Map<ResourcePath, Int>
        get() = amounts

    fun add(resourceDelta: Map<ResourcePath, Int>) {

        resourceDelta.forEach {
            amounts[it.key] = amounts.getOrElse(it.key) {0} + it.value
        }

    }

}