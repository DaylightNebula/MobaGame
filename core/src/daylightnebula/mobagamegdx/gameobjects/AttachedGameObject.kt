package daylightnebula.mobagamegdx.gameobjects

import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import daylightnebula.mobagamegdx.scenes.GameScene
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class AttachedGameObject(
    val parent: AnimatedGameObject,
    modelName: String,
    val attachPoint: String,
    val offset: Vector3 = Vector3(),
    val rotationOffset: Vector3 = Vector3()
    ): GameObject(modelName) {

    /*init {
        parent.modelInstance.nodes.forEach {
            println("Node ${it.id}")
            it.children.forEach {
                printNodes(it)
            }
        }
    }

    fun printNodes(node: Node) {
        if (node.childCount > 0)
            node.children.forEach {
                println("Node ${it.id}")
                printNodes(it)
            }
    }*/

    override fun update0() {
        // get needed stuff
        val node = parent.modelInstance.getNode(attachPoint, true)
        val parentRotation = parent.modelInstance.transform.getRotation(Quaternion())
        val nodeRotation = node.globalTransform.getRotation(Quaternion())

        // calculate rotation
        val totalYaw = nodeRotation.yaw + parentRotation.yaw + rotationOffset.y
        val totalPitch = parentRotation.pitch + nodeRotation.pitch + rotationOffset.x
        val totalRoll = parentRotation.roll + nodeRotation.roll + rotationOffset.z
        val rotation = Quaternion().setEulerAngles(totalYaw, totalPitch, totalRoll)

        // calculate position
        val modOffset = Vector3(
            cos(totalPitch * (PI / 180).toFloat()) * cos(totalYaw * (PI / 180).toFloat()),
            sin(totalPitch * (PI / 180).toFloat()) * cos(totalYaw * (PI / 180).toFloat()),
               sin(totalYaw * (PI / 180).toFloat())
        )
        val position = node.globalTransform.getTranslation(Vector3())
            /*.add(modOffset)*/.rotate(Vector3.Y, parentRotation.yaw)

        // set transform
        val scale = Vector3(1f, 1f, 1f)
        modelInstance.transform.set(
            parent.modelInstance.transform.getTranslation(Vector3()).add(position),
            rotation,
            scale
        )
    }
}