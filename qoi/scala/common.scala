import stainless.annotation.{cCode, mutable}
import stainless.lang.old

object common {
  val OpIndex = 0x00
  val OpDiff = 0x40
  val OpLuma = 0x80
  val OpRun = 0xc0
  val OpRgb = 0xfe
  val OpRgba = 0xff

  val Mask2 = 0xc0

  val MagicNumber = 1903126886
  val HeaderSize = 14
  val Padding = 8

  val MaxWidth = 8192
  val MaxHeight = 8192

  object Pixel {
    def r(px: Int): Byte = ((px >>> 24) & 0xff).toByte
    def g(px: Int): Byte = ((px >>> 16) & 0xff).toByte
    def b(px: Int): Byte = ((px >>> 8) & 0xff).toByte
    def a(px: Int): Byte = (px & 0xff).toByte

    def fromRgba(r: Byte, g: Byte, b: Byte, a: Byte): Int = {
      (r << 24) | ((g << 16) & 0xffffff) | ((b << 8) & 0xffff) | (a & 0xff)
    }.ensuring(res => Pixel.r(res) == r && Pixel.g(res) == g && Pixel.b(res) == b && Pixel.a(res) == a)

    def incremented(px: Int)(dr: Byte = 0, dg: Byte = 0, db: Byte = 0, da: Byte = 0): Int =
      fromRgba(((Pixel.r(px) + dr) & 0xff).toByte, ((Pixel.g(px) + dg) & 0xff).toByte, ((Pixel.b(px) + db) & 0xff).toByte, ((Pixel.a(px) + da) & 0xff).toByte)

    // withRgba is a convenience wrapper around fromRgba that returns a copy of a pixel with selected modified channels.
    // Example:
    //   val px: Int = ...
    //   val px2 = Pixel.withRgba(px)(g = 123, b = 3) // Same as px, except with G=123 and B=3
    def withRgba(px: Int)(r: Byte = Pixel.r(px), g: Byte = Pixel.g(px), b: Byte = Pixel.b(px), a: Byte = Pixel.a(px)): Int =
      fromRgba(r, g, b, a)
  }

  @cCode.noMangling
  sealed trait OptionMut[@mutable T]
  @cCode.noMangling
  case class SomeMut[@mutable T](v: T) extends OptionMut[T]
  @cCode.noMangling
  case class NoneMut[@mutable T]() extends OptionMut[T]

  @cCode.function(
    code =
      """array_int8 __FUNCTION__(int32_t length) {
        |  int8_t* data = malloc(length);
        |  if (!data) {
        |    exit(-1);
        |  }
        |  memset(data, 0, length);
        |  return (array_int8) { .data = data, .length = length };
        |}""",
    headerIncludes = "stdlib.h",
    cIncludes = ""
  )
  def allocArray(size: Int): Array[Byte] = {
    Array.fill(size)(0: Byte)
  }.ensuring(_.length == size)

  /////////////////////////////////////////////////////////////////////////////////////////////////////

  def colorPos(px: Int): Int =
    ((Pixel.r(px) & 0xff) * 3 + (Pixel.g(px) & 0xff) * 5 + (Pixel.b(px) & 0xff) * 7 + (Pixel.a(px) & 0xff) * 11) % 64

  def write16(data: Array[Byte], i: Int, value: Short): Unit = {
    require(data.length >= 2 && i >= 0 && i < data.length - 1)
    data(i) = ((0xff00 & value) >>> 8).toByte
    data(i + 1) = (0xff & value).toByte
  }.ensuring(_ => read16(data, i) == value && old(data).length == data.length)

  def write32(data: Array[Byte], i: Int, value: Int): Unit = {
    require(data.length >= 4 && i >= 0 && i < data.length - 3)
    write16(data, i, (value >>> 16).toShort)
    write16(data, i + 2, value.toShort)
  }.ensuring(_ => read32(data, i) == value && old(data).length == data.length)

  def read16(data: Array[Byte], i: Int): Short = {
    require(data.length >= 2 && i >= 0 && i < data.length - 1)
    (((((data(i) & 0xff) << 8) & 0xffff) | (data(i + 1) & 0xff)) & 0xffff).toShort
  }

  def read32(data: Array[Byte], i: Int): Int = {
    require(data.length >= 4 && i >= 0 && i < data.length - 3)
    (read16(data, i) << 16) | (read16(data, i + 2) & 0xffff)
  }
}
