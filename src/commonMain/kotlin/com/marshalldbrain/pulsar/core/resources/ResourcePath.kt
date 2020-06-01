package com.marshalldbrain.pulsar.core.resources

data class ResourcePath(
    val type: ResourceType,
    val path: String
) {
}