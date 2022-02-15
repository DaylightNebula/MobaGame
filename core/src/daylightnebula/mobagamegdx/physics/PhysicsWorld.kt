package daylightnebula.mobagamegdx.physics

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3

class PhysicsWorld {
    companion object {
        private val groundPlanes = mutableListOf<GroundPlane>()
        private val physicsObjects = mutableListOf<PhysicsGameObject>()

        fun getGroundHeightsAtXZ(x: Float, z: Float): List<GroundPlane> {
            return groundPlanes.filter { x >= it.x && x <= it.x + it.width && z >= it.z && z <= it.z + it.depth }
        }

        fun getGroundHeightAtLocation(x: Float, y: Float, z: Float): Float? {
            return getGroundHeightsAtXZ(x, z).firstOrNull { it.y <= y }?.y
        }

        fun getLowestGroundHeightAtXZ(x: Float, z: Float): Float? {
            return getGroundHeightsAtXZ(x, z).minBy { it.y }?.y
        }

        fun getHighestGroundHeightAtXZ(x: Float, z: Float): Float? {
            return getGroundHeightsAtXZ(x, z).maxBy { it.y }?.y
        }

        fun checkCollision(srcObject: PhysicsGameObject): PhysicsGameObject? {
            val srcPos = srcObject.modelInstance.transform.getTranslation(Vector3())
            val srcBox = srcObject.collider

            return physicsObjects.firstOrNull {
                val itPos = it.modelInstance.transform.getTranslation(Vector3())
                val itBox = it.collider

                srcBox.intersects(srcPos, itPos, itBox)
            }
        }

        fun checkCollisionWithSphere(pos: Vector3, radius: Float): PhysicsGameObject? {
            return physicsObjects.firstOrNull {
                it.collider.intersects(it.modelInstance.transform.getTranslation(Vector3()), pos, radius)
            }
        }

        fun addGroundPlane(groundPlane: GroundPlane) {
            groundPlanes.add(groundPlane)
        }

        fun addPhysicsObject(physicsGameObject: PhysicsGameObject) {
            physicsObjects.add(physicsGameObject)
        }

        fun removePhysicsObject(physicsGameObject: PhysicsGameObject) {
            physicsObjects.remove(physicsGameObject)
        }
    }
}