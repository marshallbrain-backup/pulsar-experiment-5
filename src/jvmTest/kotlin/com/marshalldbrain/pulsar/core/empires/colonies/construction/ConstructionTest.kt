package com.marshalldbrain.pulsar.core.empires.colonies.construction

import com.marshalldbrain.pulsar.core.empires.colonies.Colony
import com.marshalldbrain.pulsar.core.initConstructionTest
import com.marshalldbrain.pulsar.core.universe.Body
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class ConstructionTest : FunSpec({

    val body = Body("testBody1", 10, 0, true)

    context("Create task") {

        val build = initConstructionTest().getValue("free")
        val colony = Colony(setOf(build), body)

        test("Add to current build tasks") {

            colony.createTask(build, BuildType.BUILD, 1)

            colony.currentTasks.forEach { task ->
                task.type shouldBe BuildType.BUILD
            }

        }

        test("Add to build tasks queue") {

            colony.createTask(build, BuildType.BUILD, 1)
            colony.createTask(build, BuildType.BUILD, 1)

            colony.buildQueue.forEach { task ->
                task.type shouldBe BuildType.BUILD
            }

        }

    }

    context("Construction possessed") {

        val build = initConstructionTest().getValue("free")
        val colony = Colony(setOf(build), body)

        test("Build task") {

            colony.createTask(build, BuildType.BUILD, 1)
            colony.tickDay(10)

            colony.districts[build] shouldBe 1
            colony.currentTasks.shouldBeEmpty()

        }

        test("Build task with overflow") {

            colony.createTask(build, BuildType.BUILD, 1)
            colony.createTask(build, BuildType.BUILD, 1)
            colony.tickDay(15)

            colony.districts[build] shouldBe 1
            colony.currentTasks[0].timeUnitLeft shouldBe 5
            colony.currentTasks[0].amountLeft shouldBe 1
            colony.buildQueue.shouldBeEmpty()

        }

        test("Build task with multiple overflow") {

            colony.createTask(build, BuildType.BUILD, 1)
            colony.createTask(build, BuildType.BUILD, 1)
            colony.createTask(build, BuildType.BUILD, 1)
            colony.tickDay(27)

            colony.districts[build] shouldBe 2
            colony.currentTasks[0].timeUnitLeft shouldBe 3
            colony.currentTasks[0].amountLeft shouldBe 1
            colony.buildQueue.shouldBeEmpty()

        }

        test("Build task with multiple") {

            colony.createTask(build, BuildType.BUILD, 3)
            colony.tickDay(30)

            colony.districts[build] shouldBe 3
            colony.currentTasks.shouldBeEmpty()

        }

        test("Build task with multiple and overflow") {

            colony.createTask(build, BuildType.BUILD, 2)
            colony.createTask(build, BuildType.BUILD, 1)
            colony.tickDay(25)

            colony.districts[build] shouldBe 2
            colony.currentTasks[0].timeUnitLeft shouldBe 5
            colony.currentTasks[0].amountLeft shouldBe 1
            colony.buildQueue.shouldBeEmpty()

        }

        test("Build task with multiple and overflow with multiple") {

            colony.createTask(build, BuildType.BUILD, 2)
            colony.createTask(build, BuildType.BUILD, 2)
            colony.tickDay(35)

            colony.districts[build] shouldBe 3
            colony.currentTasks[0].timeUnitLeft shouldBe 5
            colony.currentTasks[0].amountLeft shouldBe 1
            colony.buildQueue.shouldBeEmpty()

        }

        test("Multiple builds") {

            colony.createTask(build, BuildType.BUILD, 1)
            colony.tickDay(5)

            colony.districts[build] shouldBe 0
            colony.currentTasks[0].timeUnitLeft shouldBe 5
            colony.currentTasks[0].amountLeft shouldBe 1

            colony.tickDay(5)

            colony.districts[build] shouldBe 1
            colony.currentTasks.shouldBeEmpty()

        }

        test("Everything") {

            colony.createTask(build, BuildType.BUILD, 2)
            colony.createTask(build, BuildType.BUILD, 3)
            colony.createTask(build, BuildType.BUILD, 1)

            colony.tickDay(14)
            colony.tickDay(23)
            colony.tickDay(2)
            colony.tickDay(16)

            colony.districts[build] shouldBe 5
            colony.currentTasks[0].timeUnitLeft shouldBe 5
            colony.currentTasks[0].amountLeft shouldBe 1

        }

    }

    context("Cost handling") {

    }

    context("0 time auto completes") {

        val build = initConstructionTest().getValue("0time")
        val colony = Colony(setOf(build), body)

        test("build") {

            colony.createTask(build, BuildType.BUILD, 1)

            colony.districts[build] shouldBe 1

        }

    }

}) {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
}