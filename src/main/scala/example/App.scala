/**
  * BEGIN_COPYRIGHT
  *
  * IBM Confidential
  * OCO Source Materials
  *
  * 5725-Y07
  * (C) Copyright IBM Corp. 2017. All Rights Reserved.
  *
  * The source code for this program is not published or otherwise
  * divested of its trade secrets, irrespective of what has been
  * deposited with the U.S. Copyright Office.
  *
  * END_COPYRIGHT
  */
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

        c.write("ping")
        c.readLine match {
          case Success(line) => s"client got: $line"
          case Failure(ex) => s"client failed: ${ex.toString}"
        }
      case ServerCommand =>
        val s = new Server(port = options.port)
        s.createListenSocket()
        s.acceptConnection()
        println(s"server got: ${s.readLine}")
        println("simulating work…")
        Thread.sleep(2000)
        s.write("pong")
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
