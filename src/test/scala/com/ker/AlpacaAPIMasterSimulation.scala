package com.ker

import io.gatling.core.Predef._
import io.gatling.core.feeder.Feeder
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import io.gatling.http.Predef._

class AlpacaAPIMasterSimulation extends Simulation {

  // Stock trading start and end date time for our simulation
  private val start: String = """2022-03-11T06:30:00-08:00""" // 6:30 AM PST March 11, 2022
  private val end: String = """2022-03-11T13:00:00-08:00""" // 1 PM PST March 11, 2022

  // number of stock symbols in "src/test/resources/stock-symbols.csv"
  private val totalNumberOfStockSymbolsInCSV: Int = getStockSymbolsCSVFeeder.size
  // scenario that gets trades for all the stock symbols specified in "stock-symbols.csv"
  private val getTradesScenario: ScenarioBuilder = scenario("Get Trades")
    .group("Get trades") {
      repeat(totalNumberOfStockSymbolsInCSV) {
        feed(getStockSymbolsCSVFeeder)
          .group("get trades for #{stock_symbol}") {
            // iterate through all pages of trades for a given symbol
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
          // reset "next_page_token" session attribute for next stock symbol
          .exec(session => session.remove("next_page_token"))
      }
    }

  // scenario that gets quotes for all the stock symbols specified in "stock-symbols.csv"
  private val getQuotesScenario: ScenarioBuilder = scenario("Get Quotes")
    .group("Get Quotes") {
      repeat(totalNumberOfStockSymbolsInCSV) {
        feed(getStockSymbolsCSVFeeder)
          .group("get quotes for #{stock_symbol}") {
            // iterate through all pages of quotes for a given symbol
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
          // reset "next_page_token" session attribute for next stock symbol
          .exec(session => session.remove("next_page_token"))
      }
    }
  // scenario that gets 5Min bars for all the stock symbols specified in "stock-symbols.csv"
  private val getBarsScenario: ScenarioBuilder = scenario("Get 5 minutes bars")
    .group("Get 5 minutes bars") {
      repeat(totalNumberOfStockSymbolsInCSV) {
        feed(getStockSymbolsCSVFeeder)
          .group("get bars for #{stock_symbol}") {
            // iterate through all pages of bars for a given symbol
            doWhile(session => if (session("next_page_token").validate[String].toOption.isDefined) true else false, "counter") {
              exec(http("page #{counter}")
                .get("/stocks/#{stock_symbol}/bars")
                .queryParam("""start""", start) // Note the triple double quotes: used in Scala for protecting a whole chain of characters (no need for backslash)
                .queryParam("""end""", end)
                .queryParam("""limit""", """10000""")
                .queryParam("""timeframe""", """5Min""")
                .queryParam("""page_token""", session => if (session.contains("next_page_token")) session("next_page_token").as[String] else "")
                .check(status is 200)
                .check(jsonPath("$.next_page_token").saveAs("next_page_token")))
            }
          }
          // reset "next_page_token" session attribute for next stock symbol
          .exec(session => session.remove("next_page_token"))
      }
    }

  /**
   * Read in the stock symbols under "src/test/resources/stock-symbols.csv" and return an associated feeder
   *
   * @return queue feeder containing all stock symbols in csv
   */
  private def getStockSymbolsCSVFeeder: Feeder[Any] = {
    csv("stock-symbols.csv").eager.queue()
  }

  private val masterSimulation: PopulationBuilder = getTradesScenario.inject(atOnceUsers(1))
    .andThen(getQuotesScenario.inject(atOnceUsers(1)))
    .andThen(getBarsScenario.inject(atOnceUsers(1)))

  setUp(masterSimulation)
    .protocols(http
      .baseUrl("https://data.alpaca.markets/v2")
      .headers(
        // Alpaca api credentials go here
        Map("Apca-Api-Key-Id" -> "",
          "Apca-Api-Secret-Key" -> "")
      ))
}
