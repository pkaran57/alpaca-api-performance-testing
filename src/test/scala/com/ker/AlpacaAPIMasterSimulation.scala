package com.ker

import io.gatling.core.Predef._
import io.gatling.core.feeder.Feeder
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import io.gatling.http.Predef._

class AlpacaAPIMasterSimulation extends Simulation {

  val stockSymbolFeeder: Feeder[Any] = csv("stock-symbols.csv").eager.queue()
  val getTrades: ScenarioBuilder = scenario("Get Trades")
    .repeat(stockSymbolFeeder.size) {
      feed(stockSymbolFeeder)
        .group("get trades for #{stock_symbol}") {
          doWhile(session => if (session("next_page_token").validate[String].toOption.isDefined) true else false, "counter") {
            exec(http("page #{counter}")
              .get("/stocks/#{stock_symbol}/trades")
              .queryParam("""start""", start) // Note the triple double quotes: used in Scala for protecting a whole chain of characters (no need for backslash)
              .queryParam("""end""", end)
              .queryParam("""limit""", """10000""")
              .queryParam("""page_token""", session => if (session.contains("next_page_token")) session("next_page_token").as[String] else "")
              .check(status is 200)
              .check(jsonPath("$.next_page_token").saveAs("next_page_token")))
          }
        }
        .exec(session => session.remove("next_page_token"))
    }
  val getQuotes: ScenarioBuilder = scenario("Get Quotes")
    .repeat(stockSymbolFeeder.size) {
      feed(stockSymbolFeeder)
        .group("get quotes for #{stock_symbol}") {
          doWhile(session => if (session("next_page_token").validate[String].toOption.isDefined) true else false, "counter") {
            exec(http("page #{counter}")
              .get("/stocks/#{stock_symbol}/quotes")
              .queryParam("""start""", start) // Note the triple double quotes: used in Scala for protecting a whole chain of characters (no need for backslash)
              .queryParam("""end""", end)
              .queryParam("""limit""", """10000""")
              .queryParam("""page_token""", session => if (session.contains("next_page_token")) session("next_page_token").as[String] else "")
              .check(status is 200)
              .check(jsonPath("$.next_page_token").saveAs("next_page_token")))
          }
        }
        .exec(session => session.remove("next_page_token"))
    }
  val getBars: ScenarioBuilder = scenario("Get Bars")
    .repeat(stockSymbolFeeder.size) {
      feed(stockSymbolFeeder)
        .group("get bars for #{stock_symbol}") {
          doWhile(session => if (session("next_page_token").validate[String].toOption.isDefined) true else false, "counter") {
            exec(http("page #{counter}")
              .get("/stocks/#{stock_symbol}/bars")
              .queryParam("""start""", start) // Note the triple double quotes: used in Scala for protecting a whole chain of characters (no need for backslash)
              .queryParam("""end""", end)
              .queryParam("""limit""", """10000""")
              .queryParam("""timeframe""", """1Min""")
              .queryParam("""page_token""", session => if (session.contains("next_page_token")) session("next_page_token").as[String] else "")
              .check(status is 200)
              .check(jsonPath("$.next_page_token").saveAs("next_page_token")))
          }
        }
        .exec(session => session.remove("next_page_token"))
    }
  private val start: String = """2022-03-11T06:30:00-08:00""" // 6:30 AM PST March 11, 2022
  private val end: String = """2022-03-11T13:00:00-08:00""" // 1 PM PST March 11, 2022
  private val masterSimulation: PopulationBuilder = getTrades.inject(atOnceUsers(1))
    .andThen(getQuotes.inject(atOnceUsers(1)))
    .andThen(getBars.inject(atOnceUsers(1)))

  setUp(masterSimulation)
    .protocols(http
      .baseUrl("https://data.alpaca.markets/v2")
      .headers(
        Map("Apca-Api-Key-Id" -> "PK4KLOSGHPO9TX8X99YG",
          "Apca-Api-Secret-Key" -> "R9KiY5fcN1k5yiUgCWSRWqOgK4zDGQkHp1AdyrGX")
      ))
}
