package net.bdew.lib.render

import net.bdew.lib.gui.Texture
import net.bdew.lib.{Client, Misc}
import net.minecraft.resources.ResourceLocation

/**
 * Allows loading of textures that don't belong to a specific block or item.
 *
 * In 1.21.1 the old TextureStitchEvent.Pre hook no longer exists, so these icons
 * are loaded as standalone textures instead of block-atlas sprites.
 */
class IconPreloader {
  lazy val map: Map[String, TextureLoc] = {
    (for {
      method <- getClass.getMethods if method.getParameterTypes.isEmpty && classOf[TextureLoc].isAssignableFrom(method.getReturnType)
      value <- Misc.asInstanceOpt(method.invoke(this), classOf[TextureLoc])
    } yield method.getName -> value).toMap
  }

  var icons = Set.empty[TextureLoc]

  case class TextureLoc(loc: String) {
    val resource: ResourceLocation = ResourceLocation.parse(loc)
    lazy val texture: Texture = Texture(resource)
    icons += this
  }

  import scala.language.implicitConversions

  implicit def entry2texture(v: TextureLoc): Texture = v.texture
}
