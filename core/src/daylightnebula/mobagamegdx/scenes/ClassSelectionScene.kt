package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import daylightnebula.mobagame.network.ServerPacket

class ClassSelectionScene: Scene() {

    // ui backbone
    lateinit var stage: Stage
    lateinit var timeTextStyle: TextField.TextFieldStyle
    lateinit var textButtonStyle: TextButton.TextButtonStyle
    lateinit var font: BitmapFont

    // ui elements

    override fun create() {
        // init ui backbone
        stage = Stage()
        Gdx.input.inputProcessor = stage
        font = BitmapFont()
        textButtonStyle = TextButton.TextButtonStyle()
        textButtonStyle.font = font
        timeTextStyle = TextField.TextFieldStyle(font, Color(0.2f, 0.2f, 1f, 1f), null, null, null)

        // create buttons

        // finalize ui
        positionUI(Gdx.graphics.width, Gdx.graphics.height)
    }

    fun positionUI(width: Int, height: Int) {
//        if (!this::joinQueueButton.isInitialized) return
//        joinQueueButton.setPosition(width / 2f, height / 2f, Align.center)
//        quitButton.setPosition(width / 2f, height / 2f - joinQueueButton.height, Align.center)
    }

    override fun resize(width: Int, height: Int) {
        positionUI(width, height)
    }
    override fun render() {
        stage.draw()
    }
    override fun dispose() {
        Gdx.input.inputProcessor = null
    }
    override fun processServerPacket(serverPacket: ServerPacket): Boolean {
        return false
    }
}