package daylightnebula.mobagame.network

import daylightnebula.mobagame.network.datatypes.MatchType
import java.io.Serializable

open class ServerPacket(val name: String): Serializable { val timeSent = System.currentTimeMillis() }
class LoginPacket(val userID: Long, val allowed: Boolean): ServerPacket("LoginPacket")

// queue packets
class QueuePacket(val matchType: MatchType): ServerPacket("QueuePacket")
class QueueResponse(val success: Boolean): ServerPacket("QueueResponse")

// match packets
class JoinMatchPacket(val matchID: Long, val users: Array<Long>): ServerPacket("JoinMatchPacket")
class LeaveMatchPacket(val matchID: Long): ServerPacket("LeaveMatchPacket")
class IBuyItemPacket(val userID: Long, val itemID: Int): ServerPacket("IBuyItemPacket")
class IUpgradeItemPacket(val userID: Long, val itemID: Int): ServerPacket("IUpgradeItemPacket")
class IReadyPacket(val userID: Long): ServerPacket("IReadyPacket")
class TimeLeftPacket(val timeLeft: Int): ServerPacket("TimeLeftPacket")

// round packets
class ConnectToOpponentPacket(val userID: Long, val address: String, val port: Int): ServerPacket("ConnectToOpponentPacket")
class StartRoundPacket: ServerPacket("StartRoundPacket")
class IDiedPacket(val userID: Long): ServerPacket("IDiedPacket")
class RoundEndPacket(val winner: Long, val loser: Long): ServerPacket("RoundEndPacket")
class ISelectItemPacket(val userID: Long, val itemID: Int): ServerPacket("ISelectItemPacket")
class IUseItemPacket(val userID: Long, val itemID: Int): ServerPacket("IUseItemPacket")
class IDealDamagePacket(val userID: Long, val target: Long, val damage: Float): ServerPacket("IDealDamagePacket")
class ITakeDamagePacket(val userID: Long, val from: Long, val damage: Float): ServerPacket("ITakeDamagePacket")
class IMovePacket(val userID: Long, val xPos: Float, val yPos: Float, val zPos: Float): ServerPacket("IMovePacket")
class IRotationPacket(val userID: Long, val roll: Float, val pitch: Float, val yaw: Float): ServerPacket("IRotationPacket")
class IJumpPacket(val userID: Long): ServerPacket("IJumpPacket")