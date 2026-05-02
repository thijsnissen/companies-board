package nl.thijsnissen.http

final case class Csrf(
  cookie: String,
  header: String
)
