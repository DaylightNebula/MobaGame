package daylightnebula.mobagamegdx.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Timer
import daylightnebula.mobagamegdx.RunnableTimerTask
import daylightnebula.mobagamegdx.gameobjects.AttachedGameObject
import daylightnebula.mobagame.network.PeerDamagePacket
import daylightnebula.mobagamegdx.physics.PhysicsWorld
import daylightnebula.mobagamegdx.players.MyPlayer
import daylightnebula.mobagamegdx.players.Player
import daylightnebula.mobagamegdx.scenes.GameScene

class KnifeItem(id: Int): Item(id) {
    override fun applyItem(player: Player, level: Int) {
        Gdx.app.postRunnable {
            player.attachedGameObjects.add(
                AttachedGameObject(
                    player,
                    "knife",
                    "left_hand",
                    offset = Vector3(0f, -0.25f, 0f),
                    rotationOffset = Vector3(0f, 0f , -90f)
                )
            )
        }
    }

    override fun unapplyItem(player: Player, level: Int) {
        println("Unapplied Knife")
    }

    override fun useItem(player: Player, level: Int) {
        // play animation
        Gdx.app.postRunnable {
            player.playAnimationOnce("use_knife", 0.25f)
        }

        // if is from me, deal damage after 3/4 of a second
        Timer.schedule(RunnableTimerTask {
            if (player is MyPlayer) {
                val checkCollision = PhysicsWorld.checkCollisionWithSphere(
                    GameScene.game.myPlayer.modelInstance.transform.getTranslation(Vector3()).cpy().add(0f, 100f, 0f),
                    200f
                )
                if (checkCollision != null) {
                    GameScene.game.connection.sendPacket(
                        PeerDamagePacket(10.0f)
                    )
                }
            }
        }, 0.5f)
    }

    override fun getCooldown(player: Player, level: Int): Double {
        println("Knife item cooldown called")
        return 1.0
    }
}