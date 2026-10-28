package net.bdew.lib

import net.bdew.lib.nbt.converters._
import net.bdew.lib.rich._
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.{BlockGetter, Level}
import net.neoforged.neoforge.client.model.data.ModelData

import scala.language.implicitConversions

/**
 * This object provides a bunch of implicits that makes minecraft classes nicer to work with
 * It also includes definitions that makes working with them in NBT data easier
 */
object PimpVanilla {
  implicit def pimpBlockPos(p: BlockPos): RichBlockPos = new RichBlockPos(p)
  implicit def pimpWorld(p: Level): RichWorld = new RichWorld(p)
  implicit def pimpBlockReader(p: BlockGetter): RichBlockReader = new RichBlockReader(p)
  implicit def pimpNBT(p: CompoundTag): RichCompoundNBT = new RichCompoundNBT(p)
  implicit def pimpBlockState(p: BlockState): RichBlockState = new RichBlockState(p)
  implicit def pimpModelData(p: ModelData): RichModelData = new RichModelData(p)

  /** ItemStack NBT compatibility on top of the 1.20.5+ custom_data component. */
  implicit class RichItemStack(val stack: ItemStack) extends AnyVal {
    def hasTag: Boolean = stack.has(DataComponents.CUSTOM_DATA)

    def getTag: CompoundTag = {
      val data = stack.get(DataComponents.CUSTOM_DATA)
      if (data == null) new CompoundTag() else data.getUnsafe
    }

    def getOrCreateTag(): CompoundTag = {
      val data = stack.get(DataComponents.CUSTOM_DATA)
      if (data != null) data.getUnsafe
      else {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag()))
        stack.get(DataComponents.CUSTOM_DATA).getUnsafe
      }
    }

    def setTag(tag: CompoundTag): Unit =
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
  }

  // Helpers for NBT stuff

  implicit val tBlockPos: TBlockPos.type = TBlockPos
  implicit val tBoolean: TBoolean.type = TBoolean
  implicit val tFluid: TFluid.type = TFluid
  implicit val tFluidStack: TFluidStack.type = TFluidStack
  implicit val tItemStack: TItemStack.type = TItemStack
  implicit val tDirection: TDirection.type = TDirection
}
