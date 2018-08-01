import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.{JsonElement, JsonParser}
import io.gatling.core.Predef._
import io.gatling.core.config.GatlingFiles
import io.gatling.http.Predef._

import scala.io.Source

class Basic1Simulation extends Simulation {

  private val path = GatlingFiles.dataDirectory.resolve("test.json")
  private val value: String = Source.fromFile(path.toString).getLines().mkString
  val mapper = new ObjectMapper
  private val parser: JsonParser = new JsonParser
  private val conf: JsonElement = parser.parse(value)
  private val envName = conf.getAsJsonObject.getAsJsonPrimitive("envName")

  println("json -----> ", envName)

  val httpConf = http
    .baseURL("http://computer-database.gatling.io") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val headers_10 = Map("Content-Type" -> "application/x-www-form-urlencoded") // Note the headers specific to a given request

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec(http("request_1").get("/"))
    .pause(1) // Note that Gatling has recorded real time pauses
    .exec(http("request_2").get("/computers?f=macbook"))
    .pause(1)
    .exec(http("request_3").get("/computers/6"))

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
}