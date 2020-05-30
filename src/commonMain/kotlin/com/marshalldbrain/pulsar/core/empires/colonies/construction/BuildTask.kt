package com.marshalldbrain.pulsar.core.empires.colonies.construction

import com.marshalldbrain.pulsar.core.resources.ResourceType

interface BuildTask {
}

class BuildTaskImpl(
    target: Buildable,
    type: BuildType,
    timeUnit: Int,
    costUnit: Map<ResourceType, Int>,
    amount: Int,
    onComplete: () -> Unit
) : BuildTask {
}


