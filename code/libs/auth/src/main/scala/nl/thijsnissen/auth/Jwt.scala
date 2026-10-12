package nl.thijsnissen.auth

import java.time.Instant
import java.util.UUID

final case class Jwt(
  jti: UUID,
  userId: UUID,
  token: String,
  expiration: Instant
)
