package net.bdew.lib.managers

import net.minecraft.world.item.{BucketItem, Items}
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.fluids.{FluidType, BaseFlowingFluid}
import net.neoforged.neoforge.registries.NeoForgeRegistries
import net.minecraft.core.registries.BuiltInRegistries
import net.bdew.lib.registries.RegistryObject

class FluidManager(blocks: BlockManager, items: ItemManager) extends RegistryManager(BuiltInRegistries.FLUID) {
  val types = new RegistryManager(NeoForgeRegistries.Keys.FLUID_TYPES)

  def props: BlockBehaviour.Properties = BlockBehaviour.Properties.of()

  case class FluidDef[T <: FluidType, S <: BaseFlowingFluid.Source, F <: BaseFlowingFluid.Flowing](fluidType: RegistryObject[T], source: RegistryObject[S], flowing: RegistryObject[F])

  case class FluidDefBlock[T <: FluidType, S <: BaseFlowingFluid.Source, F <: BaseFlowingFluid.Flowing, BL <: LiquidBlock, BU <: BucketItem](fluidType: RegistryObject[T], source: RegistryObject[S], flowing: RegistryObject[F], block: RegistryObject[BL], bucket: RegistryObject[BU])

  def define[T <: FluidType, S <: BaseFlowingFluid.Source, F <: BaseFlowingFluid.Flowing, BL <: LiquidBlock](
                                                                                                                id: String,
                                                                                                                typeFactory: () => T,
                                                                                                                sourceFactory: BaseFlowingFluid.Properties => S,
                                                                                                                flowingFactory: BaseFlowingFluid.Properties => F,
                                                                                                                blockFactory: (() => BaseFlowingFluid) => BL,
                                                                                                              ): FluidDefBlock[T, S, F, BL, BucketItem] = {
    var props: BaseFlowingFluid.Properties = null
    val fluidType = types.register(id, typeFactory)
    val still = register(id, () => sourceFactory(props))
    val flowing = register(id + "_flowing", () => flowingFactory(props))
    val block = blocks.register(id, () => blockFactory(() => still.get()))
    val bucket = items.register(id + "_bucket", () => new BucketItem(still.get(),
      items.props.stacksTo(1).craftRemainder(Items.BUCKET)))
    props = new BaseFlowingFluid.Properties(() => fluidType.get(), () => still.get(), () => flowing.get())
    props.block(() => block.get()).bucket(() => bucket.get())
    FluidDefBlock(fluidType, still, flowing, block, bucket)
  }

  def define[T <: FluidType, S <: BaseFlowingFluid.Source, F <: BaseFlowingFluid.Flowing](
                                                                                             id: String,
                                                                                             typeFactory: () => T,
                                                                                             sourceFactory: BaseFlowingFluid.Properties => S,
                                                                                             flowingFactory: BaseFlowingFluid.Properties => F,
                                                                                           ): FluidDef[T, S, F] = {
    var props: BaseFlowingFluid.Properties = null
    val fluidType = types.register(id, typeFactory)
    val still = register(id, () => sourceFactory(props))
    val flowing = register(id + "_flowing", () => flowingFactory(props))
    props = new BaseFlowingFluid.Properties(() => fluidType.get(), () => still.get(), () => flowing.get())
    FluidDef(fluidType, still, flowing)
  }

  override def init(): Unit = {
    super.init()
    this.types.init()
  }
}