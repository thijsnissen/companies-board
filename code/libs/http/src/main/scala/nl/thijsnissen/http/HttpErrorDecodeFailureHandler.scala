package nl.thijsnissen.http

import java.time.Instant
import java.util.UUID
import sttp.model.Header
import sttp.model.StatusCode
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.server
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler.FailureMessages
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler.respond
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.tapir.ztapir.headers
import sttp.tapir.ztapir.statusCode

object HttpErrorDecodeFailureHandler:
  def apply[F[_]]: DefaultDecodeFailureHandler[F] =
    DefaultDecodeFailureHandler[F](
      respond,
      FailureMessages.failureMessage,
      failureResponse
    )

  def failureResponse(
    statusCode: StatusCode,
    headers: List[Header],
    message: String
  ): ValuedEndpointOutput[?] =
    server.model.ValuedEndpointOutput(
      statusCode.and(headers).and(jsonBody[HttpError]),
      (
        statusCode,
        headers,
        HttpError(
          status = statusCode.code,
          code = "ValidationError",
          message = message,
          id = UUID.randomUUID(),
          timestamp = Instant.now
        )
      )
    )
