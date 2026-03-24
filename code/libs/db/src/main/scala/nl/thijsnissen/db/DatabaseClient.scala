package nl.thijsnissen.db

import java.sql.*
import zio.*

class DatabaseClient(pool: ZPool[Throwable, Connection], config: DatabaseClientConfig):
  def executeQuery[A](sql: Sql)(f: ResultSetMapper[A]): Task[A] =
    DatabaseClient
      .executeQuery(sql)(f)
      .provide(ZLayer.scoped(pool.get))

  def executeUpdate(sql: Sql): Task[Int] =
    DatabaseClient
      .executeUpdate(sql)
      .provide(ZLayer.scoped(pool.get))

  def transaction(transactionIsolationLevel: TransactionIsolationLevel =
    TransactionIsolationLevel.transactionSerializable): TaskLayer[Connection] =
    val acquire =
      pool.get.map: c =>
        c.setAutoCommit(false)
        c.setTransactionIsolation(transactionIsolationLevel)

        c

    val release = (c: Connection, e: Exit[Any, Any]) =>
      ZIO.succeed:
        e match
          case Exit.Success(_) => c.commit()
          case Exit.Failure(_) => c.rollback()

        c.setAutoCommit(true)
        c.setTransactionIsolation(config.til)

    ZLayer.scoped(ZIO.acquireReleaseExit(acquire)(release))

object DatabaseClient:
  lazy val layer
    : ZLayer[Scope & ZPool[Throwable, Connection] & DatabaseClientConfig, Throwable, DatabaseClient] =
    ZLayer.fromFunction(new DatabaseClient(_, _))

  lazy val connectionPoolLayer
    : URLayer[Scope & DatabaseClientConfig, ZPool[Throwable, Connection]] =
    ZLayer.fromZIO:
      for
        config <- ZIO.service[DatabaseClientConfig]
        acquire = ZIO.attemptBlocking:
          DriverManager.getConnection(
            config.url,
            config.username,
            config.password
          )
        release    = (c: Connection) => ZIO.succeed(c.close())
        connection = ZIO.acquireRelease(acquire)(release)
        pool <- ZPool.make(
          connection,
          Range(config.minSize, config.maxSize),
          config.ttl
        )
      yield pool

  def statement(c: Connection)(sql: Sql): RIO[Scope, PreparedStatement] =
    val acquire =
      ZIO.attemptBlocking:
        val stmt = c.prepareStatement(sql.query)

        sql
          .values
          .zipWithIndex
          .foreach: (value, index) =>
            value.set(stmt, index + 1)

        stmt

    val release =
      (s: PreparedStatement) => ZIO.succeed(s.close())

    ZIO.acquireRelease(acquire)(release)

  def executeQuery[A](sql: Sql)(f: ResultSetMapper[A])
    : ZIO[Connection, Throwable, A] =
    ZIO.scoped:
      for
        conn <- ZIO.service[Connection]
        stmt <- statement(conn)(sql)
        rs   <- ZIO.attemptBlocking(stmt.executeQuery())
      yield f(rs)

  def executeUpdate(sql: Sql): ZIO[Connection, Throwable, Int] =
    ZIO.scoped:
      for
        conn <- ZIO.service[Connection]
        stmt <- statement(conn)(sql)
        rc   <- ZIO.attemptBlocking(stmt.executeUpdate())
      yield rc
