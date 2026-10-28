package net.bdew.lib.rich

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter

import scala.reflect.ClassTag

class RichBlockReader(val v: BlockGetter) extends AnyVal {
  def getTileSafe[T: ClassTag](p: BlockPos): Option[T] = {
    val tile = v.getBlockEntity(p)
    if (tile != null && implicitly[ClassTag[T]].runtimeClass.isInstance(tile))
      Some(tile.asInstanceOf[T])
    else
      None
  }

  def getBlockSafe[T: ClassTag](p: BlockPos): Option[T] = {
    val block = v.getBlockState(p).getBlock
    if (block != null && implicitly[ClassTag[T]].runtimeClass.isInstance(block))
      Some(block.asInstanceOf[T])
    else
      None
  }

}
