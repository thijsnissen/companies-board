package nl.thijsnissen.auth

final case class JwtConfig(
  secret: String,
  issuer: String
)
