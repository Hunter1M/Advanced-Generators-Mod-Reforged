package net.bdew.generators.integration.ic2c

import ic2.api.energy.EnergyNet

sealed case class IC2Tier(number: Int, name: String) {
  lazy val maxPower: Double = EnergyNet.instance.getPowerFromTier(number)
}

object IC2Tier {
  val LV: IC2Tier = IC2Tier(1, "LV")
  val MV: IC2Tier = IC2Tier(2, "MV")
  val HV: IC2Tier = IC2Tier(3, "HV")
  val EV: IC2Tier = IC2Tier(4, "EV")
  val IV: IC2Tier = IC2Tier(5, "IV")
}
