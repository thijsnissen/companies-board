package nl.thijsnissen.http

import java.time.Instant
import java.util.UUID
import sttp.model.StatusCode
import sttp.tapir.EndpointOutput
import sttp.tapir.Schema
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.ztapir.*
import zio.IO
import zio.ZIO
import zio.json.JsonCodec

final case class HttpError(
  status: Int,
  code: String,
  message: String,
  id: UUID,
  timestamp: Instant
) derives JsonCodec, Schema

object HttpError:
  lazy val defaultErrorOutput: EndpointOutput[(StatusCode, HttpError)] =
    statusCode
      .description(StatusCode.NotFound, "Not Found")
      .description(StatusCode.BadRequest, "Bad Request")
      .description(StatusCode.Forbidden, "Forbidden")
      .description(StatusCode.Unauthorized, "Unauthorized")
      .description(StatusCode.Conflict, "Conflict")
      .description(StatusCode.InternalServerError, "Internal Server Error")
      .and(jsonBody[HttpError])

  def applyM(
    status: Int,
    code: String,
    message: String,
    id: UUID = UUID.randomUUID(),
    timestamp: Instant = Instant.now
  ): IO[(StatusCode, HttpError), Nothing] =
    val error =
      HttpError(
        status = status,
        code = code,
        message = message,
        id = id,
        timestamp = timestamp
      )

    ZIO.fail(StatusCode.apply(error.status) -> error)

  def applyMLog(
    status: Int,
    code: String,
    message: String,
    id: UUID = UUID.randomUUID(),
    timestamp: Instant = Instant.now
  ): IO[(StatusCode, HttpError), Nothing] =
    applyM(
      status = status,
      code = code,
      message = message,
      id = id,
      timestamp = timestamp
    ).tapError: (_, httpError) =>
      ZIO.logWarning(s"An http error occurred: $httpError")
