package nl.thijsnissen.foundations

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*

object QuillDemo extends ZIOAppDefault:
  val program: ZIO[JobRepository, Throwable, Unit] =
    for
      repo <- ZIO.service[JobRepository]
      _ <- repo.create(
        Job(
        id = -1,
        title = "Software Engineer",
        url = "rockthejvm.com",
        company = "Rock the JVM"
      )
      )
      _ <- repo.create(
        Job(
        id = -1,
        title = "Instructor",
        url = "rockthejvm.com",
        company = "Rock the JVM")
      )
    yield ()

  override def run: ZIO[Any, Throwable, Unit] =
    program.provide(
      JobRepositoryLive.layer,
      Quill.Postgres.fromNamingStrategy(SnakeCase), // quill instance
      Quill.DataSource.fromPrefix(
        "mydbconf"
      ) // reads the config section in application.conf and creates datasource
    )

// repository
trait JobRepository:
  def create(job: Job): Task[Job]
  def update(id: Long, op: Job => Job): Task[Job]
  def delete(id: Long): Task[Job]
  def getById(id: Long): Task[Option[Job]]
  def getAll: Task[List[Job]]

class JobRepositoryLive(quill: Quill.Postgres[SnakeCase]) extends JobRepository:
  // step 1: import quill.*
  // step 2: schemas for create, update ...

  // some methods e.g. run a query
  import quill.*

  // specifying the table name
  inline given schema: SchemaMeta[Job] =
    schemaMeta[Job]("jobs")

  // columns to exclude from insert
  inline given insMeta: InsertMeta[Job] =
    insertMeta[Job](_.id)

  // columns to exclude from update
  inline given upMeta: UpdateMeta[Job] =
    updateMeta[Job](_.id)

  def create(job: Job): Task[Job] =
    run:
      query[Job]
        .insertValue(lift(job))
        .returning(j => j)

  def update(id: Long, op: Job => Job): Task[Job] =
    for
      current <- getById(id).someOrFail(
        new RuntimeException(s"Could not update: missing key $id")
      )
      updated <- run:
        query[Job]
          .filter(_.id == lift(id))
          .updateValue(lift(op(current)))
          .returning(j => j)
    yield updated

  def delete(id: Long): Task[Job] =
    run:
      query[Job]
        .filter(_.id == lift(id))
        .delete
        .returning(j => j)

  def getById(id: Long): Task[Option[Job]] =
    run:
      query[Job]
        .filter(_.id == lift(id))
        .take(1)
    .map(_.headOption)

  def getAll: Task[List[Job]] =
    run(query[Job])

object JobRepositoryLive:
  val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, JobRepository] =
    ZLayer:
      ZIO.serviceWith[Quill.Postgres[SnakeCase]](
        quill => JobRepositoryLive(quill)
      )
