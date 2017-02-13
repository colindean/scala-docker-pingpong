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

import java.net.{ServerSocket, Socket}

import scala.concurrent.{Future, blocking}
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

class Server(val port: Int) extends SocketOps {
  type SocketHandler = Socket => Unit

  def forEachConnection(f: SocketHandler): Unit = {
    var serverSocket: ServerSocket = createListenSocket.getOrElse(throw new RuntimeException(s"unable to create socket on port $port"))
    var keepRunning = true
    while(keepRunning) {
      val connection = acceptConnectionFromServer(serverSocket)
      connection.foreach(withOpenConnection(_)(f))
      connection.failed.foreach { ex =>
        keepRunning = false
        Console.err.println(s"Failed to accept a connection, exiting ($ex)")
      }
    }
  }

  private def createListenSocket: Try[ServerSocket] = Try(new ServerSocket(port))

  private def acceptConnectionFromServer(serverSocket: ServerSocket): Try[Socket] = blocking {
    Try(serverSocket.accept())
  }

  private def withOpenConnection(socket: Socket)(f: SocketHandler): Future[_] = {
    Future {
      f(socket)
    }
  }
}
