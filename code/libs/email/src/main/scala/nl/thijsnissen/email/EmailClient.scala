package nl.thijsnissen.email

import jakarta.mail.*
import jakarta.mail.internet.*
import java.util.Properties
import zio.*

class EmailClient(config: EmailClientConfig):
  def send(email: Email): Task[Unit] =
    for
      properties <- makeProperties
      session    <- makeSession(properties)
      message    <- makeMessage(session)(email)
    yield Transport.send(message)

  private lazy val makeProperties: Task[Properties] =
    ZIO.attempt:
      val properties = new Properties

      properties.put("mail.smtp.auth", "true")
      properties.put("mail.smtp.starttls.enable", "true")
      properties.put("mail.smtp.host", config.host)
      properties.put("mail.smtp.port", config.port)
      properties.put("mail.debug", "true")

      properties

  private def makeSession(properties: Properties): Task[Session] =
    ZIO.attempt:
      Session.getInstance(
        properties,
        new Authenticator:
          override def getPasswordAuthentication: PasswordAuthentication =
            new PasswordAuthentication(config.username, config.password)
      )

  private def makeMessage(session: Session)(email: Email): Task[Message] =
    ZIO.attempt:
      val message = new MimeMessage(session)
      val content = new MimeMultipart

      message.setFrom(config.from)
      message.setRecipients(
        Message.RecipientType.TO,
        email.to.map(new InternetAddress(_)).toArray[Address]
      )
      message.setSubject(email.subject)

      if email.html.nonEmpty then
        val part = new MimeBodyPart

        part.setContent(email.html, "text/html; charset=utf-8")

        content.addBodyPart(part)

      if email.text.nonEmpty then
        val part = new MimeBodyPart

        part.setContent(email.text, "text/plain; charset=utf-8")

        content.addBodyPart(part)

      message.setContent(content)

      message

object EmailClient:
  lazy val layer: URLayer[EmailClientConfig, EmailClient] =
    ZLayer.fromFunction(new EmailClient(_))

  def render(
    html: String,
    text: String,
    data: EmailData
  )(
    to: List[String]
  ): Task[Email] =
    ZIO.attempt:
      val (renderedHtml, renderedText) =
        data.data.foldLeft(html, text):
          case ((h, t), (k, v)) =>
            (h.replace(s"{{$k}}", v), t.replace(s"{{$k}}", v))

      Email(
        to = to,
        subject = data.subject,
        html = renderedHtml,
        text = renderedText
      )
