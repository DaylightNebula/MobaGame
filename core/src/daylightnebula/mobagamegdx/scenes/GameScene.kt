package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import daylightnebula.mobagame.network.ServerPacket
import daylightnebula.mobagamegdx.Constants
import daylightnebula.mobagamegdx.gameobjects.AnimatedGameObject
import daylightnebula.mobagamegdx.gameobjects.AttachedGameObject
import daylightnebula.mobagamegdx.networking.peer.PeerConnection
import daylightnebula.mobagamegdx.physics.GroundPlane
import daylightnebula.mobagamegdx.physics.PhysicsWorld
import daylightnebula.mobagamegdx.players.MyPlayer
import daylightnebula.mobagamegdx.players.Player
import daylightnebula.mobagamegdx.players.PlayerControllerListener
import kotlin.random.Random

class GameScene(val address: String, val port: Int): Scene() {

    companion object {
        lateinit var game: GameScene
    }

    // rendering stuffs
    private val modelBatch = ModelBatch()
    private val spriteBatch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val environment = Environment()

    // physics stuff
    private val physicsWorld = PhysicsWorld()
    private val mainGroundPlane = GroundPlane(-50000f, 0f, -50000f, 100000f, 100000f)

    // player stuff
    private val playerControllerListener = PlayerControllerListener()
    val myPlayer = MyPlayer(Vector3((Random.nextFloat() - 0.5f) * 4f, 0f, (Random.nextFloat() - 0.5f) * 4f), 0, playerControllerListener)
    lateinit var enemyPlayer: Player
    val enemyInitialized: Boolean
        get() {
            return this::enemyPlayer.isInitialized
        }

    // temporary terrain // todo replace with proper terrain generation
    private val modelBuilder = ModelBuilder()
    lateinit var groundPlane: ModelInstance

    // network stuff
    val connection = PeerConnection(address, port)

    override fun create() {
        game = this

        // setup rendering stuffs
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f))

        // add physics ground planes
        PhysicsWorld.addGroundPlane(mainGroundPlane)
        groundPlane = ModelInstance(
            modelBuilder.createBox(
                mainGroundPlane.width, 0.1f, mainGroundPlane.depth,
                Material(ColorAttribute.createDiffuse(Color(0.2f, 0.55f, 0f, 0.5f))),
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()
            )
        )

        // setup input
        Gdx.input.isCursorCatched = true
        Gdx.input.inputProcessor = playerControllerListener

        connection.start()
    }

    override fun resize(width: Int, height: Int) {
        myPlayer.camera.viewportWidth = width.toFloat()
        myPlayer.camera.viewportHeight = height.toFloat()
    }

    override fun render() {
//        val start = System.currentTimeMillis()

        // prepare camera and input stuffs for render
        myPlayer.update()
        if (this::enemyPlayer.isInitialized) enemyPlayer.update()

        // setup render
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // start render
        modelBatch.begin(myPlayer.camera)

        // render objects
        modelBatch.render(groundPlane, environment)
        myPlayer.render(modelBatch, environment)
        if (this::enemyPlayer.isInitialized) enemyPlayer.render(modelBatch, environment)

        // end render
        modelBatch.end()

        // draw ui
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f)
        shapeRenderer.rect(
            0.24f * myPlayer.camera.viewportWidth, 0f,
            0.52f * myPlayer.camera.viewportWidth, 0.045f * myPlayer.camera.viewportHeight
        )
        shapeRenderer.setColor(1f, 0f, 0f, 1f)
        shapeRenderer.rect(
            0.25f * myPlayer.camera.viewportWidth, 0.01f * myPlayer.camera.viewportHeight,
            ((myPlayer.health / Constants.MAX_HEALTH) * 0.5f) * myPlayer.camera.viewportWidth, 0.025f * myPlayer.camera.viewportHeight
        )
        shapeRenderer.end()

//        val end = System.currentTimeMillis()
//        var diff = end - start
//        if (diff < 1L) diff = 1L
//        System.out.println("Max possible FPS: ${1f / (diff.toFloat() / 1000f)}")
    }

    override fun dispose() {
        // reset
        Gdx.input.inputProcessor = null

        // close connection to peer
        connection.close()

        // get rid of variables that need to be disposed of
        modelBatch.dispose()
    }

    override fun processServerPacket(serverPacket: ServerPacket): Boolean {
        return false
    }
}