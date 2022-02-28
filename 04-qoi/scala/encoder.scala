import stainless.*
import stainless.lang.*
import stainless.annotation.*
import stainless.collection.*
import stainless.proof.*
import StaticChecks.*
import common.*

object encoder {

  @cCode.`export`
  case class EncodedResult(encoded: Array[Byte], length: Long)

  case class EncodeSingleStepResult(px: Int, outPos: Long, run: Long)

  case class EncodingIteration(px: Int, outPos: Long)

  /////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////

  @cCode.`export`
  def encode(pixels: Array[Byte], w: Long, h: Long, chan: Long): OptionMut[EncodedResult] = {
    if (!(0 < w && w <= MaxWidth &&
      0 < h && h <= MaxHeight &&
      3 <= chan && chan <= 4 &&
      w * h * chan == pixels.length)) {
      NoneMut()
    } else {
      val maxSize = w * h * (chan + 1) + HeaderSize + Padding
      val bytes = allocArray(maxSize.toInt)
      writeHeader(bytes, w, h, chan)
      val index = Array.fill(64)(0)
      val pxPrev = Pixel.fromRgba(0, 0, 0, 255.toByte)
      val EncodingIteration(pxRes, outPos) = encodeLoop(index, bytes, pixels, chan, pxPrev, 0, HeaderSize, 0)
      SomeMut(EncodedResult(bytes, outPos + Padding))
    }
  }

  def encodeLoop(index: Array[Int], bytes: Array[Byte], pixels: Array[Byte], chan: Long,
                 pxPrev: Int, run0: Long, outPos0: Long, pxPos: Long): EncodingIteration = {
    val EncodeSingleStepResult(px, outPos2, run1) = encodeSingleStep(index, bytes, pixels, chan, pxPrev, run0, outPos0, pxPos)

    if (pxPos + chan < pixels.length) {
      encodeLoop(index, bytes, pixels, chan, px, run1, outPos2, pxPos + chan)
    } else {
      bytes(outPos2.toInt + Padding - 1) = 1
      EncodingIteration(px, outPos2)
    }
  }


  def writeHeader(bytes: Array[Byte], w: Long, h: Long, chan: Long): Unit = {
    write32(bytes, 0, MagicNumber)
    assert(read32(bytes, 0) == MagicNumber)

    write32(bytes, 4, w.toInt)
    assert(read32(bytes, 4) == w.toInt)

    write32(bytes, 8, h.toInt)
    assert(read32(bytes, 8) == h.toInt)

    bytes(12) = chan.toByte
    assert(bytes(12) == chan.toByte)

    bytes(13) = 0 // Color-space (unused)
  }

  def encodeSingleStep(index: Array[Int], bytes: Array[Byte], pixels: Array[Byte], chan: Long,
                       pxPrev: Int, run0: Long, outPos0: Long, pxPos: Long): EncodeSingleStepResult = {
    val px =
      if (chan == 4) read32(pixels, pxPos.toInt)
      else Pixel.fromRgba(pixels(pxPos.toInt), pixels(pxPos.toInt + 1), pixels(pxPos.toInt + 2), Pixel.a(pxPrev))

    var run = run0
    var outPos = outPos0

    if (px == pxPrev)
      run += 1

    if (run > 0 && (run == 62 || px != pxPrev || pxPos == pixels.length - chan)) {
      val b1 = (OpRun | (run - 1)).toByte
      bytes(outPos.toInt) = b1
      outPos += 1
      run = 0
    }

    val outPos2 = {
      if (px != pxPrev) encodeNoRun(index, bytes, pxPrev, px, outPos)
      else outPos
    }
    EncodeSingleStepResult(px, outPos2, run)
  }

  def encodeNoRun(index: Array[Int], bytes: Array[Byte], pxPrev: Int, px: Int, outPos: Long): Long = {
    require(index.length == 64)
    require(HeaderSize <= outPos && outPos <= bytes.length - Padding)

    val indexPos = colorPos(px)
    var newOutPos = outPos

    if (index(indexPos) == px) {
      // Case B
      val b1 = (OpIndex | indexPos) & 0xff
      bytes(newOutPos.toInt) = b1.toByte
      newOutPos += 1
    } else {
      // Cases C or D

      index(indexPos) = px

      if (Pixel.a(px) == Pixel.a(pxPrev)) {
        val vr = ((Pixel.r(px).toInt & 0xff) - (Pixel.r(pxPrev).toInt & 0xff)).toByte
        val vg = ((Pixel.g(px).toInt & 0xff) - (Pixel.g(pxPrev).toInt & 0xff)).toByte
        val vb = ((Pixel.b(px).toInt & 0xff) - (Pixel.b(pxPrev).toInt & 0xff)).toByte
        val vgR = (vr - vg).toByte
        val vgB = (vb - vg).toByte

        if (vr > -3 && vr < 2 && vg > -3 && vg < 2 && vb > -3 && vb < 2) {
          // Case B
          val b1 = OpDiff | (((vr + 2) << 4) & 0xff) | (((vg + 2) << 2) & 0xff) | ((vb + 2) & 0xff)
          bytes(newOutPos.toInt) = b1.toByte
          newOutPos += 1
        } else if (vgR > -9 && vgR < 8 && vg > -33 && vg < 32 && vgB > -9 && vgB < 8) {
          // Case B
          val b1 = OpLuma | ((vg + 32) & 0xff)
          val b2 = (((vgR + 8) << 4) & 0xff) | ((vgB + 8) & 0xff)
          bytes(newOutPos.toInt) = b1.toByte
          newOutPos += 1
          bytes(newOutPos.toInt) = b2.toByte
          newOutPos += 1
        } else {
          // Case D
          bytes(newOutPos.toInt) = OpRgb.toByte
          newOutPos += 1
          bytes(newOutPos.toInt) = Pixel.r(px)
          newOutPos += 1
          bytes(newOutPos.toInt) = Pixel.g(px)
          newOutPos += 1
          bytes(newOutPos.toInt) = Pixel.b(px)
          newOutPos += 1
        }
      } else {
        // Case D
        bytes(newOutPos.toInt) = OpRgba.toByte
        newOutPos += 1
        bytes(newOutPos.toInt) = Pixel.r(px)
        newOutPos += 1
        bytes(newOutPos.toInt) = Pixel.g(px)
        newOutPos += 1
        bytes(newOutPos.toInt) = Pixel.b(px)
        newOutPos += 1
        bytes(newOutPos.toInt) = Pixel.a(px)
        newOutPos += 1
      }
    }

    newOutPos
  }.ensuring { newOutPos =>
    encodeNoRunProp(old(index), index, bytes, pxPrev, px, outPos, newOutPos) // ~100s to verify
  }

  def encodeNoRunProp(oldIndex: Array[Int], index: Array[Int], bytes: Array[Byte], pxPrev: Int, px: Int, outPos: Long, newOutPos: Long): Boolean = {
    require(oldIndex.length == 64)
    require(index.length == 64)
    require(HeaderSize <= outPos && outPos <= bytes.length - Padding)

    decoder.doDecodeNext(oldIndex, bytes, pxPrev, outPos) match {
      case (decoder.DecodedNext.IndexOrDiffOrColor(decodedPx), inPosRes) =>
        decodedPx == px &&
        inPosRes == newOutPos &&
        oldIndex.updated(colorPos(px), px) == index
      case _ => false
    }
  }
}
