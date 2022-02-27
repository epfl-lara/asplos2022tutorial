object Pixel {   
    def r(px: Int): Byte = ((px >>> 24) & 0xff).toByte
    
    def g(px: Int): Byte = ((px >>> 16) & 0xff).toByte
    
    def b(px: Int): Byte = ((px >>> 8) & 0xff).toByte
    
    def a(px: Int): Byte = (px & 0xff).toByte

    def fromRgba0(r: Byte, g: Byte, b: Byte, a: Byte): Int = {
      // Incorrect version, albeit seemingly correct
      (r << 24) | (g << 16) | (b << 8) | a
    }.ensuring(res => Pixel.r(res) == r && Pixel.g(res) == g && Pixel.b(res) == b && Pixel.a(res) == a)

    // Above is wrong because the sub-expressions (g << 16), etc. get promoted to Int and are sign-extended.
    
    def fromRgba(r: Byte, g: Byte, b: Byte, a: Byte): Int = {
      (r << 24) | ((g << 16) & 0xffffff) | ((b << 8) & 0xffff) | (a & 0xff)
    }.ensuring(res => Pixel.r(res) == r && Pixel.g(res) == g && Pixel.b(res) == b && Pixel.a(res) == a) 
}

