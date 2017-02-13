package example
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}
object App {
  def main(args: Array[String]) {
    println(s"args: ${args.length}")
    val options = OptionParser(args)
    options.command match {
      case ClientCommand =>
        val c = new Client(port = options.port)
        options.hostname.foreach(c.host = _)

        Await.ready(c.connectToSocket(), 5.seconds)

        c.socket.write("ping")
        val text = c.socket.readLine match {
          case Success(line) => s"client got: $line"
          case Failure(ex) => s"client failed: ${ex.toString}"
        }
        println(text)
      case ServerCommand =>
        val s = new Server(port = options.port)
        import RichSocket._
        s.forEachConnection { socket =>
          println(s"Client connected: ${socket.toString}")
          val text = socket.readLine match {
            case Success(line) => s"server got: $line"
            case Failure(ex) => s"server failed: ${ex.toString}"
          }
          println("simulating work…")
          Thread.sleep(2000)
          socket.write("pong")
          socket.close()
        }
      case HelpCommand =>
        println("help text would be here, dummy")
      case UnknownCommand =>
        println("wtf mate")
    }
  }
}
sealed trait Command
case object ClientCommand extends Command
case object ServerCommand extends Command
case object HelpCommand extends Command
case object UnknownCommand extends Command
case class Options(command: Command,
                   port: Int,
                   hostname: Option[String] = None)

object OptionParser {
  def apply(args: Array[String]): Options = {
    val command: Command = args.headOption
      .map {
        case "client" => ClientCommand
        case "server" => ServerCommand
        case "help" => HelpCommand
        case _ => UnknownCommand
      }
      .getOrElse(UnknownCommand)
    val port = args(1).toInt
    val hostname = args.lift(2)

    Options(command, port, hostname)
  }
}
