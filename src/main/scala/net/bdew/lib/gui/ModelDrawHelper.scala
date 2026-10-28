package net.bdew.lib.gui

import com.mojang.math.Axis
import net.bdew.lib.Client
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.level.BlockAndTintGetter
import net.neoforged.neoforge.client.model.data.ModelData

object ModelDrawHelper {

  /**
   * Draws a face from a block, as seen in the world into GUI.
   *
   * 1.21 renders blocks through RenderType buffers; renderSingleBlock handles
   * that for us, and the pose rotates the requested face towards the viewer.
   */
  def renderWorldBlockIntoGUI(graphics: net.minecraft.client.gui.GuiGraphics, world: BlockAndTintGetter, pos: BlockPos, face: Direction, rect: Rect): Unit = {
    val dispatcher = Client.minecraft.getBlockRenderer
    val blockState = world.getBlockState(pos)
    val pose = graphics.pose()

    pose.pushPose()
    pose.translate(rect.x1 + rect.w / 2, rect.y1 + rect.h / 2, 100.0F)
    pose.scale(rect.w, rect.h, 1)

    // GUI Y coordinates are top->down, while for blocks it's bottom->up, flip everything
    pose.mulPose(Axis.XP.rotationDegrees(180))

    face match {
      case Direction.NORTH => pose.mulPose(Axis.YP.rotationDegrees(180))
      case Direction.SOUTH => // nothing
      case Direction.WEST => pose.mulPose(Axis.YP.rotationDegrees(90))
      case Direction.EAST => pose.mulPose(Axis.YP.rotationDegrees(270))
      case Direction.UP => pose.mulPose(Axis.XP.rotationDegrees(90))
      case Direction.DOWN => pose.mulPose(Axis.XP.rotationDegrees(270))
      case _ =>
    }

    pose.translate(-0.5f, -0.5f, -0.5f)

    val te = world.getBlockEntity(pos)
    val data = if (te != null) te.getModelData else ModelData.EMPTY

    dispatcher.renderSingleBlock(blockState, pose, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, data, RenderType.solid())

    pose.popPose()
    graphics.flush()
  }
}
