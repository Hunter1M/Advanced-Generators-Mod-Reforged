package net.bdew.lib.network

import io.netty.buffer.Unpooled
import net.bdew.lib.Misc
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.resources.{ResourceKey, ResourceLocation}
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.{IPayloadContext, IPayloadHandler}
import net.neoforged.neoforge.server.ServerLifecycleHooks
import org.apache.logging.log4j.{LogManager, Logger}

import java.util.function.Supplier
import scala.collection.mutable
import scala.reflect.ClassTag

case class TargetPoint(x: Double, y: Double, z: Double, range: Double, dim: ResourceKey[Level])

class NetContext(val player: Player) {
  def getSender: ServerPlayer = player match {
    case p: ServerPlayer => p
    case _ => null
  }

  def setPacketHandled(v: Boolean): Unit = {}
}

class NetChannel(val modId: String, val name: String, val version: String) {
  val log: Logger = LogManager.getLogger

  val id: ResourceLocation = ResourceLocation.fromNamespaceAndPath(modId, name)
  val key: String = id.toString

  NetChannel.channels += key -> this

  type Message = BaseMessage[this.type]

  abstract class Codec[M <: Message : ClassTag] {
    val cls: Class[M] = implicitly[ClassTag[M]].runtimeClass.asInstanceOf[Class[M]]
    def encodeMsg(m: M, p: FriendlyByteBuf): Unit
    def decodeMsg(p: FriendlyByteBuf): M
  }

  private[network] val serverHandlers = mutable.Map.empty[Int, (FriendlyByteBuf, NetContext) => Unit]
  private[network] val clientHandlers = mutable.Map.empty[Int, FriendlyByteBuf => Unit]
  private val messageCodecs = mutable.Map.empty[Class[_], (Int, Codec[_])]

  def init(): Unit = {
    log.info(s"Initialized network channel '$name' for mod '$modId'")
  }

  def regServerHandler[M <: Message](id: Int, codec: Codec[M])(handler: (M, NetContext) => Unit): Unit = {
    messageCodecs += codec.cls -> (id, codec)
    serverHandlers += id -> ((buf, ctx) => handler(codec.decodeMsg(buf), ctx))
  }

  def regServerContainerHandler[M <: Message, C <: AbstractContainerMenu](id: Int, codec: Codec[M], cont: Class[C])(handler: (M, C, NetContext) => Unit): Unit = {
    regServerHandler(id, codec) { (msg, ctx) =>
      Misc.asInstanceOpt(ctx.player.containerMenu, cont) foreach (c => handler(msg, c, ctx))
    }
  }

  def regClientHandler[M <: Message](id: Int, codec: Codec[M])(handler: M => Unit): Unit = {
    messageCodecs += codec.cls -> (id, codec)
    clientHandlers += id -> (buf => handler(codec.decodeMsg(buf)))
  }

  private def encode(message: Message): NetPayload = {
    messageCodecs.get(message.getClass) match {
      case Some((msgId, codec)) =>
        val buf = new FriendlyByteBuf(Unpooled.buffer())
        codec.asInstanceOf[Codec[Message]].encodeMsg(message, buf)
        val data = new Array[Byte](buf.readableBytes())
        buf.readBytes(data)
        new NetPayload(key, msgId, data)
      case None =>
        sys.error(s"Message ${message.getClass.getName} is not registered on channel $key")
    }
  }

  def sendToAll(message: Message): Unit = {
    PacketDistributor.sendToAllPlayers(encode(message))
  }

  def sendTo(message: Message, player: ServerPlayer): Unit = {
    PacketDistributor.sendToPlayer(player, encode(message))
  }

  def sendToAllAround(message: Message, point: TargetPoint): Unit = {
    val server = ServerLifecycleHooks.getCurrentServer
    val level = server.getLevel(point.dim)
    if (level != null)
      PacketDistributor.sendToPlayersNear(level, null, point.x, point.y, point.z, point.range, encode(message))
  }

  def sendToDimension(message: Message, dimension: ResourceKey[Level]): Unit = {
    val level = ServerLifecycleHooks.getCurrentServer.getLevel(dimension)
    if (level != null)
      PacketDistributor.sendToPlayersInDimension(level, encode(message))
  }

  def sendToServer(message: Message): Unit = {
    PacketDistributor.sendToServer(encode(message))
  }
}

object NetChannel {
  val channels: mutable.Map[String, NetChannel] = mutable.Map.empty

  def registerAll(event: RegisterPayloadHandlersEvent): Unit = {
    event.registrar("1").playBidirectional(
      NetPayload.TYPE,
      NetPayload.CODEC,
      new IPayloadHandler[NetPayload] {
        override def handle(payload: NetPayload, ctx: IPayloadContext): Unit = {
          channels.get(payload.channel) match {
            case Some(channel) =>
              val buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data))
              try {
                if (ctx.flow() == PacketFlow.SERVERBOUND) {
                  channel.serverHandlers.get(payload.id).foreach(_ (buf, new NetContext(ctx.player())))
                } else {
                  channel.clientHandlers.get(payload.id).foreach(_ (buf))
                }
              } catch {
                case e: Throwable => channel.log.error(s"Error handling packet on channel ${channel.id}", e)
              }
            case None =>
              LogManager.getLogger.error(s"Received packet for unknown channel ${payload.channel}")
          }
        }
      }
    )
  }
}
