package nl.thijsnissen.http

import java.time.Instant
import java.util.UUID
import sttp.tapir.Schema
import zio.json.JsonCodec

final case class Cursor(timestamp: Instant, uuid: UUID) derives JsonCodec, Schema
