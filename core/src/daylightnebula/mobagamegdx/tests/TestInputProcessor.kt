package daylightnebula.mobagamegdx.tests

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import daylightnebula.mobagamegdx.gameobjects.AnimatedGameObject

class TestInputProcessor(val animatedGameObject: AnimatedGameObject): InputProcessor {

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.SPACE)
            animatedGameObject.playAnimationNow("idle", -1, 0.5f)
        else if (keycode == Input.Keys.W)
            animatedGameObject.playAnimationNow("walking", -1, 0.5f)

        return true
    }

    override fun keyDown(keycode: Int): Boolean { return true }
    override fun keyTyped(character: Char): Boolean { return true }
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return true }
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return true }
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean { return true }
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean { return true }
    override fun scrolled(amountX: Float, amountY: Float): Boolean { return true }
}