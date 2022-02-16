package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import daylightnebula.mobagame.network.*
import daylightnebula.mobagame.network.datatypes.MatchType
import daylightnebula.mobagamegdx.MobaGame
import kotlin.system.exitProcess


class MenuScene: Scene() {

    lateinit var stage: Stage
    lateinit var textButton: TextButton
    lateinit var textButtonStyle: TextButton.TextButtonStyle
    lateinit var font: BitmapFont

    override fun create() {
        // create ui
        stage = Stage()
        Gdx.input.inputProcessor = stage
        font = BitmapFont()
        textButtonStyle = TextButton.TextButtonStyle()
        textButtonStyle.font = font
        textButton = TextButton("Join Queue", textButtonStyle)
        stage.addActor(textButton)

        // button listeners
        textButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                joinQueue(MatchType.NORMALS)
            }
        })
    }

    override fun render() {
        // setup render
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    override fun resize(width: Int, height: Int) {}

    override fun processServerPacket(serverPacket: ServerPacket): Boolean {
        if (serverPacket is LoginPacket) {
            MobaGame.userID = serverPacket.userID
            if (!serverPacket.allowed) exitProcess(-1) // TODO better allowed
            println("Login Packet")
            return true
        } else if (serverPacket is QueueResponse) {
            if (serverPacket.success) println("In queue")
            else println("Couldn't join queue")
            return true
        } else if (serverPacket is JoinMatchPacket) {
            println("Joined match ${serverPacket.matchID}")
            MobaGame.matchID = serverPacket.matchID
            MobaGame.game.changeScene(ItemSelectionScene())
            return true
        } else
            return false
    }

    fun joinQueue(matchType: MatchType) {
        MobaGame.game.connection.sendPacket(
            QueuePacket(matchType)
        )
    }

    /*class MenuInputProcessor: InputProcessor {
        override fun keyUp(keycode: Int): Boolean {
            if (keycode == Input.Keys.A) {
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
    }*/
}