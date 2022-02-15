package daylightnebula.mobagamegdx.networking.peer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import daylightnebula.mobagame.network.*
import daylightnebula.mobagamegdx.Constants
import daylightnebula.mobagamegdx.MobaGame
import daylightnebula.mobagamegdx.physics.PhysicsWorld
import daylightnebula.mobagamegdx.players.Player
import daylightnebula.mobagamegdx.scenes.GameScene
import java.io.*
import java.net.*
import kotlin.random.Random

class PeerConnection(val address: String, val port: Int): Thread() {
    // todo get connection from server

    // sockets
    private var serverSocket: ServerSocket? = null
    lateinit var socket: Socket

    // I/O stuffs
    lateinit var inStream: ObjectInputStream
    lateinit var outStream: ObjectOutputStream
    var ready = false

    override fun run() {
        // get connection
        try {
            socket = Socket("localhost", port)
        } catch (ex: ConnectException) {
            serverSocket = ServerSocket(port)
            socket = serverSocket!!.accept()
        }

        // get input and output streams
        outStream = ObjectOutputStream(socket.getOutputStream())
        inStream = ObjectInputStream(socket.getInputStream())

        // send test packet if client
        if (isClient()) {
            val position = GameScene.game.myPlayer.modelInstance.transform.getTranslation(Vector3())
            sendPacket(PeerInitPlayerPacket(MobaGame.game.userID, position.x, position.y, position.z))
        }
        ready = true

        // start loop
        while (true) {
            try {
                val obj = inStream.readObject()

                if (obj is PeerPacket) {
                    recv(obj)
                } else {
                    System.err.println("Bad packet received $obj")
                }
            } catch (ex: Exception) { ex.printStackTrace() }
        }
    }

    fun sendPacket(any: PeerPacket) {
        if (!this::outStream.isInitialized) {
            //println("Packet sent to early, packet: $any")
            return
        }
        outStream.writeObject(any)
        outStream.flush()
    }

    fun isServer(): Boolean {
        return serverSocket != null
    }

    fun isClient(): Boolean {
        return serverSocket == null
    }

    fun close() {
        socket.close()
        serverSocket?.close()
    }

    fun recv(packet: PeerPacket) {
        if (packet is PeerInitPlayerPacket) {
            GameScene.game.enemyPlayer = Player(Vector3(packet.xPos, packet.yPos, packet.zPos), packet.userID)
            PhysicsWorld.addPhysicsObject(GameScene.game.enemyPlayer)

            if (isServer()) {
                val position = GameScene.game.myPlayer.modelInstance.transform.getTranslation(Vector3())
                sendPacket(PeerInitPlayerPacket(MobaGame.game.userID, position.x, position.y, position.z))
            }
        } else if (packet is PeerMovePlayerPacket) {
            if (!GameScene.game.enemyInitialized) return
            GameScene.game.enemyPlayer.modelInstance.transform.setTranslation(packet.x, packet.y, packet.z)
        } else if (packet is PeerPlayerAnimationChangePacket) {
            Gdx.app.postRunnable {
                // set play speed
                GameScene.game.enemyPlayer.playSpeed = packet.animation.playSpeed

                // play animation
                GameScene.game.enemyPlayer.playAnimationNow(
                    packet.animation.animName, -1, 0.25f
                )

                // update rotation
                GameScene.game.enemyPlayer.modelInstance.transform.rotate(Vector3.Y, -packet.lastAnimation.yaw)
                GameScene.game.enemyPlayer.modelInstance.transform.rotate(Vector3.Y, packet.animation.yaw)
            }
        } else if (packet is PeerPlayerJumpPacket) {
            if (!GameScene.game.enemyInitialized) return
            GameScene.game.enemyPlayer.gravityVelocity = Constants.JUMP_VELOCITY
        } else if (packet is PeerRotatePlayerPacket) {
            if (!GameScene.game.enemyInitialized) return
            val currentPos = GameScene.game.enemyPlayer.modelInstance.transform.getTranslation(Vector3())
            val matrix = Matrix4(currentPos, Quaternion(packet.x, packet.y, packet.z, packet.w), Vector3(1f, 1f, 1f))
            GameScene.game.enemyPlayer.modelInstance.transform.set(matrix)
        } else if (packet is PeerSelectItemPacket) {
            GameScene.game.enemyPlayer.changeItem(packet.itemID)
        } else if (packet is PeerUseItemPacket) {
            GameScene.game.enemyPlayer.useItem()
        } else if (packet is PeerDamagePacket) {
            GameScene.game.myPlayer.damage(packet.damage)
            println("MY HEALTH ${GameScene.game.myPlayer.health}/${Constants.MAX_HEALTH}")
        } else {
            println("Unknown packet $packet")
        }
    }
}