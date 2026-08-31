package nl.thijsnissen.http

import sttp.model.StatusCode
import sttp.tapir.server.interceptor.cors.CORSInterceptor
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.server.ziohttp.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.*
import zio.*
import zio.http.Server

class HttpServer(
  endpoints: Endpoints,
  config: HttpServerConfig,
  swaggerConfig: SwaggerConfig
):
  private lazy val prometheusMetrics: PrometheusMetrics[Task] =
    PrometheusMetrics.default[Task]()

  private lazy val serverOptions: ZioHttpServerOptions[Any] =
    ZioHttpServerOptions.customiseInterceptors
      .metricsInterceptor(prometheusMetrics.metricsInterceptor())
      .corsInterceptor(CORSInterceptor.default)
      .decodeFailureHandler(HttpErrorDecodeFailureHandler.apply)
      .options

  private lazy val healthEndpointImpl: ZServerEndpoint[Any, Any] =
    HttpServer
      .healthEndpoint
      .zServerLogic(_ => ZIO.succeed("Alive!"))

  private lazy val errorEndpointImpl: ZServerEndpoint[Any, Any] =
    HttpServer
      .errorEndpoint
      .zServerLogic(_ => ZIO.fail(StatusCode.InternalServerError -> "Error!"))

  private lazy val metricsEndpoint: ZServerEndpoint[Any, Any] =
    prometheusMetrics.metricsEndpoint

  private lazy val docEndpoints: List[ZServerEndpoint[Any, Any]] =
    SwaggerInterpreter()
      .fromServerEndpoints[Task](
        swaggerConfig.endpoints.value,
        swaggerConfig.title,
        swaggerConfig.version
      )

  private lazy val httpServerEndpoints: List[ZServerEndpoint[Any, Any]] =
    healthEndpointImpl :: errorEndpointImpl :: metricsEndpoint :: docEndpoints

  private lazy val program: Task[Nothing] =
    Server
      .serve(
        ZioHttpInterpreter(serverOptions).toHttp(
          httpServerEndpoints ::: endpoints.value
        )
      )
      .provide(Server.defaultWith(_.binding(config.host, config.port)))

  lazy val run: Task[Nothing] =
    ZIO.log(
      s"Go to http://${config.host}:${config.port}/docs to open SwaggerUI."
    ) *> program

object HttpServer:
  private lazy val healthEndpoint =
    endpoint
      .tag("HttpServer")
      .name("healthcheck")
      .description("healthcheck")
      .get
      .in("status")
      .out(plainBody[String])

  private lazy val errorEndpoint =
    endpoint
      .tag("HttpServer")
      .name("error")
      .description("error")
      .get
      .in("error")
      .errorOut(statusCode and plainBody[String])

  lazy val layer
    : URLayer[Endpoints & HttpServerConfig & SwaggerConfig, HttpServer] =
    ZLayer.fromFunction(new HttpServer(_, _, _))

  lazy val run: RIO[Endpoints & HttpServerConfig & SwaggerConfig, Nothing] =
    ZIO.serviceWithZIO[HttpServer](_.run)
      .provideSome[Endpoints & HttpServerConfig & SwaggerConfig](layer)
