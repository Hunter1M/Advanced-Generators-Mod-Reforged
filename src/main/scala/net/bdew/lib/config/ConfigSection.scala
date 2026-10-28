package net.bdew.lib.config

import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue

trait ConfigSection {
  def getter[T](cv: ConfigValue[T]): () => T = () => cv.get()
  def getter[T, R](cv: ConfigValue[T], transform: T => R): () => R = () => transform(cv.get())

  def commentIfGiven(spec: ModConfigSpec.Builder, comment: String): ModConfigSpec.Builder =
    if (comment == null)
      spec
    else
      spec.comment(comment)

  def intVal(spec: ModConfigSpec.Builder, name: String, comment: String, default: Int, minVal: Int = Int.MinValue, maxVal: Int = Int.MaxValue): () => Int =
    getter(commentIfGiven(spec, comment).defineInRange(name, default, minVal, maxVal), Int.unbox)

  def doubleVal(spec: ModConfigSpec.Builder, name: String, comment: String, default: Double, minVal: Double = Double.MinValue, maxVal: Double = Double.MaxValue): () => Double =
    getter(commentIfGiven(spec, comment).defineInRange(name, default, minVal, maxVal), Double.unbox)

  def floatVal(spec: ModConfigSpec.Builder, name: String, comment: String, default: Float, minVal: Float = Float.MinValue, maxVal: Float = Float.MaxValue): () => Float =
    getter[java.lang.Double, Float](commentIfGiven(spec, comment).defineInRange(name, default.toDouble, minVal.toDouble, maxVal.toDouble), x => Double.unbox(x).toFloat)

  def boolVal(spec: ModConfigSpec.Builder, name: String, comment: String, default: Boolean): () => Boolean =
    getter(commentIfGiven(spec, comment).define(name, default), Boolean.unbox)

  def section[T](spec: ModConfigSpec.Builder, name: String, comment: String, body: => T): T =
    ConfigSection(spec, name, comment, body)
}

object ConfigSection {
  def apply[T](spec: ModConfigSpec.Builder, name: String, comment: String, body: => T): T = {
    apply(spec.comment(comment), name, body)
  }

  def apply[T](spec: ModConfigSpec.Builder, name: String, body: => T): T = {
    spec.push(name)
    val result = body
    spec.pop()
    result
  }
}
