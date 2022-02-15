package daylightnebula.mobagamegdx.gameobjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import daylightnebula.mobagamegdx.physics.BoxCollider
import daylightnebula.mobagamegdx.physics.PhysicsGameObject

open class AnimatedGameObject(name: String, collider: BoxCollider, hasGravity: Boolean): PhysicsGameObject(name, collider, hasGravity) {

    private val readyAnimations = mutableListOf("mixamo.com")
    val animationController = AnimationController(modelInstance)

    var playSpeed = 1f

    override fun animationStep() {
        animationController.update(Gdx.graphics.deltaTime)
    }

    fun playAnimationNow(animName: String, loop: Int, transitionTime: Float) {
        // play animation
        animationController.animate(animName, loop, playSpeed, BlankAnimationListener(), transitionTime)
    }

    fun playAnimationOnce(animName: String, transitionTime: Float) {
        val currentAnim = animationController.current
        animationController.animate(animName, 1, playSpeed, BlankAnimationListener(), transitionTime)
        if (currentAnim.animation.id != animName) animationController.queue(currentAnim.animation.id, -1, playSpeed, BlankAnimationListener(), transitionTime)
    }

    class BlankAnimationListener(): AnimationController.AnimationListener {
        override fun onEnd(animation: AnimationController.AnimationDesc?) {}
        override fun onLoop(animation: AnimationController.AnimationDesc?) {}
    }
}