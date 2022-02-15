package daylightnebula.mobagamegdx.physics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import daylightnebula.mobagamegdx.gameobjects.GameObject

open class PhysicsGameObject(modelName: String, val collider: BoxCollider, val hasGravity: Boolean): GameObject(modelName) {

    var gravityAcceleration = -980f
    var gravityVelocity = 0f

    open fun move(x: Float, y: Float, z: Float) {
        modelInstance.transform.translate(x, y, z)
        val collision = PhysicsWorld.checkCollision(this)
        if (collision != null) {
            val myPos = modelInstance.transform.getTranslation(Vector3())
            val cPos = collision.modelInstance.transform.getTranslation(Vector3())
            //val moveBackX = cPos.x - myPos.x + if (x < 0f) collision.collider.width else if (y > 0f) collider.width else 0f
            val moveBackY = cPos.y - myPos.y + if (y < 0f) collision.collider.height else if (y > 0f) collider.height else 0f
            //val moveBackZ = cPos.z - myPos.z + if (z < 0f) collision.collider.depth else if (y > 0f) collider.depth else 0f
            modelInstance.transform.translate(-x, moveBackY, -z)
        }
    }

    override fun physicsStep() {
        // should gravity be applied?
        if (hasGravity) {
            // get position and height
            val pos = modelInstance.transform.getTranslation(Vector3())
            val height = PhysicsWorld.getGroundHeightAtLocation(pos.x, pos.y, pos.z) ?: PhysicsWorld.getLowestGroundHeightAtXZ(pos.x, pos.z) ?: return

            // get on ground?
            val onGround = pos.y <= height

            // apply gravity velocity if not on ground
            if (!onGround) {
                // update the gravity velocity and get a move amount
                gravityVelocity += gravityAcceleration * Gdx.graphics.deltaTime
            } else if (gravityVelocity < 0f) {
                // if at or below the height, move to the height and reset the gravity velocity
                modelInstance.transform.translate(0f, height - pos.y, 0f)
                gravityVelocity = 0f
            }

            // get move amount
            val moveAmount = gravityVelocity * Gdx.graphics.deltaTime

            // if room to move the full amount, move the full amount, else move to the given height
            if (pos.y + moveAmount < height)
                modelInstance.transform.translate(0f, height - pos.y, 0f)
            else
                modelInstance.transform.translate(0f, moveAmount, 0f)
        }
    }
}