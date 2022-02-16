package daylightnebula.mobagamegdx

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import daylightnebula.mobagamegdx.managers.ModelManager
import daylightnebula.mobagamegdx.networking.server.ServerConnection
import daylightnebula.mobagamegdx.scenes.GameScene
import daylightnebula.mobagamegdx.scenes.MenuScene
import daylightnebula.mobagamegdx.scenes.Scene
import kotlin.properties.Delegates
import kotlin.random.Random

class MobaGame: ApplicationListener {

    companion object {
        lateinit var game: MobaGame

        var userID by Delegates.notNull<Long>()
        var matchID by Delegates.notNull<Long>()
    }

    lateinit var currentScene: Scene
    lateinit var connection: ServerConnection

    init {
        game = this
    }

    override fun create() {
        currentScene = MenuScene()
        currentScene.create()
        connection = ServerConnection()
        connection.start()
    }

    override fun resize(width: Int, height: Int) {
        currentScene.resize(width, height)
    }

    override fun render() {
        currentScene.render()
    }

    override fun dispose() {
        currentScene.dispose()
        ModelManager.dispose()
    }

    fun changeScene(scene: Scene) {
        currentScene.dispose()
        currentScene = scene
        currentScene.create()
    }

    override fun pause() {}
    override fun resume() {}
}