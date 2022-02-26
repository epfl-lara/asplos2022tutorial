import stainless.*
import stainless.collection.List
import stainless.lang.*

case class Stack[T](private var data: List[T])
{
  def list: List[T] = { data }

  def push(a: T): Unit = {
    data = a :: data
  } ensuring(_ => list == a :: old(this).list)

  def pop: T = {
    require(!list.isEmpty)
    val a = data.head
    data = data.tail
    a
  } ensuring (res =>
    res == old(this).list.head &&
    list == old(this).list.tail)
}
