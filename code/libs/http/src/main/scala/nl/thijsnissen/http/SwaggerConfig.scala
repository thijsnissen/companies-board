package nl.thijsnissen.http

final case class SwaggerConfig(
  endpoints: Endpoints,
  title: String,
  version: String
)
