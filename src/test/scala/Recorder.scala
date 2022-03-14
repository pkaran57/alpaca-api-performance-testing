import io.gatling.recorder.GatlingRecorder
import io.gatling.recorder.config.RecorderPropertiesBuilder

/**
 * This file is optional. Only needed when running from IDE.
 */
object Recorder extends App {

  val props = new RecorderPropertiesBuilder()
    .simulationsFolder(IDEPathHelper.mavenSourcesDirectory.toString)
    .resourcesFolder(IDEPathHelper.mavenResourcesDirectory.toString)
    .simulationPackage("com.ker")

  GatlingRecorder.fromMap(props.build, Some(IDEPathHelper.recorderConfigFile))
}
