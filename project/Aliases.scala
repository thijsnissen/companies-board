import sbt.*

object Aliases {
  lazy val common =
    addCommandAlias("format", "scalafmtAll;scalafmtSbt") ++
      addCommandAlias("check", "scalafmtCheckAll;scalafmtSbtCheck") ++
      addCommandAlias("validate", "format;clean;compile;Test/compile") ++
      addCommandAlias("vtest", "validate;test")
}
