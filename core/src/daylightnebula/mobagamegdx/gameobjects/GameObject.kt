package daylightnebula.mobagamegdx.gameobjects

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import daylightnebula.mobagamegdx.managers.ModelManager
import daylightnebula.mobagamegdx.physics.PhysicsWorld

open class GameObject(name: String, val isActive: Boolean = true) {
    val modelInstance = ModelInstance(ModelManager.getModel(name))

    open fun physicsStep() {}
    open fun animationStep() {}
    open fun update0() {}

    fun update() {
        physicsStep()
        animationStep()
        update0()
    }

    open fun render(modelBatch: ModelBatch, environment: Environment? = null) {
        if (environment == null)
            modelBatch.render(modelInstance)
        else
            modelBatch.render(modelInstance, environment)
    }
}