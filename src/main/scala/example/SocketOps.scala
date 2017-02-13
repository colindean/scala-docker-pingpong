package example

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{ServerSocket, Socket}

import scala.concurrent.{Future, blocking}
import scala.util.{Failure, Success, Try}

trait SocketOps {
  var socket: Option[Socket] = None
  val port: Int
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

import example.Client._
import scala.concurrent.ExecutionContext.Implicits.global
class Client(var host: String = LOCALHOST, val port: Int) extends SocketOps {
  def connectToSocket(): Future[Unit] = {
    Future {
      Try( new Socket(host, port)) match {
        case Success(s) => socket = Option(s)
        case Failure(ex) => println(s"failed to open client socket: ${ex.toString}")
      }
    }
  }
}
object Client {
  val LOCALHOST = "127.0.0.1"
}