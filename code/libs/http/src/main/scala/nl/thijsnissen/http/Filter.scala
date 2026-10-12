package nl.thijsnissen.http

import sttp.tapir.Schema
import zio.json.JsonCodec

final case class Filter(key: String, values: List[String]) derives JsonCodec, Schema
