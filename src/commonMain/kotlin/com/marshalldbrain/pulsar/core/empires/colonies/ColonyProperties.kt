package com.marshalldbrain.pulsar.core.empires.colonies

import com.marshalldbrain.pulsar.core.empires.colonies.districts.DistrictProperties
import com.marshalldbrain.pulsar.core.universe.Body

class ColonyProperties (
    private val parentBody: Body
) : DistrictProperties {

    override val districtCount
        get() = parentBody.size
    override val districtSlots
        get() = 4

}