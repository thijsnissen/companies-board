import sbt.*
import sbt.Keys.*

object Settings:
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
      "-explain",
      defaultImports
    ),
    Compile / run / connectInput := true,
    Compile / run / fork         := true,
    Compile / run / javaOptions ++= Seq(
      "-Xmx4G",
      "-XX:+HeapDumpOnOutOfMemoryError",
      "-XX:+UseG1GC"
    ),
    libraryDependencySchemes += "dev.zio" %% "zio-json" % VersionScheme.Always
  )

  private lazy val defaultImports =
    Seq(
      "java.lang",
      "scala",
      "scala.Predef",
      "scala.annotation",
      "scala.util.chaining"
    ).mkString("-Yimports:", ",", "")
