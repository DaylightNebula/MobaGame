package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import daylightnebula.mobagame.network.ConnectToOpponentPacket
import daylightnebula.mobagame.network.IBuyItemPacket
import daylightnebula.mobagame.network.ServerPacket
import daylightnebula.mobagame.network.TimeLeftPacket
import daylightnebula.mobagame.network.datatypes.MatchType
import daylightnebula.mobagamegdx.MobaGame

class ItemSelectionScene: Scene() {

    lateinit var stage: Stage
    lateinit var timeText: TextArea
    lateinit var timeTextStyle: TextField.TextFieldStyle
    lateinit var textButton: TextButton
    lateinit var textButtonStyle: TextButton.TextButtonStyle
    lateinit var font: BitmapFont

    var timeLeft = 45

    override fun create() {
        // in opengl context
        Gdx.app.postRunnable {
            // create ui
            stage = Stage()
            Gdx.input.inputProcessor = stage
            font = BitmapFont()
            textButtonStyle = TextButton.TextButtonStyle()
            textButtonStyle.font = font
            textButton = TextButton("Buy Knife", textButtonStyle)
            timeTextStyle = TextField.TextFieldStyle(font, Color(0.2f, 0.2f, 1f, 1f), null, null, null)
            timeText = TextArea(timeLeft.toString(), timeTextStyle)
            timeText.setPosition(100f, 100f)
            stage.addActor(textButton)
            stage.addActor(timeText)

            // button listeners
            textButton.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    MobaGame.game.connection.sendPacket(
                        IBuyItemPacket(MobaGame.matchID, MobaGame.userID, 0)
                    )
                }
            })
        }
    }

    override fun resize(width: Int, height: Int) {}

    override fun render() {
        if (!this::stage.isInitialized) return

        // setup render
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    override fun processServerPacket(serverPacket: ServerPacket): Boolean {
        if (serverPacket is ConnectToOpponentPacket) {
            Gdx.app.postRunnable {
                MobaGame.game.changeScene(GameScene(serverPacket.address, serverPacket.port))
            }
            return true
        } else if (serverPacket is TimeLeftPacket) {
            println("Update time to ${serverPacket.timeLeft}")
            timeLeft = serverPacket.timeLeft
            return true
        }
        return false
    }
}