package net.bdew.lib.container

import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent

object ContainerEventListener {
  def register(): Unit = {
    NeoForge.EVENT_BUS.addListener(this.containerOpen)
    NeoForge.EVENT_BUS.addListener(this.containerClosed)
  }

  private def containerOpen(event: PlayerContainerEvent.Open): Unit = {
    (event.getEntity, event.getContainer) match {
      case (player: ServerPlayer, cont: NoInvContainer) => cont.playerAdded(player)
      case _ => // no op
    }
  }

  private def containerClosed(event: PlayerContainerEvent.Close): Unit = {
    (event.getEntity, event.getContainer) match {
      case (player: ServerPlayer, cont: NoInvContainer) => cont.playerRemoved(player)
      case _ => // no op
    }
  }
}
