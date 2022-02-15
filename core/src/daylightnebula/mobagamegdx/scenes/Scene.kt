package daylightnebula.mobagamegdx.scenes

import daylightnebula.mobagame.network.ServerPacket

abstract class Scene {
    abstract fun create()
    abstract fun resize(width: Int, height: Int)
    abstract fun render()
    abstract fun dispose()

    abstract fun processServerPacket(serverPacket: ServerPacket): Boolean
}