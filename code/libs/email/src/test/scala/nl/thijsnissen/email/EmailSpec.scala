package nl.thijsnissen.email

import scala.util.Random
import zio.*
import zio.test.*

object EmailSpec extends ZIOSpecDefault:
  import EmailSpecTestData.*

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Email")(
      test("Render"):
        val data = randomEmailData()
        val to   = randomStrings()

        EmailClient.render(
          html = "<b>{{name}}: {{age}}</b>",
          text = "{{name}}: {{age}}",
          data = data
        )(to = to).map: email =>
          assertTrue(
            email.html == s"<b>${data.name}: ${data.age}</b>",
            email.text == s"${data.name}: ${data.age}",
            email.subject == data.subject,
            email.to == to
          )
    )

object EmailSpecTestData:
  def randomString(length: Int = 10): String                           = Random.alphanumeric.take(length).mkString
  def randomStrings(length: Int = 10): List[String]                    = List.fill(length)(randomString())
  def randomInt(min: Int = Int.MinValue, max: Int = Int.MaxValue): Int = Random.between(min, max)
  def randomEmailData(name: String = randomString(), age: Int = randomInt()): Template = Template(name, age)

  case class Template(name: String, age: Int) extends EmailData:
    lazy val subject: String = randomString()
