package spectral

import anticipation.*, fileApi.galileiApi
import galilei.*, filesystemOptions.{createNonexistent, dereferenceSymlinks, overwritePreexisting, deleteRecursively, createNonexistentParents}
import serpentine.*, hierarchies.unix
import rudiments.*
import guillotine.*
import gossamer.*
import turbulence.*
import eucalyptus.*
import perforate.*
import spectacular.*
import ambience.*
import fulminate.*

object Installer:

  object Result:
    given Communicable[Result] =
      case AlreadyOnPath(script, path) => msg"the $script command is already installed at $path"
      case Installed(script, path)     => msg"the $script command was installed to $path"
      case PathNotWritable             => msg"no directory on the PATH environment variable was writable"

  enum Result:
    case AlreadyOnPath(script: Text, path: Text)
    case Installed(script: Text, path: Text)
    case PathNotWritable

  def install
      ()
      (using service: DaemonService[?], log: Log[Text])
      (using Raises[ExecError], Raises[NumberError], Raises[SystemPropertyError], Raises[IoError],
          Raises[StreamCutError], Raises[NotFoundError], Raises[PathError], Raises[EnvironmentError])
      : Result =
    import workingDirectories.default
    import systemProperties.jvm
    import environments.jvm
    val command: Text = service.scriptName
    val scriptPath = sh"sh -c 'command -v $command'".exec[Text]()

    if safely(scriptPath.decodeAs[Path]) == service.script
    then Result.AlreadyOnPath(command, service.script.show)
    else
      val payloadSize: ByteSize = ByteSize(Properties.spectral.payloadSize[Int]())
      val jarSize: ByteSize = ByteSize(Properties.spectral.jarSize[Int]())
      val scriptFile: File = service.script.as[File]
      val fileSize = scriptFile.size()
      val prefixSize = fileSize - payloadSize - jarSize
      val stream = scriptFile.stream[Bytes]
      val paths: List[Path] = Environment.path
      val installDirectory = paths.filter(_.exists()).view.map(_.as[Directory]).find(_.writable()).maybe
      
      val installFile = installDirectory.mm: directory =>
        (directory / PathName(command)).make[File]()

      Log.info(t"Writing to ${installFile.debug}")
      installFile.mm: file =>
        (stream.take(prefixSize) ++ stream.drop(fileSize - jarSize)).writeTo(file)
        file.executable() = true
        Result.Installed(command, service.script.show)
      .or:
        Result.PathNotWritable
        