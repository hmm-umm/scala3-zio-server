package io.github.doohochang.scala3zioserver
package http.service

import cats.effect.*
import zio.*
import zio.interop.catz.*
import org.http4s.*
import org.http4s.dsl.Http4sDslBinCompat

import service.GreetingService

class GreetingHttpService(service: GreetingService):
  object dsl extends Http4sDslBinCompat[Task]
  import dsl.*

  val routes: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case GET -> Root / "greet" / name =>
      for
        greeting <- service.greet(name)
        response <- Ok(greeting)
      yield response
  }

object GreetingHttpService:
  val layer: URLayer[Has[GreetingService], Has[GreetingHttpService]] =
    ZLayer.fromEffect(
      for greetingService <- ZIO.service[GreetingService]
      yield GreetingHttpService(greetingService)
    )
