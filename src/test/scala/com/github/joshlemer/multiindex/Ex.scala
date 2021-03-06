package com.github.joshlemer.multiindex

object Ex extends App {

  case class User(id: Int, name: String, sex: Char)

  val users = List(
    User(1, "Josh", 'M'),
    User(2, "Joanne", 'F'),
    User(3, "Shivani", 'F'),
    User(4, "Steve", 'M'),
    User(5, "Josh", 'M'),
    User(6, "Hillary", 'F'),
    User(7, "Robert", 'M'),
    User(8, "Josh", 'M'),
    User(9, "Aboozipadoo from planet pluto", '?')
  )

  val indexByNameAndSex = users.indexBy(_.name, _.sex) // MultiIndexMap2[User, String, Char]

  val joshs = indexByNameAndSex.get1("Josh") // List[User] -- contains all users with name "Josh"

  val females = indexByNameAndSex.get2('F') // List[User] -- contains all users with sex 'F'


  val femaleJoshs = indexByNameAndSex.get("Josh", 'F') // List[User] -- contains all users with name "Josh" and sex 'F'


  val newUser = User(100, "New User", 'M')

  val added = indexByNameAndSex + newUser

  assert(added.get1("New User") == List(newUser))

  // can store non-unique items, can add Iterable[User]
  assert((added ++ List(newUser, newUser)).get1("New User") == List(newUser, newUser, newUser))

  // can remove a single instance of a non-unique element
  assert((added + newUser - newUser).get1("New User") == List(newUser))


  // can add more indexes to the map, taking it to a higher-number-of-indexes map

  val withIdIndex: MultiIndex3[User, String, Char, Int] = indexByNameAndSex.withIndex(_.id)
  val withNameLengthIndex = withIdIndex.withIndex(_.name.length)

  withNameLengthIndex.get4(6) // list of users with name of length 6

}

object Foo extends App {
  def time[R](block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block    // call-by-name
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) + "ms")
    result
  }
  import com.github.joshlemer.multiset._

  val bigList = time((1 to 100000).flatMap(i => Vector.fill(1)(i)))
  val bigMultiSet = time(bigList.toMultiSet)

  println(time(bigList.count(_ == 50)))
  println(time(bigMultiSet.count(_ == 50)))
  val a = time(bigMultiSet intersect bigMultiSet)
  time(bigMultiSet intersect (1 to 4).toMultiSet)
  time(bigMultiSet union bigMultiSet)

  println(MultiSet(1, 1, 1, 2, 3, 4, 4) union MultiSet(1, 1, 2, 2, 3, 3, 3, 3, 3))

}


