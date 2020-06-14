package com.marshalldbrain.pulsar.core.empires.colonies.construction

import com.marshalldbrain.ion.ImpossibleStateException
import com.marshalldbrain.ion.collections.queueOf
import com.marshalldbrain.pulsar.core.resources.ResourceHelper
import com.marshalldbrain.pulsar.core.resources.ResourceType
import kotlin.reflect.KFunction3

internal class ConstructionManager {

    private val tasks = mutableListOf<BuildTaskImpl>()
    val currentTasks: List<BuildTask>
        get() = tasks.toList()
    private val queue = queueOf<BuildTaskImpl>()
    val buildQueue: List<BuildTask>
        get() = queue.toList()

    fun add(
        task: BuildTaskImpl
    ) {
        when {
            task.timeLeft == 0 -> task.build(0)
            tasks.isEmpty() -> tasks.add(task)
            else -> queue.push(task)
        }
    }

    fun processTime(timePassed: Int) {
        var buildRemaining = timePassed

        while (buildRemaining > 0 && tasks.isNotEmpty()) {
            buildRemaining = tasks[0].build(buildRemaining)
            if (buildRemaining >= 0 && tasks[0].isDone) {
                tasks.removeAt(0)
                if (tasks.isNotEmpty()) {
                    throw ImpossibleStateException()
                }
                if (queue.isNotEmpty()) {
                    tasks.add(queue.pop())
                }
            } else if ((buildRemaining < 0 && tasks[0].isDone) || (buildRemaining >= 0 && !tasks[0].isDone)) {
                throw ImpossibleStateException()
            }

        }

    }

}