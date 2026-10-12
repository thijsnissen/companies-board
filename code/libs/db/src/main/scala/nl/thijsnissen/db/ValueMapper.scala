package nl.thijsnissen.db

import java.net.URI
import java.sql.PreparedStatement
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.UUID

opaque type ValueMapper[A] =
  (PreparedStatement, Int, A) => Unit

object ValueMapper:
  extension [A](vm: ValueMapper[A])
    def set(stmt: PreparedStatement, i: Int, a: A): Unit =
      vm(stmt, i, a)

  given ValueMapper[Boolean] =
    (stmt: PreparedStatement, i: Int, a: Boolean) =>
      stmt.setBoolean(i, a)

  given ValueMapper[Byte] =
    (stmt: PreparedStatement, i: Int, a: Byte) =>
      stmt.setByte(i, a)

  given ValueMapper[Short] =
    (stmt: PreparedStatement, i: Int, a: Short) =>
      stmt.setShort(i, a)

  given ValueMapper[Int] =
    (stmt: PreparedStatement, i: Int, a: Int) =>
      stmt.setInt(i, a)

  given ValueMapper[Long] =
    (stmt: PreparedStatement, i: Int, a: Long) =>
      stmt.setLong(i, a)

  given ValueMapper[Float] =
    (stmt: PreparedStatement, i: Int, a: Float) =>
      stmt.setFloat(i, a)

  given ValueMapper[Double] =
    (stmt: PreparedStatement, i: Int, a: Double) =>
      stmt.setDouble(i, a)

  given ValueMapper[BigDecimal] =
    (stmt: PreparedStatement, i: Int, a: BigDecimal) =>
      stmt.setBigDecimal(i, a.bigDecimal)

  given ValueMapper[String] =
    (stmt: PreparedStatement, i: Int, a: String) =>
      stmt.setString(i, a)

  given ValueMapper[URI] =
    (stmt: PreparedStatement, i: Int, a: URI) =>
      stmt.setString(i, a.toString)

  given ValueMapper[Array[Byte]] =
    (stmt: PreparedStatement, i: Int, a: Array[Byte]) =>
      stmt.setBytes(i, a)

  given ValueMapper[LocalDate] =
    (stmt: PreparedStatement, i: Int, a: LocalDate) =>
      stmt.setObject(i, a)

  given ValueMapper[LocalTime] =
    (stmt: PreparedStatement, i: Int, a: LocalTime) =>
      stmt.setObject(i, a)

  given ValueMapper[LocalDateTime] =
    (stmt: PreparedStatement, i: Int, a: LocalDateTime) =>
      stmt.setObject(i, a.atOffset(ZoneOffset.UTC))

  given ValueMapper[Instant] =
    (stmt: PreparedStatement, i: Int, a: Instant) =>
      stmt.setObject(i, a.atOffset(ZoneOffset.UTC))

  given ValueMapper[UUID] =
    (stmt: PreparedStatement, i: Int, a: UUID) =>
      stmt.setObject(i, a)

  given [A](using vm: ValueMapper[A]): ValueMapper[Option[A]] =
    (stmt: PreparedStatement, i: Int, a: Option[A]) =>
      a match
        case Some(v) => vm(stmt, i, v)
        case None    => stmt.setNull(i, java.sql.Types.NULL)

  given [A]: ValueMapper[List[A]] =
    (stmt: PreparedStatement, i: Int, a: List[A]) =>
      stmt.setArray(i, stmt.getConnection.createArrayOf("text", a.map(_.toString).toArray))
