package daylightnebula.mobagame.network

import daylightnebula.mobagame.network.datatypes.MatchType
import java.io.Serializable

open class ServerPacket(val name: String): Serializable { val timeSent = System.currentTimeMillis() }
class LoginPacket(val userID: Long, val allowed: Boolean): ServerPacket("LoginPacket")

// queue packets
class QueuePacket(val matchType: MatchType): ServerPacket("QueuePacket")
class QueueResponse(val success: Boolean): ServerPacket("QueueResponse")

// match packets
class JoinMatchPacket(val matchID: Long, val matchType: MatchType, val users: List<Long>): ServerPacket("JoinMatchPacket")
class ForceLeavePacket(val matchID: Long, val userID: Long, val reason: String): ServerPacket("ForceLeavePacket")
class TimeLeftPacket(val timeLeft: Int): ServerPacket("TimeLeftPacket")

// round packets
open class MatchPacket(name: String, val matchID: Long, val userID: Long): ServerPacket(name)
class ConnectToOpponentPacket(matchID: Long, userID: Long, val address: String, val port: Int): MatchPacket("ConnectToOpponentPacket", matchID, userID)
class StartRoundPacket(matchID: Long, userID: Long): MatchPacket("StartRoundPacket", matchID, userID)
class RoundEndPacket(matchID: Long, userID: Long, val winner: Long, val loser: Long): MatchPacket("RoundEndPacket", matchID, userID)

// I packets
class IDiedPacket(matchID: Long, userID: Long): MatchPacket("IDiedPacket", matchID, userID)
class ISelectItemPacket(matchID: Long,  userID: Long, val itemID: Int): MatchPacket("ISelectItemPacket", matchID, userID)
class IUseItemPacket(matchID: Long, userID: Long, val itemID: Int): MatchPacket("IUseItemPacket", matchID, userID)
class IDealDamagePacket(matchID: Long, userID: Long, val target: Long, val damage: Float): MatchPacket("IDealDamagePacket", matchID, userID)
class ITakeDamagePacket(matchID: Long, userID: Long, val from: Long, val damage: Float): MatchPacket("ITakeDamagePacket", matchID, userID)
class IMovePacket(matchID: Long, userID: Long, val xPos: Float, val yPos: Float, val zPos: Float): MatchPacket("IMovePacket", matchID, userID)
class IRotationPacket(matchID: Long, userID: Long, val roll: Float, val pitch: Float, val yaw: Float): MatchPacket("IRotationPacket", matchID, userID)
class IJumpPacket(matchID: Long, userID: Long): MatchPacket("IJumpPacket", matchID, userID)
class ILeaveMatchPacket(matchID: Long, userID: Long): MatchPacket("ILeaveMatchPacket", matchID, userID)
class IBuyItemPacket(matchID: Long, userID: Long, val itemID: Int): MatchPacket("IBuyItemPacket", matchID, userID)
class IUpgradeItemPacket(matchID: Long, userID: Long, val itemID: Int): MatchPacket("IUpgradeItemPacket", matchID, userID)
class IReadyPacket(matchID: Long, userID: Long): MatchPacket("IReadyPacket", matchID, userID)
