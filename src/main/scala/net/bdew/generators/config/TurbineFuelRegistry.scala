package net.bdew.generators.config

import net.bdew.generators.recipes.LiquidFuelRecipe
import net.bdew.generators.registries.Recipes
import net.bdew.lib.recipes.RecipeReloadListener
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.{BuiltInRegistries, Registries}
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.{FluidTags, TagKey}
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack
import org.apache.logging.log4j.LogManager

import scala.jdk.CollectionConverters._
import scala.util.Try

object TurbineFuelRegistry {
  private val log = LogManager.getLogger

  var recipes = Set.empty[LiquidFuelRecipe]

  private case class ConfiguredFuel(fluid: Option[Fluid], tag: Option[TagKey[Fluid]], value: Float) {
    def matches(f: Fluid): Boolean =
      fluid.contains(f) || tag.exists(t => f.builtInRegistryHolder().is(t))
  }

  private var configuredFuels = List.empty[ConfiguredFuel]
  private var multiplier = 1.0F
  private var configLoaded = false

  def refreshRecipes(manager: RecipeManager, ra: RegistryAccess): Unit = {
    recipes = Recipes.liquidFuel.from(manager).toSet
    // Fuels can depend on tags/fluids from other mods, so refresh after recipes too.
    refreshConfig()
  }

  def init(): Unit = {
    RecipeReloadListener.onServerRecipeUpdate.listen(refreshRecipes)
    RecipeReloadListener.onClientRecipeUpdate.listen(refreshRecipes)
  }

  /** Re-read the [Fuels] config section. Safe to call from config loading/reloading events. */
  def refreshConfig(): Unit = {
    try {
      multiplier = Config.Fuels.multiplier()
      configuredFuels = Config.Fuels.values().asScala.toList.flatMap(parseEntry)
      configLoaded = true
    } catch {
      case err: IllegalStateException =>
        // Config not loaded yet; try again on first use / config event.
        log.debug("Fuel config not available yet: {}", err.getMessage)
      case err: Throwable =>
        log.warn("Failed to read fuel config", err)
    }
  }

  private def ensureConfig(): Unit =
    if (!configLoaded) refreshConfig()

  private def parseEntry(entry: String): Option[ConfiguredFuel] = {
    val eq = entry.lastIndexOf('=')
    if (eq <= 0 || eq == entry.length - 1) {
      log.warn("Invalid fuel config entry '{}', expected fluid_or_tag=fePerMb", entry)
      return None
    }

    val key = entry.substring(0, eq).trim
    val value = Try(entry.substring(eq + 1).trim.toFloat).toOption
      .filter(_ >= 0)
      .getOrElse {
        log.warn("Invalid fuel value in config entry '{}'", entry)
        return None
      }

    if (key.startsWith("#")) {
      ResourceLocation.tryParse(key.substring(1)) match {
        case null =>
          log.warn("Invalid fluid tag id in fuel config entry '{}'", entry)
          None
        case id =>
          Some(ConfiguredFuel(None, Some(FluidTags.create(id)), value))
      }
    } else {
      ResourceLocation.tryParse(key) match {
        case null =>
          log.warn("Invalid fluid id in fuel config entry '{}'", entry)
          None
        case id =>
          Option(BuiltInRegistries.FLUID.get(id)) match {
            case Some(fluid) if fluid != net.minecraft.world.level.material.Fluids.EMPTY =>
              Some(ConfiguredFuel(Some(fluid), None, value))
            case _ =>
              log.warn("Unknown fluid '{}' in fuel config entry '{}'", id, entry)
              None
          }
      }
    }
  }

  def isValidFuel(fs: FluidStack): Boolean = isValidFuel(fs.getFluid)

  def isValidFuel(f: Fluid): Boolean = {
    ensureConfig()
    configuredFuels.exists(_.matches(f)) || recipes.exists(_.input.matches(f))
  }

  def getFuelValue(f: Fluid): Float = {
    ensureConfig()
    val base = configuredFuels.find(_.matches(f)).map(_.value)
      .orElse(recipes.find(_.input.matches(f)).map(_.fePerMb))
      .getOrElse(0.0F)
    base * multiplier
  }

  /** All currently known fuel values, for commands/debugging. */
  def knownValues: Map[String, Float] = {
    ensureConfig()
    val recipeValues = recipes.flatMap(r => r.input.fluids.map(f => BuiltInRegistries.FLUID.getKey(f).toString -> r.fePerMb)).toMap
    val configValues = configuredFuels.map { cfg =>
      val key = cfg.fluid.map(f => BuiltInRegistries.FLUID.getKey(f).toString)
        .orElse(cfg.tag.map(t => "#" + t.location()))
        .getOrElse("<invalid>")
      key -> cfg.value
    }.toMap
    (recipeValues ++ configValues).view.mapValues(_ * multiplier).toMap
  }
}
