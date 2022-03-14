import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

/**
 * This file is optional. Only needed when running from IDE.
 */
object Engine extends App {

  val props = new GatlingPropertiesBuilder()
    .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString)
    .resultsDirectory(IDEPathHelper.resultsDirectory.toString)
    .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString)

  Gatling.fromMap(props.build)
}
