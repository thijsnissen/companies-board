package nl.thijsnissen.db

import java.time.Duration

final case class DatabaseClientConfig(
  host: String,
  port: Int,
  driver: String,
  database: String,
  username: String,
  password: String,
  minSize: Int,
  maxSize: Int,
  ttl: Duration,
  til: TransactionIsolationLevel,
  cleanDisabled: Boolean,
  migrations: List[String]
):
  lazy val url: String =
    s"jdbc:$driver://$host:$port/$database"
