

object Recorder extends App {

  val props = new RecorderPropertiesBuilder()
    .simulationsFolder(IDEPathHelper.mavenSourcesDirectory.toString)
    .resourcesFolder(IDEPathHelper.mavenResourcesDirectory.toString)
    .simulationPackage("computerdatabase")

  GatlingRecorder.fromMap(props.build, Some(IDEPathHelper.recorderConfigFile))
}