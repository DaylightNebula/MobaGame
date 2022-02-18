package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.steer.behaviors.Alignment
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import daylightnebula.mobagame.network.*
import daylightnebula.mobagame.network.datatypes.MatchType
import daylightnebula.mobagamegdx.MobaGame
import kotlin.system.exitProcess


class MenuScene: Scene() {

    // ui stuff
    lateinit var stage: Stage
    lateinit var textButtonStyle: TextButton.TextButtonStyle
    lateinit var font: BitmapFont

    // buttons
    lateinit var joinQueueButton: TextButton
    lateinit var quitButton: TextButton

    override fun create() {
        // create ui backbone
        stage = Stage()
        Gdx.input.inputProcessor = stage
        font = BitmapFont()
        textButtonStyle = TextButton.TextButtonStyle()
        textButtonStyle.font = font

        // create buttons
        joinQueueButton = TextButton("Join Queue", textButtonStyle)
        quitButton = TextButton("Quit", textButtonStyle)

        // prepare ui stuff
        stage.addActor(joinQueueButton)
        stage.addActor(quitButton)
        positionButtons(Gdx.graphics.width, Gdx.graphics.height)

        // button listeners
        joinQueueButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                joinQueue(MatchType.NORMALS)
            }
        })
        quitButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                exitProcess(0)
            }
        })
    }

    override fun render() {
        // setup render
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    override fun resize(width: Int, height: Int) {
        positionButtons(width, height)
    }

    fun positionButtons(width: Int, height: Int) {
        if (!this::joinQueueButton.isInitialized) return
        joinQueueButton.setPosition(width / 2f, height / 2f, Align.center)
        quitButton.setPosition(width / 2f, height / 2f - joinQueueButton.height, Align.center)
    }

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
            MobaGame.game.changeScene(ClassSelectionScene())
            return true
        } else
            return false
    }

    fun joinQueue(matchType: MatchType) {
        MobaGame.game.connection.sendPacket(
            QueuePacket(matchType)
        )
    }
}