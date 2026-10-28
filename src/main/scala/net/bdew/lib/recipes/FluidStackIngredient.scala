package net.bdew.lib.recipes

import com.google.gson.{JsonObject, JsonSyntaxException}
import net.bdew.lib.{JSResLoc, JSSinglePair}
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.FluidTags
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack
import net.minecraft.core.registries.BuiltInRegistries

case class FluidStackIngredient(fluid: GenIngredient[Fluid], amount: Int) {
  def fluids: Set[Fluid] = fluid.resolve

  def isEmpty: Boolean = amount <= 0 || fluid.isEmpty

  def test(f: Fluid): Boolean = fluid.test(f)
  def test(fs: FluidStack): Boolean = !fs.isEmpty && test(fs.getFluid) && fs.getAmount >= amount
  def testSoft(stack: FluidStack): Boolean = test(stack.getFluid)

  def toStack: FluidStack =
    fluids.headOption.map(new FluidStack(_, amount)).getOrElse(FluidStack.EMPTY)

  def toPacket(pkt: FriendlyByteBuf): Unit = {
    fluid.toPacket(pkt)
    pkt.writeInt(amount)
  }
}

object FluidStackIngredient {
  val EMPTY: FluidStackIngredient = FluidStackIngredient(GenIngredient.empty[Fluid], 0)

  def singleFluid(js: JsonObject): Set[GenIngredient[Fluid]] = {
    js match {
      case JSSinglePair("fluid", JSResLoc(key)) =>
        if (!BuiltInRegistries.FLUID.containsKey(key))
          throw new JsonSyntaxException(s"Fluid not found - $key")
        Set(GenIngredient.of(BuiltInRegistries.FLUID.get(key)))
      case JSSinglePair("fluidTag", JSResLoc(key)) =>
        Set(GenIngredient.of(FluidTags.create(key)))
      case _ => throw new JsonSyntaxException(s"Invalid fluid ingredient: ${js.toString}")
    }
  }

  def fromJson(js: JsonObject): FluidStackIngredient = {
    val fluid = if (js.has("fluid")) {
      val key = ResourceLocation.parse(js.get("fluid").getAsString)
      if (!BuiltInRegistries.FLUID.containsKey(key))
        throw new JsonSyntaxException(s"Fluid not found - $key")
      GenIngredient.of(BuiltInRegistries.FLUID.get(key))
    } else if (js.has("fluidTag")) {
      val key = ResourceLocation.parse(js.get("fluidTag").getAsString)
      GenIngredient.of(FluidTags.create(key))
    } else throw new JsonSyntaxException(s"Invalid fluid ingredient: ${js.toString}")
    val amount = if (js.has("amount"))
      js.get("amount").getAsInt
    else 1000
    FluidStackIngredient(fluid, amount)
  }

  def fromPacket(pkt: FriendlyByteBuf): FluidStackIngredient = {
    val fluid = GenIngredient.fromPacket[Fluid](pkt)
    val amount = pkt.readInt()
    FluidStackIngredient(fluid, amount)
  }
}