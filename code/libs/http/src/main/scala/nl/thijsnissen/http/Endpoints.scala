package nl.thijsnissen.http

import sttp.tapir.ztapir.*

final case class Endpoints(value: List[ZServerEndpoint[Any, Any]])
