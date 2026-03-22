package nl.thijsnissen.auth

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import java.time.Duration
import java.util.UUID
import scala.util.Random
import zio.*
import zio.test.*

object AuthenticationSpec extends ZIOSpecDefault:
  import AuthenticationSpecTestData.*

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Authentication")(
      test("password is valid"):
        val password = randomString(length = 25)

        for
          service  <- ZIO.service[Authentication]
          hash     <- service.encryptPassword(password = password)
          verified <- service.verifyPassword(password = password, hash = hash)
        yield assertTrue(verified)
      ,
      test("password is invalid"):
        for
          service  <- ZIO.service[Authentication]
          hash     <- service.encryptPassword(password = randomString())
          verified <-
            service.verifyPassword(password = randomString(), hash = hash)
        yield assertTrue(!verified)
      ,
      test("jwt is valid"):
        for
          service  <- ZIO.service[Authentication]
          jwt      <- service.generateJwt(userId = randomUuid(), ttl = Duration.ofDays(1))
          verified <- service.verifyJwt(jwt = jwt.token)
        yield assertTrue(jwt == verified)
      ,
      test("jwt is invalid"):
        for
          service  <- ZIO.service[Authentication]
          verified <- service.verifyJwt(jwt = randomString()).exit
        yield assert(verified)(Assertion.failsWithA[JWTVerificationException])
      ,
      test("jwt is expired"):
        for
          service  <- ZIO.service[Authentication]
          jwt      <- service.generateJwt(userId = randomUuid(), ttl = Duration.ZERO)
          verified <- service.verifyJwt(jwt = jwt.token).exit
        yield assert(verified)(Assertion.failsWithA[TokenExpiredException])
      ,
      test("csrf is valid"):
        val sessionId = Some(randomUuid())

        for
          service  <- ZIO.service[Authentication]
          csrf     <- service.generateCsrf(sessionId = sessionId)
          verified <- service.verifyCsrf(
            sessionId = sessionId,
            cookie = csrf,
            header = csrf
          )
        yield assertTrue(verified)
      ,
      test("csrf is invalid"):
        val sessionId = Some(randomUuid())

        for
          service  <- ZIO.service[Authentication]
          csrf     <- service.generateCsrf(sessionId = sessionId)
          verified <- service.verifyCsrf(
            sessionId = sessionId,
            cookie = csrf,
            header = randomString()
          )
        yield assertTrue(!verified)
    ).provide(
      Authentication.layer,
      ZLayer.succeed(AuthenticationConfig(
        password = PasswordConfig(pepper = randomString()),
        jwt = JwtConfig(secret = randomString(), issuer = randomString()),
        csrf = CsrfConfig(secret = randomString())
      ))
    )

object AuthenticationSpecTestData:
  def randomString(length: Int = 10): String = Random.alphanumeric.take(length).mkString
  def randomUuid(): UUID = UUID.randomUUID()
