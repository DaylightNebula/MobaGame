package daylightnebula.mobagamegdx.players

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import daylightnebula.mobagame.network.datatypes.PlayerAnimation
import daylightnebula.mobagamegdx.Constants
import daylightnebula.mobagamegdx.gameobjects.AnimatedGameObject
import daylightnebula.mobagamegdx.gameobjects.AttachedGameObject
import daylightnebula.mobagamegdx.items.Item
import daylightnebula.mobagamegdx.physics.BoxCollider

open class Player(spawnLocation: Vector3, val id: Long): AnimatedGameObject("mobaplayer", BoxCollider(1f, 2f, 1f), true) {

    // camera stuff
    val camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

    // animation and movement data
    var currentAnimation = PlayerAnimation.IDLE
    var lastAnimation = PlayerAnimation.IDLE
    var isMoving = false
    var lastIsMoving = false
    var direction = Direction.FORWARD
    var lastDirection = Direction.FORWARD
    var isRunning = false
    var lastIsRunning = false

    // camera angle stuff
    var yaw = 0f
    var lastYaw = 0f
    var pitch = 0f
    var lastPitch = 0f
    val roll = 0f

    // health
    var health = Constants.MAX_HEALTH

    // item stuff
    var itemID = -1 // -1 = no item, 0+ = items
    val attachedGameObjects = mutableListOf<AttachedGameObject>()

    init {
        // setup game object
        modelInstance.transform.translate(spawnLocation)
        playAnimationNow("idle", -1, 0f)
    }

    override fun update0() {
        //if (!GameScene.game.connection.ready) return

        // call updates for the attached game objects
        attachedGameObjects.forEach {
            it.update()
        }
    }

    // on render, do my render and then called the attached game objects render
    override fun render(modelBatch: ModelBatch, environment: Environment?) {
        super.render(modelBatch, environment)
        attachedGameObjects.forEach {
            it.render(modelBatch, environment)
        }
    }

    fun setIdleAnimation(animName: String) {
        playAnimationNow(animName, -1, 0.25f)
    }

    fun useItem() {
        if (itemID == -1) return
        Item.getItem(itemID).useItem(this, 0)
    }

    // todo levels
    fun changeItem(id: Int) {
        // return if the same item
        if (id == itemID) return

        // unapply old item if needed
        if (itemID != -1) {
            Item.getItem(itemID).unapplyItem(this, 0)
            attachedGameObjects.clear()
        }

        // update to new item
        itemID = id
        Item.getItem(itemID).applyItem(this, 0)
    }

    fun damage(damage: Float) {
        // damage myself
        health -= damage

        // check if dead
        if (health <= 0f)
            death()
    }

    // on death, send packets
    fun death() {
        println("I DIED")
    }

    enum class Direction {
        FORWARD,
        FORWARD_LEFT,
        FORWARD_RIGHT,
        BACKWARD,
        BACKWARD_LEFT,
        BACKWARD_RIGHT,
        LEFT,
        RIGHT
    }
}