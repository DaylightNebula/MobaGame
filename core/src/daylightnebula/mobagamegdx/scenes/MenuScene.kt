package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import daylightnebula.mobagame.network.LoginPacket
import daylightnebula.mobagame.network.QueuePacket
import daylightnebula.mobagame.network.QueueResponse
import daylightnebula.mobagame.network.ServerPacket
import daylightnebula.mobagame.network.datatypes.MatchType
import daylightnebula.mobagamegdx.MobaGame
import kotlin.system.exitProcess

class MenuScene: Scene() {
    override fun create() {
        Gdx.input.inputProcessor = MenuInputProcessor()
    }

    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    override fun resize(width: Int, height: Int) {}
    override fun render() {}

    override fun processServerPacket(serverPacket: ServerPacket): Boolean {
        if (serverPacket is LoginPacket) {
            MobaGame.game.userID = serverPacket.userID
            if (!serverPacket.allowed) exitProcess(-1) // TODO better allowed
            println("Login Packet")
            return true
        } else if (serverPacket is QueueResponse) {
            if (serverPacket.success) println("In queue")
            else println("Couldn't join queue")
            return true
        } else
            return false
    }

    class MenuInputProcessor: InputProcessor {
        override fun keyUp(keycode: Int): Boolean {
            println("IP Key Up")
            if (keycode == Input.Keys.A) {
                println("Sending queue packet")
                MobaGame.game.connection.sendPacket(
                    QueuePacket(MatchType.NORMALS)
                )
            }
            return true
        }

        override fun keyDown(keycode: Int): Boolean {return true}
        override fun keyTyped(character: Char): Boolean {return true}
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {return true}
        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {return true}
        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {return true}
        override fun mouseMoved(screenX: Int, screenY: Int): Boolean {return true}
        override fun scrolled(amountX: Float, amountY: Float): Boolean {return true}
    }
}