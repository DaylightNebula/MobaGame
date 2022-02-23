package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import daylightnebula.mobagame.network.*
import daylightnebula.mobagamegdx.MobaGame
import daylightnebula.mobagame.network.playerstuffs.GameClass

class ClassSelectionScene: Scene() {

    // ui backbone
    lateinit var stage: Stage
    lateinit var font: BitmapFont

    // styles
    lateinit var timeTextStyle: TextField.TextFieldStyle
    lateinit var notSelectedStyle: TextButton.TextButtonStyle
    lateinit var selectedStyle: TextButton.TextButtonStyle
    lateinit var readyStyle: TextButton.TextButtonStyle

    // ui elements
    lateinit var timeText: TextField
    val buttonList = mutableListOf<TextButton>()
    lateinit var readyButton: TextButton

    // logic vars
    //var selectedID = -1
    var isReady = false

    override fun create() {
        Gdx.app.postRunnable {
            MobaGame.currentClass = -1

            // init ui backbone
            stage = Stage()
            Gdx.input.inputProcessor = stage
            font = BitmapFont()

            // create styles
            notSelectedStyle = TextButton.TextButtonStyle()
            notSelectedStyle.font = font
            notSelectedStyle.fontColor = Color(0.75f, 0.75f, 0.75f, 1f)
            selectedStyle = TextButton.TextButtonStyle()
            selectedStyle.font = font
            selectedStyle.fontColor = Color(0.7f, 1f, 0.7f, 1f)
            readyStyle = TextButton.TextButtonStyle()
            readyStyle.font = font
            readyStyle.fontColor = Color(1f, 0.7f, 0.7f, 1f)
            timeTextStyle = TextField.TextFieldStyle(font, Color(1f, 1f, 1f, 1f), null, null, null)
            timeText = TextField("45", timeTextStyle)

            // create buttons
            GameClass.list.forEach { c ->
                val button = TextButton(c.name, notSelectedStyle)
                button.addListener {
                    if (it !is ChangeListener.ChangeEvent) return@addListener true
                    // reset last class if another was selected before
                    if (MobaGame.currentClass != -1)
                        buttonList[MobaGame.currentClass].style = notSelectedStyle

                    // set style
                    MobaGame.currentClass = c.classID
                    button.style = selectedStyle

                    // send packet
                    MobaGame.game.connection.sendPacket(
                        ISelectClassPacket(MobaGame.matchID, MobaGame.userID, c.classID)
                    )

                    true
                }
                buttonList.add(button)
                stage.addActor(button)
            }

            // create ready button
            readyButton = TextButton("Ready", notSelectedStyle)
            readyButton.addListener {
                if (it !is ChangeListener.ChangeEvent) return@addListener true
                if (!isReady && MobaGame.currentClass != -1) {
                    readyButton.style = readyStyle
                    MobaGame.game.connection.sendPacket(
                        IReadyPacket(MobaGame.matchID, MobaGame.userID)
                    )
                    isReady = true
                }
                true
            }
            stage.addActor(readyButton)

            // finalize ui
            stage.addActor(timeText)
            positionUI(Gdx.graphics.width, Gdx.graphics.height)
        }
    }

    private fun positionUI(width: Int, height: Int) {
        if (!this::selectedStyle.isInitialized) return
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
        readyButton.setPosition(width / 2f, 20f, Align.center)
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
        return when (serverPacket) {
            is TimeLeftPacket -> {
                if (this::timeText.isInitialized) timeText.text = serverPacket.timeLeft.toString()
                true
            }
            is ProceedToItemSelectPacket -> {
                MobaGame.game.changeScene(ItemSelectionScene())
                true
            }
            else -> false
        }
    }
}