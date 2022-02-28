import stainless.*
import stainless.lang.*
import stainless.annotation.cCode
import stainless.collection.*
import common.*

object decoder {

  @cCode.`export`
  case class DecodedResult(pixels: Array[Byte], w: Long, h: Long, chan: Long)

  case class WriteRunPixelsResult(remainingRun: Long, pxPos: Long)

  enum DecodedNext {
    case Run(run: Long)
    case IndexOrDiffOrColor(px: Int)
  }
  import DecodedNext._

  case class DecodingIteration(px: Int, inPos: Long, pxPos: Long, remainingRun: Long)

  /////////////////////////////////////////////////////////////////////////////////////////////////////

  @cCode.`export`
  def decode(bytes: Array[Byte], to: Long): OptionMut[DecodedResult] = {
    if (!(bytes.length > HeaderSize + Padding && HeaderSize + Padding < to && to <= bytes.length)) {
      NoneMut()
    } else {
      decodeHeader(bytes) match {
        case Some((w, h, chan)) =>
          val index = Array.fill(64)(0)
          val pixels = allocArray(w.toInt * h.toInt * chan.toInt)
          val px = Pixel.fromRgba(0, 0, 0, 255.toByte)
          val decIter = decodeLoop(index, bytes, pixels, chan, px, HeaderSize, to - Padding, 0)
          if (decIter.pxPos != pixels.length) {
            writeRemainingPixels(pixels, chan, decIter.px, decIter.pxPos)
          }
          SomeMut(DecodedResult(pixels, w, h, chan))
        case None() => NoneMut()
      }
    }
  }

  def writeRemainingPixels(pixels: Array[Byte], chan: Long, pxPrev: Int, pxPos: Long): Unit = {
    writePixel(pixels, chan, pxPrev, pxPos)
    if (pxPos + chan < pixels.length) {
      writeRemainingPixels(pixels, chan, pxPrev, pxPos + chan)
    }
  }

  def decodeLoop(index: Array[Int], bytes: Array[Byte], pixels: Array[Byte], chan: Long,
                 pxPrev: Int, inPos0: Long, untilInPos: Long, pxPos0: Long): DecodingIteration = {
    val (res, decIter) = decodeNext(index, bytes, pixels, chan, pxPrev, inPos0, pxPos0)

    if (decIter.inPos < untilInPos && decIter.pxPos + chan <= pixels.length) {
      decodeLoop(index, bytes, pixels, chan, decIter.px, decIter.inPos, untilInPos, decIter.pxPos)
    } else {
      decIter
    }
  }

  def decodeNext(index: Array[Int], bytes: Array[Byte], pixels: Array[Byte], chan: Long, pxPrev: Int, inPos0: Long, pxPos0: Long): (DecodedNext, DecodingIteration) = {
    val (decRes, inPos) = doDecodeNext(index, bytes, pxPrev, inPos0)

    decRes match {
      case Run(run) =>
        val WriteRunPixelsResult(resRun, resPxPos) = writeRunPixels(pixels, chan, pxPrev, run, pxPos0)
        (decRes, DecodingIteration(pxPrev, inPos, resPxPos, resRun))

      case IndexOrDiffOrColor(px) =>
        writePixel(pixels, chan, px, pxPos0)
        index(colorPos(px)) = px
        (decRes, DecodingIteration(px, inPos, pxPos0 + chan, 0))
    }
  }

  def doDecodeNext(index: Array[Int], bytes: Array[Byte], pxPrev: Int, inPos: Long): (DecodedNext, Long) = {
    require(index.length == 64)
    require(HeaderSize <= inPos && inPos <= bytes.length - Padding)
    var px = pxPrev
    var newInPos = inPos
    var run = 0L

    val b1 = bytes(inPos.toInt).toInt & 0xff
    newInPos += 1
    val res: DecodedNext = if (b1 == OpRgb) {
      val px = Pixel.withRgba(pxPrev)(r = bytes(newInPos.toInt), g = bytes(newInPos.toInt + 1), b = bytes(newInPos.toInt + 2))
      newInPos += 3
      IndexOrDiffOrColor(px)
    } else if (b1 == OpRgba) {
      val px = Pixel.withRgba(pxPrev)(r = bytes(newInPos.toInt), g = bytes(newInPos.toInt + 1), b = bytes(newInPos.toInt + 2), a = bytes(newInPos.toInt + 3))
      newInPos += 4
      IndexOrDiffOrColor(px)
    } else if ((b1 & Mask2) == OpIndex) {
      val px = index(b1)
      IndexOrDiffOrColor(px)
    } else if ((b1 & Mask2) == OpDiff) {
      val px = decodeDiff(pxPrev, b1)
      IndexOrDiffOrColor(px)
    } else if ((b1 & Mask2) == OpLuma) {
      val b2 = bytes(newInPos.toInt).toInt & 0xff
      newInPos += 1
      val px = decodeLuma(pxPrev, b1, b2)
      IndexOrDiffOrColor(px)
    } else if ((b1 & Mask2) == OpRun) {
      val run = decodeRun(b1)
      Run(run)
    } else {
      IndexOrDiffOrColor(pxPrev)
    }
    (res, newInPos)
  }

  def decodeHeader(bytes: Array[Byte]): Option[(Long, Long, Long)] = {
    val magic = read32(bytes, 0)
    val w = read32(bytes, 4)
    val h = read32(bytes, 8)
    val chan = bytes(12)

    if (0 < w && w <= MaxWidth && 0 < h && h <= MaxHeight && magic == MagicNumber && 3 <= chan && chan <= 4)
      Some((w.toLong, h.toLong, chan.toLong))
    else
      None()
  }

  def writeRunPixels(pixels: Array[Byte], chan: Long, px: Int, run0: Long, pxPos0: Long): WriteRunPixelsResult = {
    writePixel(pixels, chan, px, pxPos0)
    if (run0 > 0 && pxPos0 + chan < pixels.length) {
      writeRunPixels(pixels, chan, px, run0 - 1, pxPos0 + chan)
    } else {
      WriteRunPixelsResult(run0, pxPos0 + chan)
    }
  }

  def writePixel(pixels: Array[Byte], chan: Long, px: Int, pxPos: Long): Unit = {
    pixels(pxPos.toInt) = Pixel.r(px)
    pixels(pxPos.toInt + 1) = Pixel.g(px)
    pixels(pxPos.toInt + 2) = Pixel.b(px)
    if (chan == 4) {
      pixels(pxPos.toInt + 3) = Pixel.a(px)
    }
  }

  def decodeDiff(pxPrev: Int, b1: Int): Int = {
    Pixel.incremented(pxPrev)(
      (((b1 >>> 4) & 0x03) - 2).toByte,
      (((b1 >>> 2) & 0x03) - 2).toByte,
      ((b1 & 0x03) - 2).toByte)
  }

  def decodeLuma(pxPrev: Int, b1: Int, b2: Int): Int = {
    val vg = (b1 & 0x3f) - 32
    Pixel.incremented(pxPrev)(
      (vg - 8 + ((b2 >>> 4) & 0x0f)).toByte,
      vg.toByte,
      (vg - 8 + (b2 & 0x0f)).toByte)
  }

  def decodeRun(b1: Int): Int = b1 & 0x3f
}
