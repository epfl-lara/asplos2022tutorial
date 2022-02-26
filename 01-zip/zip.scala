import stainless.*
import stainless.lang.*

// Immutable linked lists.
enum List[T] {
  case Nil()
  case Cons(head: T, tail: List[T])

  def map[U](f: T => U): List[U] =
    this match {
      case Nil()            => Nil()
      case Cons(head, tail) => Cons(f(head), tail.map(f))
    }

  def size: BigInt = {
    this match {
      case Nil()         => BigInt(0)
      case Cons(_, tail) => BigInt(1) + tail.size
    }
  } ensuring (_ >= 0)

  // NOTE: Adding the postcondition to size is crucial since it allows
  //   us to conclude that *both* xs and ys are Nil when we know that
  //   xs.size == ys.size and *either* xs *or* ys is Nil. (This is
  //   what we need to complete the proof of zip below.)
}

import List._


// A particular list consisting of elements 1, 2 and 3.
val xs: List[Int] = Cons(1, Cons(2, Cons(3, Nil())))


// The usual zip function with specifications.
def zip(xs: List[Int], ys: List[Boolean]): List[(Int, Boolean)] = {
  require(xs.size == ys.size)
  (xs, ys) match {
    case (Cons(x, xs0), Cons(y, ys0)) =>
      Cons((x, y), zip(xs0, ys0))
    case _ =>
      Nil[(Int, Boolean)]()
  }
} ensuring ( res =>
  res.map(_._1) == xs  &&  res.map(_._2) == ys
)



// case class Stack[T](private var data: List[T])
// {
//   def list: List[T] = { data }

//   def push(a: T): Unit = {
//     data = a :: data
//   } ensuring(_ => list == a :: old(list))

//   def pop: T = {
//     require(!list.isEmpty)
//     val a = data.head
//     data = data.tail
//     a
//   } ensuring (res =>
//     res == old(list).head &&
//     list == old(list).tail)
// }
