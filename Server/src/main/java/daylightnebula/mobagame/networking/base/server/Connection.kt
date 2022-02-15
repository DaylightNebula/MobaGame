package daylightnebula.mobagame.networking.base.server

import daylightnebula.mobagame.network.ServerPacket
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class Connection(val userID: Long, val socket: Socket, val inStream: ObjectInputStream, val outStream: ObjectOutputStream): Thread() {
    fun sendPacket(serverPacket: ServerPacket) {
        outStream.writeObject(serverPacket)
        outStream.flush()
    }

    override fun run() {
        while (true) {
            val obj = inStream.readObject()

            if (obj is ServerPacket)
                Server.server.processPacket(this, obj)
            else
                println("Unknown packet $obj")
        }
    }

    fun readPacket(): ServerPacket? {
        val packet = inStream.readObject()
        return if (packet is ServerPacket) packet else null
    }
}