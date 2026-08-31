package nl.thijsnissen.db

import nl.thijsnissen.db.Sql.Argument.*
import nl.thijsnissen.db.Sql.sql
import scala.util.Random
import zio.*
import zio.test.*

object SqlSpec extends ZIOSpecDefault:
  import SqlSpecTestData.*

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Sql")(
      test("isEmpty"):
        assertTrue(
          Sql("", List.empty) == Sql.empty,
          Sql.empty.isEmpty,
          !Sql(randomString(), List(Sql.Value(randomString()))).isEmpty
        )
      ,
      test("stripMargin"):
        val id = randomString()

        assertTrue(
          sql"""
               | select *
               |from tablename
               |where id = ${id.p}
          """.stripMargin ==
            Sql("select *\nfrom tablename\nwhere id = ?", List(Sql.Value(id)))
        )
      ,
      test("concat"):
        val id   = randomString()
        val size = randomInt()

        val select = sql"select * from tablename"
        val where  = sql"where id = ${id.p}"
        val limit  = sql"limit ${size.p}"
        val result = Sql("select * from tablename where id = ? limit ?", List(Sql.Value(id), Sql.Value(size)))

        assertTrue(
          select ++ where ++ limit == result,
          Sql.empty ++ select == select,
          where ++ Sql.empty == where,
          Sql.empty ++ Sql.empty == Sql.empty
        )
      ,
      test("interpolation"):
        val id   = randomString()
        val size = randomInt()

        assertTrue(
          sql"select * from tablename where id = ${id.p} limit ${size.p}" ==
            Sql("select * from tablename where id = ? limit ?", List(Sql.Value(id), Sql.Value(size)))
        )
      ,
      test("raw"):
        val id        = randomString()
        val size      = randomInt()
        val tablename = "tablename"

        assertTrue(
          sql"select * from ${tablename.r} where id = ${id.p} limit ${size.p}" ==
            Sql("select * from tablename where id = ? limit ?", List(Sql.Value(id), Sql.Value(size)))
        )
      ,
      test("bind"):
        val ids = List.fill(3)(randomString())

        assertTrue(
          sql"select * from tablename where id in (${ids.b})" ==
            Sql("select * from tablename where id in (?, ?, ?)", ids.map(Sql.Value(_)))
        )
    )

object SqlSpecTestData:
  def randomString(length: Int = 10): String                           = Random.alphanumeric.take(length).mkString
  def randomInt(min: Int = Int.MinValue, max: Int = Int.MaxValue): Int = Random.between(min, max)
