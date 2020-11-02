package com.marshalldbrain.pulsar.core.empires.colonies.construction

import com.marshalldbrain.ion.ImpossibleStateException
import com.marshalldbrain.ion.collections.queueOf

internal class ConstructionManager {

    private val tasks = mutableSetOf<BuildTaskImpl>()
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
        //TODO possible bug with task building twice.
        //Fix will be to simply clone tasks before the loop but will not do unless necessary
        for (task in tasks) {
            var currentTask = task
            var buildRemaining = timePassed

            while (buildRemaining > 0 && tasks.isNotEmpty()) {
                buildRemaining = currentTask.build(buildRemaining)
                if (buildRemaining >= 0 && currentTask.isDone) {
                    tasks.remove(currentTask)
                    if (tasks.isNotEmpty()) {
                        throw ImpossibleStateException()
                    }
                    if (queue.isNotEmpty()) {
                        currentTask = queue.pop()
                        tasks.add(currentTask)
                    }
                } else if ((buildRemaining < 0 && currentTask.isDone) || (buildRemaining >= 0 && !currentTask.isDone)) {
                    throw ImpossibleStateException()
                }

            }

        }

    }

}