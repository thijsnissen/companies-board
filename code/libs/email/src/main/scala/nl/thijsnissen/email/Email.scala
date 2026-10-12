package nl.thijsnissen.email

final case class Email(
  to: List[String],
  subject: String,
  html: String = "",
  text: String = ""
)
