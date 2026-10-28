package net.bdew.lib

import net.bdew.lib.commands.{CommandDumpRegistry, CommandOreDistribution}
import net.bdew.lib.container.ContainerEventListener
import net.bdew.lib.multiblock.network.MultiblockNetHandler
import net.bdew.lib.network.NetChannel
import net.bdew.lib.network.misc.MiscNetworkHandler
import net.bdew.lib.recipes.RecipeReloadListener
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.{AddReloadListenerEvent, RegisterCommandsEvent, TagsUpdatedEvent}
import org.apache.logging.log4j.{LogManager, Logger}

object BdLib {
  final val ModId = "bdlib"

  val log: Logger = LogManager.getLogger

  var modBus: IEventBus = _

  def logDebug(msg: String, args: Any*): Unit = log.debug(msg.format(args: _*))
  def logInfo(msg: String, args: Any*): Unit = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*): Unit = log.warn(msg.format(args: _*))
  def logError(msg: String, args: Any*): Unit = log.error(msg.format(args: _*))
  def logWarnException(msg: String, t: Throwable, args: Any*): Unit = log.warn(msg.format(args: _*), t)
  def logErrorException(msg: String, t: Throwable, args: Any*): Unit = log.error(msg.format(args: _*), t)

  def init(bus: IEventBus): Unit = {
    modBus = bus

    NeoForge.EVENT_BUS.addListener((event: RegisterCommandsEvent) => registerCommands(event))
    NeoForge.EVENT_BUS.addListener((event: AddReloadListenerEvent) => RecipeReloadListener.addReloadListener(event))
    NeoForge.EVENT_BUS.addListener((event: TagsUpdatedEvent) => RecipeReloadListener.tagsUpdated(event))

    bus.addListener((event: net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent) => NetChannel.registerAll(event))
    bus.addListener((event: net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent) => net.bdew.lib.capabilities.CapabilityRegistry.registerCapabilities(event))

    MultiblockNetHandler.init()
    MiscNetworkHandler.init()

    ContainerEventListener.register()
  }

  private def registerCommands(event: RegisterCommandsEvent): Unit = {
    event.getDispatcher.register(CommandOreDistribution.register)
    event.getDispatcher.register(CommandDumpRegistry.register)
  }
}
