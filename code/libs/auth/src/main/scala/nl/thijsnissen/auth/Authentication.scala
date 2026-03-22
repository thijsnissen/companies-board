package nl.thijsnissen.auth

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.password4j.*

import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import zio.*

import java.nio.charset.StandardCharsets

class Authentication(config: AuthenticationConfig):
  def encryptPassword(password: String): Task[String] =
    ZIO.attempt:
      Password
        .hash(password)
        .addRandomSalt()
        .addPepper(config.password.pepper)
        .withArgon2
        .getResult

  def verifyPassword(password: String, hash: String): Task[Boolean] =
    ZIO.attempt:
      Password
        .check(password, hash)
        .addPepper(config.password.pepper)
        .withArgon2

  def generateJwt(userId: UUID, ttl: Duration): Task[Jwt] =
    val jti = UUID.randomUUID()
    val now = Instant.now.truncatedTo(ChronoUnit.SECONDS)

    ZIO
      .attempt:
        JWT
          .create
          .withIssuer(config.jwt.issuer)
          .withIssuedAt(now)
          .withExpiresAt(now.plus(ttl))
          .withSubject(userId.toString)
          .withJWTId(jti.toString)
          .sign(Algorithm.HMAC512(config.jwt.secret))
      .map: jwt =>
        Jwt(jti, userId, jwt, now.plus(ttl))

  def verifyJwt(jwt: String): Task[Jwt] =
    ZIO
      .attempt:
        JWT
          .require(Algorithm.HMAC512(config.jwt.secret))
          .withIssuer(config.jwt.issuer)
          .build
          .verify(jwt)
      .map: token =>
        Jwt(
          UUID.fromString(token.getId),
          UUID.fromString(token.getSubject),
          token.getToken,
          token.getExpiresAtAsInstant
        )

  def generateCsrf(sessionId: Option[UUID]): Task[String] =
    for
      randomValue <- ZIO.attempt:
        val randomBytes = new Array[Byte](32)
        new SecureRandom().nextBytes(randomBytes)

        Base64
          .getUrlEncoder
          .withoutPadding
          .encodeToString(randomBytes)
      token <- encodeCsrf(sessionId, randomValue)
    yield s"$token.$randomValue"

  def verifyCsrf(
    sessionId: Option[UUID],
    cookie: String,
    header: String
  ): Task[Boolean] =
    (for
      (cookieToken, cookieRandom) <- ZIO.attempt:
        cookie.split("\\.", 2) match
          case Array(t, r) => (t, r)
      (headerToken, headerRandom) <- ZIO.attempt:
        header.split("\\.", 2) match
          case Array(t, r) => (t, r)
      cookie <- encodeCsrf(sessionId, cookieRandom)
      header <- encodeCsrf(sessionId, headerRandom)
    yield java.security.MessageDigest.isEqual(
      cookieToken.getBytes(StandardCharsets.UTF_8),
      cookie.getBytes(StandardCharsets.UTF_8)
    ) && java.security.MessageDigest.isEqual(
      headerToken.getBytes(StandardCharsets.UTF_8),
      header.getBytes(StandardCharsets.UTF_8)
    )).catchSome:
      case _: MatchError => ZIO.succeed(false)

  private def encodeCsrf(sessionId: Option[UUID], random: String): Task[String] =
    ZIO.attempt:
      val session = sessionId.fold("")(_.toString)

      val hmac = Mac.getInstance("HmacSHA256")
      hmac.init(new SecretKeySpec(config.csrf.secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))

      Base64
        .getUrlEncoder
        .withoutPadding
        .encodeToString:
          hmac.doFinal:
            s"${session.length}!$session!${random.length}!$random".getBytes(StandardCharsets.UTF_8)

object Authentication:
  lazy val layer: URLayer[AuthenticationConfig, Authentication] =
    ZLayer.fromFunction(new Authentication(_))
