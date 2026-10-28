package net.bdew.lib.capabilities

import net.minecraft.core.Direction

class SidedCapHandler[T <: AnyRef](factory: Direction => T, allowNull: Boolean = true) {
  val handlers: Map[Direction, T] = (for (side <- Direction.values.toList :+ null) yield {
    if (allowNull || side != null)
      side -> factory(side)
    else
      side -> null.asInstanceOf[T]
  }).toMap

  def get(side: Direction): T = handlers.getOrElse(side, null.asInstanceOf[T])
  def invalidate(): Unit = {}
}
