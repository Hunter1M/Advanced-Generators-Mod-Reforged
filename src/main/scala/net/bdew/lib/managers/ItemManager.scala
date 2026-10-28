package net.bdew.lib.managers

import net.minecraft.world.item.Item
import net.minecraft.core.registries.BuiltInRegistries
import net.bdew.lib.registries.RegistryObject

class ItemManager extends RegistryManager(BuiltInRegistries.ITEM) {
  val creativeTabs = new CreativeTabsManager

  def props: Item.Properties = new Item.Properties()

  def simple(id: String, props: Item.Properties): RegistryObject[Item] = {
    register(id, () => new Item(props))
  }

  override def init(): Unit = {
    super.init()
    creativeTabs.init()
  }
}
