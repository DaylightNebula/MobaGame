package daylightnebula.mobagame.networking.base.server

import daylightnebula.mobagame.network.LoginPacket
import daylightnebula.mobagame.network.QueuePacket
import daylightnebula.mobagame.network.QueueResponse
import daylightnebula.mobagame.network.ServerPacket
import daylightnebula.mobagame.network.datatypes.MatchType
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ConnectException
import java.net.ServerSocket
import kotlin.random.Random
import kotlin.system.exitProcess

class Server {

    companion object {
        lateinit var server: Server
        val serverSocket = ServerSocket(4456)
        val connections = mutableListOf<Connection>()
    }

    val queues = hashMapOf<MatchType, MutableList<Connection>>()

    fun start() {
        server = this

        // create client management threads
        ClientConnector().start()

        // setup queue map
        MatchType.values().forEach {
            queues[it] = mutableListOf()
        }

        while (true) {}
    }

    fun onConnect(conn: Connection) {
        conn.sendPacket(LoginPacket(conn.userID, true))
    }

    fun processPacket(conn: Connection, packet: ServerPacket) {
        if (packet is QueuePacket) {
            // when asked to join a queue, check if they can join that queue.  If they can, add them.
            if (packet.matchType.active) {
                queues[packet.matchType]?.add(conn)
                conn.sendPacket(QueueResponse(true))
            } else
                conn.sendPacket(QueueResponse(false))
        }
    }

    class ClientConnector: Thread() {
        override fun run() {
            while (true) {
                // create socket and I/O stream data
                val socket = serverSocket.accept()
                val outStream = ObjectOutputStream(socket.getOutputStream())
                val inStream = ObjectInputStream(socket.getInputStream())

                // create  and start connection
                val conn = Connection(
                    Random.nextLong(),
                    socket,
                    inStream, outStream
                )
                conn.start()

                // on connected events
                connections.add(conn)
                server.onConnect(conn)
                println("Connected to ${conn.socket.localAddress.hostAddress}")
            }
        }
    }
}