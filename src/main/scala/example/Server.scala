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

class Server(val port: Int) extends SocketOps {
  var serverSocket: ServerSocket = _

  def createListenSocket(): Unit = serverSocket = new ServerSocket(port)
  def acceptConnection(): Unit = blocking {
    socket = Option(serverSocket.accept())
  }
}
