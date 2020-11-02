package com.marshalldbrain.pulsar.core.empires.colonies.districts

import com.marshalldbrain.pulsar.core.empires.colonies.Colony
import com.marshalldbrain.pulsar.core.empires.colonies.construction.BuildType
import com.marshalldbrain.pulsar.core.energy
import com.marshalldbrain.pulsar.core.food
import com.marshalldbrain.pulsar.core.initDistrictTypes
import com.marshalldbrain.pulsar.core.initResourceTest
import com.marshalldbrain.pulsar.core.resources.ResourcePath
import com.marshalldbrain.pulsar.core.universe.Body
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.*
import io.kotest.matchers.shouldBe

class DistrictTest : FunSpec({

    val body = Body("testBody1", 10, 0, true)

    context("Check task") {

        val districts = initDistrictTypes()
        val possible = districts.getValue("possibleTrue")
        val notPossible = districts.getValue("possibleFalse")
        val tool = districts.getValue("tool")
        val blocking = districts.getValue("blocking")

        val colony = Colony(districts.values.toSet(), body)

        test("District is possible") {
            colony.checkOrderPossible(possible, BuildType.BUILD, 1) shouldBe true
        }

        test("District is not possible") {
            colony.checkOrderPossible(notPossible, BuildType.BUILD, 1) shouldBe false
        }

        context("Supported built types") {

            context("Build") {

                test("Possible") {
                    colony.checkOrderPossible(possible, BuildType.BUILD, 1) shouldBe true
                }

                test("To many districts") {
                    colony.checkOrderPossible(possible, BuildType.BUILD, 11) shouldBe false
                }

                test("To many districts while constructing") {
                    colony.createTask(blocking, BuildType.BUILD, 6)
                    colony.checkOrderPossible(blocking, BuildType.BUILD, 6) shouldBe false

                }

                test("Completed") {
                    colony.createTask(possible, BuildType.BUILD, 1)

                    colony.districts shouldContainKey possible
                    colony.districts[possible] shouldBe 1
                }

            }

            context("Destroy") {

                test("Possible") {
                    colony.createTask(possible, BuildType.BUILD, 1)
                    colony.checkOrderPossible(possible, BuildType.DESTROY, 1) shouldBe true
                }

                test("To few districts") {
                    colony.checkOrderPossible(possible, BuildType.DESTROY, 1) shouldBe false
                }

                test("To few districts while constructing") {
                    colony.createTask(possible, BuildType.BUILD, 1)
                    colony.createTask(blocking, BuildType.DESTROY, 1)
                    colony.checkOrderPossible(blocking, BuildType.DESTROY, 1) shouldBe false

                }

                test("Completed") {
                    colony.createTask(possible, BuildType.BUILD, 1)
                    colony.createTask(possible, BuildType.DESTROY, 1)

                    colony.districts shouldContainKey possible
                    colony.districts[possible] shouldBe 0
                }

            }

            context("Tool") {

                test("Possible empty slot") {
                    colony.checkOrderPossible(tool, BuildType.TOOL) shouldBe true
                }

                test("Possible existing slot") {
                    colony.checkOrderPossible(tool, BuildType.TOOL, replace = possible) shouldBe true
                }

                test("Target is not possible") {
                    colony.checkOrderPossible(notPossible, BuildType.TOOL, replace = possible) shouldBe false
                }

                test("Replace is not tooled") {
                    colony.checkOrderPossible(tool, BuildType.TOOL, replace = notPossible) shouldBe false
                }

                test("To many tooled districts") {
                    colony.createTask(initResourceTest().getValue("production"), BuildType.TOOL)
                    colony.createTask(initResourceTest().getValue("upkeep"), BuildType.TOOL)

                    colony.checkOrderPossible(tool, BuildType.TOOL) shouldBe false
                }

                test("Completed empty slot") {
                    colony.createTask(tool, BuildType.TOOL)

                    colony.districts shouldContainKey tool
                    colony.districts[tool] shouldBe 0
                }

                test("Completed existing slot") {
                    colony.createTask(tool, BuildType.TOOL, replace = possible)
                    colony.tickDay(10)

                    colony.districts shouldNotContainKey possible
                    colony.districts shouldContainKey tool
                    colony.districts[tool] shouldBe 0
                }
            }

            context("Replace") {

                colony.createTask(tool, BuildType.TOOL, 1)

                test("Possible") {
                    colony.createTask(possible, BuildType.BUILD, 1)
                    colony.checkOrderPossible(tool, BuildType.REPLACE, 1, possible) shouldBe true
                }

                test("Target is not tooled") {
                    colony.checkOrderPossible(notPossible, BuildType.REPLACE, 1, possible) shouldBe false
                }

                test("Replace has to few districts") {
                    colony.checkOrderPossible(tool, BuildType.REPLACE, 1, possible) shouldBe false
                }

                test("Replace is null") {
                    colony.checkOrderPossible(tool, BuildType.REPLACE, 1, null) shouldBe false
                }

                test("Replace is not tooled") {
                    colony.checkOrderPossible(tool, BuildType.REPLACE, 1, notPossible) shouldBe false
                }

                test("Completed") {
                    colony.createTask(possible, BuildType.BUILD, 1)
                    colony.createTask(tool, BuildType.REPLACE, 1, possible)

                    colony.districts shouldContainKey possible
                    colony.districts shouldContainKey tool
                    colony.districts[possible] shouldBe 0
                    colony.districts[tool] shouldBe 1
                }

            }

        }

        test("Unsupported build types") {

        }

    }

    context("ResourceDelta updates") {

        context("Production") {

            val build = initResourceTest().getValue("production")
            val colony = Colony(setOf(build), body)

            test("One built") {

                colony.createTask(build, BuildType.BUILD, 1)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(energy, build.id), 10)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple amounts built") {

                colony.createTask(build, BuildType.BUILD, 4)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(energy, build.id), 40)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple tasks built") {

                colony.createTask(build, BuildType.BUILD, 1)
                colony.createTask(build, BuildType.BUILD, 1)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(energy, build.id), 20)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple amounts and tasks built") {

                colony.createTask(build, BuildType.BUILD, 3)
                colony.createTask(build, BuildType.BUILD, 2)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(energy, build.id), 50)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

        }

        context("Upkeep") {

            val build = initResourceTest().getValue("upkeep")
            val colony = Colony(setOf(build), body)

            test("One built") {

                colony.createTask(build, BuildType.BUILD, 1)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(energy, build.id), -10)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple amounts built") {

                colony.createTask(build, BuildType.BUILD, 4)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(energy, build.id), -40)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple tasks built") {

                colony.createTask(build, BuildType.BUILD, 1)
                colony.createTask(build, BuildType.BUILD, 1)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(energy, build.id), -20)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple amounts and tasks built") {

                colony.createTask(build, BuildType.BUILD, 3)
                colony.createTask(build, BuildType.BUILD, 2)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(energy, build.id), -50)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

        }

        context("Production and upkeep") {

            val build = initResourceTest().getValue("operation")
            val colony = Colony(setOf(build), body)

            test("One built") {

                colony.createTask(build, BuildType.BUILD, 1)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(food, build.id), 10),
                    Pair(ResourcePath(energy, build.id), -10)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple amounts built") {

                colony.createTask(build, BuildType.BUILD, 4)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(food, build.id), 40),
                    Pair(ResourcePath(energy, build.id), -40)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple tasks built") {

                colony.createTask(build, BuildType.BUILD, 1)
                colony.createTask(build, BuildType.BUILD, 1)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(food, build.id), 20),
                    Pair(ResourcePath(energy, build.id), -20)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

            test("Multiple amounts and tasks built") {

                colony.createTask(build, BuildType.BUILD, 3)
                colony.createTask(build, BuildType.BUILD, 2)
                colony.tickMonth()

                val expected = mapOf(
                    Pair(ResourcePath(food, build.id), 50),
                    Pair(ResourcePath(energy, build.id), -50)
                )

                colony.resourceAmounts.shouldContainAll(expected)

            }

        }

    }

}) {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
}