import sbt.*

object Dependencies:
  lazy val foundations = Seq(
    "dev.zio"                     %% "zio"                      % Version.zio,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"    % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"  % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio"           % Version.tapir,
    "io.getquill"                 %% "quill-jdbc-zio"           % Version.zioQuill,
    "org.postgresql"               % "postgresql"               % Version.postgresql
  )

  lazy val application = Seq(
    "dev.zio"       %% "zio-config-typesafe" % Version.zioConfig,
    "dev.zio"       %% "zio-config-magnolia" % Version.zioConfig,
    "dev.zio"       %% "zio-logging"         % Version.zioLogging,
    "dev.zio"       %% "zio-logging-slf4j2"  % Version.zioLogging,
    "ch.qos.logback" % "logback-classic"     % Version.logback
  )

  lazy val server = Seq(
    "dev.zio"                       %% "zio"         % Version.zio,
    "com.stripe"                     % "stripe-java" % Version.stripe,
    "com.softwaremill.sttp.client4" %% "zio"         % Version.client4,
    "com.softwaremill.sttp.client4" %% "zio-json"    % Version.client4
  )

  lazy val test = Seq(
    "dev.zio" %% "zio-test"          % Version.zioTest % Test,
    "dev.zio" %% "zio-test-sbt"      % Version.zioTest % Test,
    "dev.zio" %% "zio-test-magnolia" % Version.zioTest % Test
  )

  lazy val libAuth = Seq(
    "dev.zio"       %% "zio"        % Version.zio,
    "com.password4j" % "password4j" % Version.password4j,
    "com.auth0"      % "java-jwt"   % Version.javaJwt
  )

  lazy val libDb = Seq(
    "dev.zio"       %% "zio"                        % Version.zio,
    "org.postgresql" % "postgresql"                 % Version.postgresql,
    "org.flywaydb"   % "flyway-core"                % Version.flyway,
    "org.flywaydb"   % "flyway-database-postgresql" % Version.flyway
  )

  lazy val libEmail = Seq(
    "dev.zio"          %% "zio"              % Version.zio,
    "jakarta.mail"      % "jakarta.mail-api" % Version.jakartaMail,
    "org.eclipse.angus" % "angus-mail"       % Version.angusMail
  )

  lazy val libHttp = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"    % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"  % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio"           % Version.tapir
  )
