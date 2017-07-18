package com.yoppworks.chat.infrastructure

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ FreeSpec, Matchers }

class StaticRoutesSpec extends FreeSpec with Matchers with ScalatestRouteTest with StaticRoutes {

  "/static" - {
    "get" - {
      "should return file and 200" in {
          Get("/static/validfile.html") ~> staticRoutes ~> check {
            assert(status === StatusCodes.OK)
            assert(responseAs[String] === "valid")
          }
        }
      "should return no file and 404 for files that don't exist" in {
          Get("/static/invalidfile.html") ~> Route.seal(staticRoutes) ~> check {
            assert(status === StatusCodes.NotFound)
            assert(responseAs[String] === "The requested resource could not be found.")
          }
        }
      "should return index.html when accessing the route path" in {
        Get() ~> staticRoutes ~> check {
          assert(status === StatusCodes.OK)
          assert(responseAs[String] contains "<HTML>")
        }
      }
    }
  }
}
