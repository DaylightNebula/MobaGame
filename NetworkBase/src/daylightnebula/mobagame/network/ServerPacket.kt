package daylightnebula.mobagame.network

import daylightnebula.mobagame.network.datatypes.MatchType
import java.io.Serializable

open class ServerPacket(val name: String): Serializable { val timeSent = System.currentTimeMillis() }
class LoginPacket(val userID: Long, val allowed: Boolean): ServerPacket("LoginPacket")

// queue packets
class QueuePacket(val matchType: MatchType): ServerPacket("QueuePacket")
class QueueResponse(val success: Boolean): ServerPacket("QueueResponse")

// match packets
open class MatchPacket(name: String, val matchID: Long, val userID: Long): ServerPacket(name)
class JoinMatchPacket(val matchID: Long, val matchType: MatchType, val users: List<Long>): ServerPacket("JoinMatchPacket")
class ForceLeavePacket(matchID: Long, userID: Long, val reason: String): MatchPacket("ForceLeavePacket", matchID, userID)
class TimeLeftPacket(matchID: Long, userID: Long, val timeLeft: Int): MatchPacket("TimeLeftPacket", matchID, userID)
class ProceedToItemSelectPacket(matchID: Long, userID: Long, val classMap: HashMap<Long, Int>): MatchPacket("ProceedToItemSelectPacket", matchID, userID)
class EndMatchPacket(matchID: Long, userID: Long, val sortedPlayers: List<Long>): MatchPacket("EndMatchPacket", matchID, userID)

// round packets
class ConnectToOpponentPacket(matchID: Long, yourUserID: Long, val theirUserID: Long, val address: String, val port: Int): MatchPacket("ConnectToOpponentPacket", matchID, yourUserID)
class StartRoundPacket(matchID: Long, userID: Long): MatchPacket("StartRoundPacket", matchID, userID)
class RoundEndPacket(matchID: Long, userID: Long): MatchPacket("RoundEndPacket", matchID, userID)
class WhoWonPacket(matchID: Long, userID: Long, val winner: Long, val loser: Long): MatchPacket("WhoWonPacket", matchID, userID)

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
class ISelectClassPacket(matchID: Long, userID: Long, val classID: Int): MatchPacket("ISelectClassPacket", matchID, userID)
class IBuyItemPacket(matchID: Long, userID: Long, val itemID: Int): MatchPacket("IBuyItemPacket", matchID, userID)
class IUpgradeItemPacket(matchID: Long, userID: Long, val itemID: Int): MatchPacket("IUpgradeItemPacket", matchID, userID)
class IReadyPacket(matchID: Long, userID: Long): MatchPacket("IReadyPacket", matchID, userID)
