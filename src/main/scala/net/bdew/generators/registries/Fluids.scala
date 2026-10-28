package net.bdew.generators.registries

import net.bdew.generators.Generators
import net.bdew.lib.managers.FluidManager
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.{FluidTags, TagKey}
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.{FluidType, BaseFlowingFluid}

import java.util.function.Consumer

object Fluids extends FluidManager(Blocks, Items) {
  def gasProps: FluidType.Properties = FluidType.Properties.create()
    .density(-10)

  val syngas: Fluids.FluidDef[FluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing] =
    define("syngas", () => new FluidType(gasProps.descriptionId("block.advgenerators.syngas")) {

      override def initializeClient(consumer: Consumer[IClientFluidTypeExtensions]): Unit = {
        consumer.accept(new IClientFluidTypeExtensions() {
          private val MILK_STILL = ResourceLocation.fromNamespaceAndPath(Generators.ModId, "block/syngas/still")
          private val MILK_FLOW = ResourceLocation.fromNamespaceAndPath(Generators.ModId, "block/syngas/flowing")
          override def getStillTexture: ResourceLocation = MILK_STILL
          override def getFlowingTexture: ResourceLocation = MILK_FLOW
        })
      }
    },
      new BaseFlowingFluid.Source(_),
      new BaseFlowingFluid.Flowing(_),
    )

  val steam: Fluids.FluidDef[FluidType, BaseFlowingFluid.Source, BaseFlowingFluid.Flowing] =
    define("steam", () => new FluidType(gasProps.descriptionId("block.advgenerators.steam")) {
      override def initializeClient(consumer: Consumer[IClientFluidTypeExtensions]): Unit = {
        consumer.accept(new IClientFluidTypeExtensions() {
          private val STILL = ResourceLocation.fromNamespaceAndPath(Generators.ModId, "block/steam/still")
          private val FLOW = ResourceLocation.fromNamespaceAndPath(Generators.ModId, "block/steam/flowing")
          override def getStillTexture: ResourceLocation = STILL
          override def getFlowingTexture: ResourceLocation = FLOW
        })
      }
    },
      new BaseFlowingFluid.Source(_),
      new BaseFlowingFluid.Flowing(_),
    )

  val steamTag: TagKey[Fluid] = FluidTags.create(ResourceLocation.parse("c:steam"))
}