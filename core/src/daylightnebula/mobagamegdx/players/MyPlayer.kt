package daylightnebula.mobagamegdx.players

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import daylightnebula.mobagame.network.*
import daylightnebula.mobagame.network.datatypes.PlayerAnimation
import daylightnebula.mobagamegdx.Constants
import daylightnebula.mobagamegdx.scenes.GameScene
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MyPlayer(spawnLocation: Vector3, id: Long, private val pcl: PlayerControllerListener): Player(spawnLocation, id) {
    init {
        // setup camera
        camera.position.set(0f, 2f, -2f)
        camera.lookAt(0f,1.6f,0f)
        camera.near = 1f
        camera.far = 100000f
        camera.update()

        // key listener for items
        pcl.keyListener.add { keycode ->
            if (keycode == Input.Keys.NUM_1) {
                changeItem(0)
                GameScene.game.connection.sendPacket(
                    PeerSelectItemPacket(0)
                )
            }
        }
        pcl.clickListeners.add { buttoncode ->
            if (buttoncode == 0) {
                useItem()
                GameScene.game.connection.sendPacket(
                    PeerUseItemPacket()
                )
            }
        }
    }

    override fun move(x: Float, y: Float, z: Float) {
        super.move(x, y, z)
        val pos = modelInstance.transform.getTranslation(Vector3())
        GameScene.game.connection.sendPacket(PeerMovePlayerPacket(pos.x, pos.y, pos.z))
    }

    fun moveForward(a: Float) { move(0f, 0f, a) }
    fun moveLeft(a: Float) { move(a, 0f, 0f) }

    override fun update0() {
        super.update0()
        direction = Direction.FORWARD
        isMoving = false

        // arrow key rotation
        if (pcl.keys[Input.Keys.UP])
            pcl.timedMouseY -= Constants.ARROW_KEY_ROTATION_SPEED * Gdx.graphics.deltaTime
        else if (pcl.keys[Input.Keys.DOWN])
            pcl.timedMouseY += Constants.ARROW_KEY_ROTATION_SPEED * Gdx.graphics.deltaTime
        if (pcl.keys[Input.Keys.LEFT])
            pcl.timedMouseX -= Constants.ARROW_KEY_ROTATION_SPEED * Gdx.graphics.deltaTime
        else if (pcl.keys[Input.Keys.RIGHT])
            pcl.timedMouseX += Constants.ARROW_KEY_ROTATION_SPEED * Gdx.graphics.deltaTime

        // update yaw
        yaw = pcl.timedMouseX
        val deltaYaw = yaw - lastYaw
        lastYaw = yaw

        // update pitch
        pcl.timedMouseY = MathUtils.clamp(pcl.timedMouseY, -1f, 1f)
        pitch = -pcl.timedMouseY
        val deltaPitch = pitch - lastPitch
        lastPitch = pitch

        // rotate player according to delta yaw
        if (deltaYaw != 0f) {
            modelInstance.transform.rotate(Vector3.Y, deltaYaw * (180 / PI).toFloat())
            val quat = modelInstance.transform.getRotation(Quaternion())
            GameScene.game.connection.sendPacket(PeerRotatePlayerPacket(quat.x, quat.y, quat.z, quat.w))
        }

        // should run?
        isRunning = pcl.keys[Input.Keys.SHIFT_LEFT]

        // move left/right keys
        if (pcl.keys[Input.Keys.A]) {
            moveLeft(if (isRunning) Constants.STRAFE_SPEED_FAST * Gdx.graphics.deltaTime else Constants.STRAFE_SPEED * Gdx.graphics.deltaTime)
            isMoving = true
            direction = Direction.LEFT
        }
        else if (pcl.keys[Input.Keys.D]) {
            moveLeft(if (isRunning) -Constants.STRAFE_SPEED_FAST * Gdx.graphics.deltaTime else -Constants.STRAFE_SPEED * Gdx.graphics.deltaTime)
            isMoving = true
            direction = Direction.RIGHT
        }

        // move forward/backward keys
        if (pcl.keys[Input.Keys.W]) {
            moveForward(if (isRunning) Constants.RUN_SPEED * Gdx.graphics.deltaTime else Constants.MOVE_SPEED * Gdx.graphics.deltaTime)
            isMoving = true
            direction = if (direction == Direction.LEFT) Direction.FORWARD_LEFT else if (direction == Direction.RIGHT) Direction.FORWARD_RIGHT else Direction.FORWARD
        }
        else if (pcl.keys[Input.Keys.S]) {
            moveForward(if (isRunning) -Constants.RUN_SPEED * Gdx.graphics.deltaTime else -Constants.MOVE_SPEED * Gdx.graphics.deltaTime)
            isMoving = true
            direction = if (direction == Direction.LEFT) Direction.BACKWARD_LEFT else if (direction == Direction.RIGHT) Direction.BACKWARD_RIGHT else Direction.BACKWARD
        }

        // when space is pressed, preform jump stuff
        if (gravityVelocity == 0f && pcl.keys[Input.Keys.SPACE]) {
            gravityVelocity = Constants.JUMP_VELOCITY
            GameScene.game.connection.sendPacket(PeerPlayerJumpPacket())
        }

        // assign animations
        if (isMoving != lastIsMoving) {
            if (!isMoving)
                currentAnimation = PlayerAnimation.IDLE
            else {
                if (direction == Direction.FORWARD)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_FORWARD else PlayerAnimation.WALK_FORWARD
                else if (direction == Direction.BACKWARD)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_BACKWARD else PlayerAnimation.WALK_BACKWARD
                else if (direction == Direction.LEFT)
                    currentAnimation = if (isRunning) PlayerAnimation.STRAFE_LEFT_FAST else PlayerAnimation.STRAFE_LEFT
                else if (direction == Direction.RIGHT)
                    currentAnimation = if (isRunning) PlayerAnimation.STRAFE_RIGHT_FAST else PlayerAnimation.STRAFE_RIGHT
                else if (direction == Direction.FORWARD_LEFT)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_FORWARD_LEFT else PlayerAnimation.WALK_FORWARD_LEFT
                else if (direction == Direction.FORWARD_RIGHT)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_FORWARD_RIGHT else PlayerAnimation.WALK_FORWARD_RIGHT
                else if (direction == Direction.BACKWARD_LEFT)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_BACKWARD_LEFT else PlayerAnimation.WALK_BACKWARD_LEFT
                else
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_BACKWARD_RIGHT else PlayerAnimation.WALK_BACKWARD_RIGHT
            }
        } else {
            if (isRunning != lastIsRunning || lastDirection != direction) {
                if (direction == Direction.FORWARD)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_FORWARD else PlayerAnimation.WALK_FORWARD
                else if (direction == Direction.BACKWARD)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_BACKWARD else PlayerAnimation.WALK_BACKWARD
                else if (direction == Direction.LEFT)
                    currentAnimation = if (isRunning) PlayerAnimation.STRAFE_LEFT_FAST else PlayerAnimation.STRAFE_LEFT
                else if (direction == Direction.RIGHT)
                    currentAnimation = if (isRunning) PlayerAnimation.STRAFE_RIGHT_FAST else PlayerAnimation.STRAFE_RIGHT
                else if (direction == Direction.FORWARD_LEFT)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_FORWARD_LEFT else PlayerAnimation.WALK_FORWARD_LEFT
                else if (direction == Direction.FORWARD_RIGHT)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_FORWARD_RIGHT else PlayerAnimation.WALK_FORWARD_RIGHT
                else if (direction == Direction.BACKWARD_LEFT)
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_BACKWARD_LEFT else PlayerAnimation.WALK_BACKWARD_LEFT
                else
                    currentAnimation = if (isRunning) PlayerAnimation.RUN_BACKWARD_RIGHT else PlayerAnimation.WALK_BACKWARD_RIGHT
            }
        }

        // update last variables for state change tracking
        lastIsMoving = isMoving
        lastDirection = direction
        lastIsRunning = isRunning

        // update camera and its position
        val pos = modelInstance.transform.getTranslation(Vector3())
        camera.position.set(pos.x + ((sin(yaw) * cos(pitch)) * -2f), pos.y + 2f + (sin(pitch) * 2f), pos.z + ((cos(yaw) * cos(pitch)) * -2f))
        camera.rotate(Vector3.Y, deltaYaw * (180 / PI).toFloat())
        camera.lookAt(pos.x, pos.y + 2f, pos.z)
        camera.update()

        // check if animation state changed, if it did, update the animation and send a packet
        if (lastAnimation != currentAnimation) {
            // set player speed
            playSpeed = currentAnimation.playSpeed

            // update animation
            playAnimationNow(
                currentAnimation.animName, -1, 0.25f
            )

            // set rotation // todo fix interaction with attach game objects
            modelInstance.transform.rotate(Vector3.Y, -lastAnimation.yaw)
            modelInstance.transform.rotate(Vector3.Y, currentAnimation.yaw)

            // send packet
            GameScene.game.connection.sendPacket(PeerPlayerAnimationChangePacket(lastAnimation, currentAnimation))

            // update last animation
            lastAnimation = currentAnimation
        }
    }
}