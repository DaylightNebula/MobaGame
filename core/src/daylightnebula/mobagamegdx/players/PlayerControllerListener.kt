package daylightnebula.mobagamegdx.players

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor

class PlayerControllerListener: InputProcessor {

    val keyListener = mutableListOf<(keycode: Int) -> Unit>()
    val keys = BooleanArray(256)

    val clickListeners = mutableListOf<(buttoncode: Int) -> Unit>()

    override fun keyDown(keycode: Int): Boolean {
        keys[keycode] = true
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        keys[keycode] = false
        keyListener.forEach {
            it(keycode)
        }
        return true
    }

    var timedMouseX = 0f
    var timedMouseY = 0f
    private var lastX = 0
    private var lastY = 0
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        timedMouseX += (lastX - screenX).toFloat() / Gdx.graphics.width.toFloat()
        timedMouseY += (lastY - screenY).toFloat() / Gdx.graphics.height.toFloat()

        lastX = screenX
        lastY = screenY
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        clickListeners.forEach { it(button) }
        return true
    }

    override fun keyTyped(character: Char): Boolean { return true }
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return true }
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean { return true }
    override fun scrolled(amountX: Float, amountY: Float): Boolean { return true }
}