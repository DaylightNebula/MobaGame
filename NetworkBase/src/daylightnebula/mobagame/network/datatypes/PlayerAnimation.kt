package daylightnebula.mobagame.network.datatypes

import java.io.Serializable

enum class PlayerAnimation(val animName: String, val playSpeed: Float, val yaw: Float): Serializable {
    IDLE("idle", 1f, 0f),
    WALK_FORWARD("walk", 1f, 0f),
    WALK_FORWARD_LEFT("walk", 1f, 30f),
    WALK_FORWARD_RIGHT("walk", 1f, -30f),
    WALK_BACKWARD("walk", -1f, 0f),
    WALK_BACKWARD_LEFT("walk", -1f, -30f),
    WALK_BACKWARD_RIGHT("walk", -1f, 30f),
    RUN_FORWARD("run", 0.8f, 0f),
    RUN_FORWARD_LEFT("run", 0.8f, 30f),
    RUN_FORWARD_RIGHT("run", 0.8f, -30f),
    RUN_BACKWARD("run", -0.8f, 0f),
    RUN_BACKWARD_LEFT("run", -0.8f, -30f),
    RUN_BACKWARD_RIGHT("run", -0.8f, 30f),
    STRAFE_LEFT("walk", 1f, -90f),
    STRAFE_RIGHT("walk", 1f, 90f),
    STRAFE_LEFT_FAST("run", 0.8f, -90f),
    STRAFE_RIGHT_FAST("run", 0.8f, 90f)
}