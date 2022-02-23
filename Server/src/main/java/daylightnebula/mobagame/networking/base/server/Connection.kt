package daylightnebula.mobagame.networking.base.server

import daylightnebula.mobagame.network.ServerPacket
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.SocketException

class Connection(val userID: Long, val socket: Socket, val inStream: ObjectInputStream, val outStream: ObjectOutputStream): Thread() {

    var open = true

    fun sendPacket(serverPacket: ServerPacket) {
        try {
            outStream.writeObject(serverPacket)
            outStream.flush()
        } catch (ex: SocketException) {
            open = false
        }
    }

    override fun run() {
        while (socket.isConnected && open) {
            try {
                val obj = inStream.readObject()

                if (obj is ServerPacket)
                    Server.server.processPacket(this, obj)
                else
                    println("Unknown packet $obj")
            } catch (ex: SocketException) {
                open = false
            }
        }
    }

    fun readPacket(): ServerPacket? {
        val packet = inStream.readObject()
        return if (packet is ServerPacket) packet else null
    }
}