package com.marshalldbrain.pulsar.core.empires.colonies

import com.marshalldbrain.ion.collections.queueOf
import com.marshalldbrain.pulsar.core.empires.colonies.construction.*
import com.marshalldbrain.pulsar.core.empires.colonies.districts.DistrictOverseer
import com.marshalldbrain.pulsar.core.empires.colonies.districts.DistrictType

class Colony {

    private val constructionManager = ConstructionManager()
    private val districtOverseer = DistrictOverseer(emptySet())

    val currentTasks: List<BuildTask>
        get() = constructionManager.currentTasks
    val buildQueue: List<BuildTask>
        get() = constructionManager.buildQueue

    fun checkOrderPossible(target: Buildable, type: BuildType, amount: Int): Boolean {

        return when(target) {
            is DistrictType -> {
                districtOverseer.check(target, type, amount)
            }
            else -> false
        }

    }

    fun createTask(target: Buildable, type: BuildType, amount: Int) {

        if (checkOrderPossible(target, type, amount)) {

            val task = when(target) {
                is DistrictType -> {
                    districtOverseer.createOrder(target, type, amount)
                }
                else -> throw UnsupportedOperationException("$target is not supported. " +
                        "This should also never be seen and is a bug if it is")
            }

            constructionManager.add(task)

        }

    }

}