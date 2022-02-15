package daylightnebula.mobagamegdx

import com.badlogic.gdx.utils.Timer

class RunnableTimerTask(val function: () -> Unit): Timer.Task() {
    override fun run() {
        function()
    }
}