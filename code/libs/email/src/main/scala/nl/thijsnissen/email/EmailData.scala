package nl.thijsnissen.email

trait EmailData extends Product:
  def subject: String

  def data: Map[String, String] =
    productElementNames
      .zip(productIterator)
      .map(_ -> _.toString)
      .toMap
