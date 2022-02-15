package daylightnebula.mobagamegdx.tests

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import daylightnebula.mobagamegdx.gameobjects.AnimatedGameObject
import daylightnebula.mobagamegdx.managers.ModelManager
import daylightnebula.mobagamegdx.physics.BoxCollider
import daylightnebula.mobagamegdx.physics.GroundPlane
import daylightnebula.mobagamegdx.physics.PhysicsWorld
import net.mgsx.gltf.data.texture.GLTFImage
import net.mgsx.gltf.data.texture.GLTFTexture

class ModelViewer: ApplicationListener {

    // rendering stuff
    lateinit var cam: PerspectiveCamera
    lateinit var modelBuilder: ModelBuilder
    lateinit var modelBatch: ModelBatch
    lateinit var environment: Environment

    // stuff to render
    lateinit var animatedGameObject: AnimatedGameObject
    lateinit var physicsRectangle: ModelInstance

    // input stuff
    lateinit var cameraInputController: CameraInputController

    val physicsWorld = PhysicsWorld()

    override fun create() {
        // create environment with lights
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f))
        //environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        // create a model batch for rendering
        modelBatch = ModelBatch()

        // create camera
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(2f, 2f, 2f)
        cam.lookAt(0f,1f,0f)
        cam.near = 1f
        cam.far = 10000f
        cam.update()

        // create game object
        val radius = 50f
        val height = 175f
        animatedGameObject = AnimatedGameObject("mobaplayer", BoxCollider(0f, 1f, 0f), true)
        animatedGameObject.playAnimationNow("idle", -1, 0.5f)

        // create physics outline
        modelBuilder = ModelBuilder()
        physicsRectangle = ModelInstance(
            modelBuilder.createBox(
                radius / 2, height, radius,
                Material(ColorAttribute.createDiffuse(Color(0f, 0f, 1f, 0.5f))),
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()
            )
        )
        physicsRectangle.transform.translate(-(radius / 2), height / 2, 0f)
        PhysicsWorld.addGroundPlane(GroundPlane(-50f, 0f, -50f, 100f, 100f))

        // setup input controller
        cameraInputController = CameraInputController(cam)
        Gdx.input.inputProcessor = cameraInputController
    }

    override fun resize(width: Int, height: Int) {
        cam.viewportWidth = width.toFloat()
        cam.viewportHeight = height.toFloat()
    }

    override fun render() {
        // setup render
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // update controllers and stuff
        cam.update()
        cameraInputController.update()
        animatedGameObject.update()

        // start render
        modelBatch.begin(cam)

        animatedGameObject.render(modelBatch, environment)
        modelBatch.render(physicsRectangle)

        // end the renders
        modelBatch.end()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {
        // get rid of everything
        modelBatch.dispose()
        ModelManager.dispose()
        physicsRectangle.model.dispose()
    }
}