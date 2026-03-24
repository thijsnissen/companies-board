package nl.thijsnissen.db

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.flywaydb.core.api.output.MigrateResult
import zio.*

class Migrations(flyway: Flyway):
  def clean(): Task[Unit] =
    ZIO.attemptBlocking(flyway.clean())

  def baseline(): Task[Unit] =
    ZIO.attemptBlocking(flyway.baseline())

  def repair(): Task[Unit] =
    ZIO.attemptBlocking(flyway.repair())

  def migrate(): Task[MigrateResult] =
    ZIO.attemptBlocking(flyway.migrate())

object Migrations:
  lazy val layer: RLayer[DatabaseClientConfig, Migrations] =
    ZLayer.fromZIO:
      for
        config <- ZIO.service[DatabaseClientConfig]
        flyway <- ZIO.attempt:
          Flyway
            .configure()
            .locations(config.migrations*)
            .cleanDisabled(config.cleanDisabled)
            .loggers("slf4j")
            .dataSource(config.url, config.username, config.password)
            .load()
      yield new Migrations(flyway)

  lazy val run: RIO[DatabaseClientConfig, MigrateResult] =
    ZIO
      .serviceWithZIO[Migrations]: m =>
        m.migrate().catchSome:
          case e: FlywayException =>
            ZIO.logError(e.getMessage) *> m.repair() *> m.migrate()
      .provideSome[DatabaseClientConfig](layer)
