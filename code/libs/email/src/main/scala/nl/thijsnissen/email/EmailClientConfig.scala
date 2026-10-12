package nl.thijsnissen.email

final case class EmailClientConfig(
  host: String,
  port: Int,
  username: String,
  password: String,
  from: String
)
