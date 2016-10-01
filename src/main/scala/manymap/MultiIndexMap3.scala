package manymap

import scala.collection.{mutable, IterableLike}

trait MultiIndexMap3[A, B1, B2, B3] extends MultiIndexMap3Like[A, B1, B2, B3, MultiIndexMap3[A, B1, B2, B3]] {

  def empty(f1: A => B1, f2: A => B2, f3: A => B3) = MultiIndexMap.empty(f1, f2, f3)

  def ==(that: MultiIndexMap3[A, B1, B2, B3]) = multiSet == that.multiSet && f1 == that.f1 && f2 == that.f2 && f3 == that.f3
}

trait MultiIndexMap3Like[A, B1, B2, B3, +This <: MultiIndexMap3Like[A, B1, B2, B3, This] with MultiIndexMap3[A, B1, B2, B3]] extends IterableLike[A, This] with MultiIndexMap[A] {

  def empty(f1: A => B1, f2: A => B2, f3: A => B3): This

  /** First function to index elements on */
  def f1: A => B1

  /** Second function to index elements on */
  def f2: A => B2

  /** Third function to index elements on */
  def f3: A => B3

  /** Get a bag of all elements that match on both indexes with b1 and b2 and b3*/
  def get(b1: B1, b2: B2, b3: B3): List[A]

  /** Get a list of all elements that match b1 on index 1 */
  def get1(b1: B1): List[A]

  /** Get a bag of all elements that match b1 on index 1 */
  def get1MultiSet(b1: B1): MultiSet[A]

  /** Get a list of all elements that match b2 on index 2 */
  def get2(b2: B2): List[A]

  /** Get a bag of all elements that match b2 on index 2 */
  def get2MultiSet(b2: B2): MultiSet[A]

  /** Get a list of all elements that match b3 on index 3 */
  def get3(b3: B3): List[A]

  /** Get a bag of all elements that match b3 on index 3 */
  def get3MultiSet(b3: B3): MultiSet[A]

  /** Append an element to these elements, add it to the indexes */
  def + (a: A): MultiIndexMap3[A, B1, B2, B3]

  /** Remove one instance of a from these elements */
  def - (a: A): MultiIndexMap3[A, B1, B2, B3]

  /** Append elements to these elements, add them to the indexes */
  def ++ (as: Iterable[A]): MultiIndexMap3[A, B1, B2, B3]

  /** Remove one instance of each element from these elements and indexes */
  def -- (as: Iterable[A]): MultiIndexMap3[A, B1, B2, B3]

  def withIndex[B4](f4: A => B4): MultiIndexMap4[A, B1, B2, B3, B4]

  override protected[this] def newBuilder: mutable.Builder[A, This] = new MultiIndexMap3Builder[A, B1, B2, B3, This](empty(f1, f2, f3))
}
class MultiIndexMap3Builder[A, B1, B2, B3, Coll <: MultiIndexMap3[A, B1, B2, B3] with MultiIndexMap3Like[A, B1, B2, B3, Coll]](empty: Coll) extends mutable.Builder[A, Coll] {
  protected var elems: Coll = empty
  def +=(x: A): this.type = {
    elems = (elems + x).asInstanceOf[Coll]
    this
  }
  def clear() { elems = empty }
  val result: Coll = elems
}

class MultiIndexMap3Impl[A, B1, B2, B3] private[manymap] (
  val multiSet: MultiSet[A],
  val f1: A => B1,
  val index1: JIndex[A, B1],
  val f2: A => B2,
  val index2: JIndex[A, B2],
  val f3: A => B3,
  val index3: JIndex[A, B3]) extends MultiIndexMap3[A, B1, B2, B3] {

  def get(b1: B1, b2: B2, b3: B3) = get1(b1).intersect(get2(b2)).intersect(get3(b3))

  def get1(b1: B1) = index1.getList(b1)

  def get1MultiSet(b1: B1) = index1(b1)

  def get2(b2: B2) = index2.getList(b2)

  def get2MultiSet(b2: B2) = index2(b2)

  def get3(b3: B3) = index3.getList(b3)

  def get3MultiSet(b3: B3) = index3(b3)

  def + (a: A) = new MultiIndexMap3Impl(multiSet + a, f1, index1 + a, f2, index2 + a, f3, index3 + a)

  def - (a: A) = new MultiIndexMap3Impl(multiSet - a, f1, index1 - a, f2, index2 - a, f3, index3 - a)

  def ++ (as: Iterable[A]) = new MultiIndexMap3Impl(multiSet ++ as, f1, index1 ++ as, f2, index2 ++ as, f3, index3 ++ as)

  /** Remove one instance of each element from these elements and indexes */
  def -- (as: Iterable[A]) = new MultiIndexMap3Impl(multiSet -- as, f1, index1 -- as, f2, index2 -- as, f3, index3 -- as)

  override def filter(p: A => Boolean) = new MultiIndexMap3Impl(multiSet.filter(p), f1, index1.filter(p), f2, index2.filter(p), f3, index3.filter(p))

  def withIndex[B4](f4: A => B4) = ??? //new MultiIndexMap3Impl(multiSet, f1, index1, f2, index2, f3, JIndex(f3, multiSet.toList))
}