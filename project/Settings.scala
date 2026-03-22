import sbt.*
import sbt.Keys.*

object Settings {
  lazy val common = Seq(
    scalacOptions ++= Seq(
      "-encoding",
      "utf8",
      "-language:implicitConversions",
      "-language:existentials",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Werror",
      "-Wunused:imports",
      "-Wunused:locals",
      "-print-lines",
      "-explain",
      defaultImports
    ),
    Compile / run / connectInput := true,
    ThisBuild / run / fork       := true,
    ThisBuild / run / javaOptions ++= Seq(
      "-XX:+HeapDumpOnOutOfMemoryError",
      "-XX:+UseG1GC"
    )
  )

  private lazy val defaultImports =
    Seq(
      "java.lang",
      "scala",
      "scala.Predef",
      "scala.annotation",
      "scala.util.chaining"
    ).mkString("-Yimports:", ",", "")
}
