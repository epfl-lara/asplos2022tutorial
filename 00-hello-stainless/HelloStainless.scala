import stainless._
import stainless.collection._

object HelloStainless {
  def nonEmptyListSize(l: List[String]): BigInt = {
    require(l.nonEmpty)
    l.size
  }.ensuring(res => res > 0)
}
