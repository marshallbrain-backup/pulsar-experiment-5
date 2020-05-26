package com.marshalldbrain.pulsar.core.empires.colonies.districts

class DistrictOverseer (private val DistrictTypes: Set<DistrictType>) {

    private val max = 25
    private val maxSlots = 4
    private val allocation = mutableMapOf<DistrictType, Int>()

    init {

        val possible = getPossibleDistricts()
        val starting = possible.filter {
            it.starting()
        }

        starting.associateWithTo(allocation) { 0 }

    }

    private fun getPossibleDistricts(): List<DistrictType> =
        DistrictTypes.filter {
            it.possible()
        }

}