package com.marshalldbrain.pulsar.core.empires.colonies.construction

import com.marshalldbrain.ion.collections.Queue
import com.marshalldbrain.ion.collections.queueOf

class ConstructionManager {

    private val tasks = mutableListOf<BuildTaskImpl>()
    val currentTasks: List<BuildTask>
        get() = tasks.toList()
    private val queue = queueOf<BuildTaskImpl>()
    val buildQueue: List<BuildTask>
        get() = queue.toList()

    internal fun add(task: BuildTaskImpl) {
        if (tasks.isEmpty()) {
            tasks.add(task)
        } else {
            queue.push(task)
        }
    }

}