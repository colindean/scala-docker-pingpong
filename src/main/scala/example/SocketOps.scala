package example

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket

import scala.concurrent.{Future, blocking}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

trait SocketOps {
  val socket: Option[Socket] = None
  def readLine: Try[String] = {
    if(socket.isEmpty) return Failure(new RuntimeException("socket not created"))
    if(socket.get.isConnected) {
      blocking { Try(new BufferedReader(new InputStreamReader(socket.get.getInputStream)).readLine()) }
    } else {
      Failure(new RuntimeException("read: socket not connected"))
    }
  }
  def write(data: String): Try[Unit] = {
    if(socket.isEmpty) return Failure(new RuntimeException("socket not created"))
    if(socket.get.isConnected) {
      val out = new PrintWriter(socket.get.getOutputStream)
      out.println(data)
      out.flush()
      Success()
    } else {
      Failure(new RuntimeException("write: socket not connected"))
    }
  }
}

class RichSocket(override val socket: Option[Socket]) extends SocketOps
object RichSocket {
  implicit def enrichSocket(socket: Socket): RichSocket = new RichSocket(Option(socket))
}

import example.Client._

import scala.concurrent.ExecutionContext.Implicits.global
class Client(var host: String = LOCALHOST, val port: Int) {
  var socket: RichSocket = _
  def connectToSocket(): Future[Unit] = {
    Future {
      Try( new Socket(host, port)) match {
        case Success(s) => socket = s
        case Failure(ex) => println(s"failed to open client socket: ${ex.toString}")
      }
    }
  }
}
object Client {
  val LOCALHOST = "127.0.0.1"
}