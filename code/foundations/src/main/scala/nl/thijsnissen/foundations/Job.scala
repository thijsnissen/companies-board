package nl.thijsnissen.foundations

import zio.json.*

final case class Job(
  id: Long,
  title: String,
  url: String,
  company: String
)

object Job:
  // macro-based JSON codec (generated)
  given JsonCodec[Job] =
    DeriveJsonCodec.gen[Job]

// special request for the HTTP endpoint
final case class CreateJobRequest(
  title: String,
  url: String,
  company: String
)

object CreateJobRequest:
  // macro-based JSON codec (generated)
  given JsonCodec[CreateJobRequest] =
    DeriveJsonCodec.gen[CreateJobRequest]
