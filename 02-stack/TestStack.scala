import stainless.*
import stainless.collection.List
import stainless.lang.*

object TestStack {
  def test(s: Stack[Int], a: Int, b: Int) = {
    s.push(a)
    s.push(b)
    s.pop
  } ensuring(res => res == a)
}
