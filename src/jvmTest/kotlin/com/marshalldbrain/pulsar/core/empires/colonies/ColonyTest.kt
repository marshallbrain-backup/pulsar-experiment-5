package com.marshalldbrain.pulsar.core.empires.colonies

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ColonyTest : FunSpec({

    test("test") {
        "sammy".length shouldBe 5
    }

}) {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
}
