import stainless._
import stainless.lang._

object Nontermination {

  def answer(x: Int): Int = {
    answer(x)
  } ensuring(res => res == x && x == 42)

  def test = {
    val x = answer(5)
    assert(5 == 42)
  }
}

