package daylightnebula.mobagame.networking.base.server

import daylightnebula.mobagame.network.*
import daylightnebula.mobagame.network.datatypes.MatchType
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import kotlin.random.Random

class Server {

    companion object {
        val playersPerMatch = 2
        val MAX_ITEMS = 3

        lateinit var server: Server
        val serverSocket = ServerSocket(4456)
        val connections = mutableListOf<Connection>()
    }

    // match stuff
    val queues = hashMapOf<MatchType, MutableList<Connection>>()
    val activeMatches = mutableListOf<Match>()

    fun start() {
        server = this

        // create client management threads
        ClientConnector().start()

        // setup queue map
        MatchType.values().forEach {
            queues[it] = mutableListOf()
        }

        while (true) {
            // remove all matches that are closed from active
            activeMatches.removeIf { it.matchState == Match.MatchState.CLOSED }

            // loop through queues and check if a match needs to be created
            queues.forEach { entry ->
                // break down entry
                val matchType = entry.key
                val list = entry.value

                // while there is enough players to start a match, put them in a match
                while (list.size >= playersPerMatch) {
                    // create match
                    val match = Match(matchType)

                    // remove the connection from the queue list and add it to the matches' connection list
                    for (i in 0 until playersPerMatch) {
                        val conn = list.removeAt(0)
                        match.players.add(Match.MatchPlayer(conn))
                    }

                    // start the match
                    activeMatches.add(match)
                    match.start()
                }
            }
        }
    }

    fun onConnect(conn: Connection) {
        conn.sendPacket(LoginPacket(conn.userID, true))
    }

    fun processPacket(conn: Connection, packet: ServerPacket) {
        if (packet is MatchPacket) {
            val match = activeMatches.firstOrNull { it.matchID == packet.matchID } ?: return
            match.processPacket(conn, packet)
        } else if (packet is QueuePacket) {
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

                // create connection and start thread // todo find a way to group threads so we can support millions of players instead of thousands
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