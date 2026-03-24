package nl.thijsnissen.db

import java.sql.ResultSet

opaque type ResultSetMapper[A] =
  ResultSet => A

object ResultSetMapper:
  extension [A](m: ResultSetMapper[A])
    def apply(rs: ResultSet): A = m(rs)

  extension (self: ResultSet)
    def extractOne[A](using f: ResultSetMapper[A]): Option[A] =
      Option.when(self.next())(f(self))

    def extractAll[A](using f: ResultSetMapper[A]): List[A] =
      @tailrec def loop(acc: List[A]): List[A] =
        if self.next() then loop(f(self) :: acc) else acc

      loop(List.empty[A]).reverse
