package daylightnebula.mobagamegdx.networking.server

import daylightnebula.mobagame.network.ServerPacket
import daylightnebula.mobagamegdx.MobaGame
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import kotlin.random.Random

class ServerConnection: Thread() {

    // socket stuff
    private val serverAddress = "localhost"
    private val port = 4456
    val socket = Socket(serverAddress, port)
    val inStream = ObjectInputStream(socket.getInputStream())
    val outStream = ObjectOutputStream(socket.getOutputStream())

    override fun run() {
        while (true) {
            val obj = inStream.readObject()

            if (obj is ServerPacket)
                processPacket(obj)
            else
                println("Illegal packet $obj")
        }
    }

    // sends data to the server
    fun sendPacket(packet: ServerPacket) {
        outStream.writeObject(packet)
        outStream.flush()
    }

    // process' an incoming packet
    private fun processPacket(serverPacket: ServerPacket) {
        if (!MobaGame.game.currentScene.processServerPacket(serverPacket))
            println("Unprocessed packet ${serverPacket.name}")
    }
}