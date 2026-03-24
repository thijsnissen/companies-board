package nl.thijsnissen.db

import java.sql.PreparedStatement

final case class Sql(query: String, values: List[Sql.Value[?]]):
  lazy val isEmpty: Boolean =
    query.isEmpty && values.isEmpty

  def stripMargin: Sql =
    this.copy(query.stripMargin.trim)

  @targetName("concat")
  inline def ++(that: Sql): Sql =
    Sql(
      query = s"$query${if isEmpty || that.isEmpty then "" else " "}${that.query}",
      values = values ::: that.values
    )

object Sql:
  def apply(query: String, values: List[Sql.Value[?]]): Sql =
    new Sql(query, values).stripMargin

  lazy val empty: Sql = Sql("", List.empty[Sql.Value[?]])

  case class Value[A: ValueMapper](a: A):
    def set(stmt: PreparedStatement, i: Int): Unit =
      summon[ValueMapper[A]].set(stmt, i, a)

  sealed trait Argument:
    def asValues: List[Sql.Value[?]]
    def asParameter: String

  object Argument:
    final case class Raw[A](a: A) extends Argument:
      override val asValues: List[Sql.Value[A]] = Nil
      override val asParameter: String          = a.toString

    final case class Param[A: ValueMapper](a: A) extends Argument:
      override val asValues: List[Sql.Value[A]] = List(Sql.Value(a))
      override val asParameter: String          = "?"

    final case class Bind[A: ValueMapper](as: List[A]) extends Argument:
      override val asValues: List[Sql.Value[A]] = as.map(Sql.Value(_))
      override val asParameter: String          = List.fill(as.length)("?").mkString(", ")

    extension [A](a: A) def r: Raw[A]                      = Raw(a)
    extension [A: ValueMapper](a: A) def p: Param[A]       = Param(a)
    extension [A: ValueMapper](as: List[A]) def b: Bind[A] = Bind(as)

  extension (sc: StringContext)
    def sql(args: Argument*): Sql =
      val query: String =
        sc.parts.head + args.map(_.asParameter).zip(sc.parts.tail).map(_ + _).mkString

      val values: List[Sql.Value[?]] =
        args.flatMap(_.asValues).toList

      Sql(query, values)
