package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import daylightnebula.mobagame.network.ISelectClassPacket
import daylightnebula.mobagame.network.ServerPacket
import daylightnebula.mobagame.network.TimeLeftPacket
import daylightnebula.mobagamegdx.MobaGame
import daylightnebula.mobagamegdx.`class`.Class

class ClassSelectionScene: Scene() {

    // ui backbone
    lateinit var stage: Stage
    lateinit var timeTextStyle: TextField.TextFieldStyle
    lateinit var textButtonStyle: TextButton.TextButtonStyle
    lateinit var font: BitmapFont

    // ui elements
    lateinit var timeText: TextField
    val buttonList = mutableListOf<TextButton>()

    override fun create() {
        Gdx.app.postRunnable {
            // init ui backbone
            stage = Stage()
            Gdx.input.inputProcessor = stage
            font = BitmapFont()
            textButtonStyle = TextButton.TextButtonStyle()
            textButtonStyle.font = font
            timeTextStyle = TextField.TextFieldStyle(font, Color(0.7f, 0.7f, 1f, 1f), null, null, null)
            timeText = TextField("45", timeTextStyle)

            // create buttons
            Class.list.forEach { c ->
                val button = TextButton(c.name, textButtonStyle)
                button.addListener {
                    MobaGame.game.connection.sendPacket(
                        ISelectClassPacket(MobaGame.matchID, MobaGame.userID, c.classID)
                    )
                    true
                }
                buttonList.add(button)
                stage.addActor(button)
            }

            // finalize ui
            stage.addActor(timeText)
            positionUI(Gdx.graphics.width, Gdx.graphics.height)
        }
    }

    fun positionUI(width: Int, height: Int) {
        if (!this::textButtonStyle.isInitialized) return
        timeText.setPosition(width / 2f, height - 20f, Align.center)

        // update buttons
        val buttonHeight = buttonList.first().height
        val startingOffset = (buttonList.size / 2) * -buttonHeight
        buttonList.forEachIndexed { index, button ->
            button.setPosition(
                width / 2f,
                (height / 2f) + startingOffset + (buttonHeight * index),
                Align.center
            )
        }
    }

    override fun resize(width: Int, height: Int) {
        positionUI(width, height)
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    override fun processServerPacket(serverPacket: ServerPacket): Boolean {
        if (serverPacket is TimeLeftPacket) {
            if (this::timeText.isInitialized) timeText.text = serverPacket.timeLeft.toString()
            return true
        } else
            return false
    }
}