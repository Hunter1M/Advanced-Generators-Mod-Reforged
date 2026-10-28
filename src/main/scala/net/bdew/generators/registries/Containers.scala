package net.bdew.generators.registries

import net.bdew.generators.controllers.exchanger.ContainerExchanger
import net.bdew.generators.controllers.steam.ContainerSteamTurbine
import net.bdew.generators.controllers.syngas.ContainerSyngas
import net.bdew.generators.controllers.turbine.ContainerFuelTurbine
import net.bdew.generators.gui.ContainerOutputConfig
import net.bdew.generators.modules.control.ContainerControl
import net.bdew.generators.modules.sensor.ContainerSensor
import net.bdew.lib.managers.ContainerManager
import net.bdew.lib.registries.RegistryObject
import net.minecraft.world.inventory.MenuType

object Containers extends ContainerManager {
  val fuelTurbine: RegistryObject[MenuType[ContainerFuelTurbine]] =
    registerPositional("fuel_turbine", Machines.controllerFuelTurbine.teType) {
      (id, inv, te) => new ContainerFuelTurbine(te, inv, id)
    }

  val steamTurbine: RegistryObject[MenuType[ContainerSteamTurbine]] =
    registerPositional("steam_turbine", Machines.controllerSteamTurbine.teType) {
      (id, inv, te) => new ContainerSteamTurbine(te, inv, id)
    }

  val syngas: RegistryObject[MenuType[ContainerSyngas]] =
    registerPositional("syngas", Machines.controllerSyngas.teType) {
      (id, inv, te) => new ContainerSyngas(te, inv, id)
    }

  val exchanger: RegistryObject[MenuType[ContainerExchanger]] =
    registerPositional("exchanger", Machines.controllerExchanger.teType) {
      (id, inv, te) => new ContainerExchanger(te, inv, id)
    }

  val control: RegistryObject[MenuType[ContainerControl]] =
    registerPositional("control", Machines.moduleControl.teType) {
      (id, inv, te) => new ContainerControl(te, inv, id)
    }

  val sensor: RegistryObject[MenuType[ContainerSensor]] =
    registerPositional("sensor", Machines.moduleSensor.teType) {
      (id, inv, te) => new ContainerSensor(te, inv, id)
    }

  val outputConfig: RegistryObject[MenuType[ContainerOutputConfig]] =
    registerSimple("output_config") { (_, _, _) => new ContainerOutputConfig() }
}
