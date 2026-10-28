package net.bdew.lib.multiblock.render

import net.minecraft.core.Direction
import net.neoforged.neoforge.client.model.data.ModelProperty

/**
 * Used for rendering output overlays, the integer will be used as tint index for coloring
 */
object OutputFaceProperty extends ModelProperty[Map[Direction, Int]]
