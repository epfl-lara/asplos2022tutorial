import stainless._
import stainless.collection._

object HelloStainless {
  def nonEmptyListSize(l: List[String]): BigInt = {
    require(l.nonEmpty)
    l.size
  }.ensuring(size => size > 0)
}