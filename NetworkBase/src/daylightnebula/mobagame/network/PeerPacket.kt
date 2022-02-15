package daylightnebula.mobagame.network

import daylightnebula.mobagame.network.datatypes.PlayerAnimation
import java.io.Serializable

/*
byte	1 byte	    Stores whole numbers from -128 to 127
short	2 bytes	    Stores whole numbers from -32,768 to 32,767
int	    4 bytes	    Stores whole numbers from -2,147,483,648 to 2,147,483,647
long	8 bytes	    Stores whole numbers from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
float	4 bytes	    Stores fractional numbers. Sufficient for storing 6 to 7 decimal digits
double	8 bytes	    Stores fractional numbers. Sufficient for storing 15 decimal digits
boolean	1 bit	    Stores true or false values
char	2 bytes	    Stores a single character/letter or ASCII values
 */
abstract class PeerPacket(val name: String): Serializable { val timeSent = System.currentTimeMillis() }
class PeerInitPlayerPacket(val userID: Long, val xPos: Float, val yPos: Float, val zPos: Float): PeerPacket("PeerInitPlayerPacket")
class PeerMovePlayerPacket(val x: Float, val y: Float, val z: Float): PeerPacket("PeerMovePlayerPacket")
class PeerPlayerAnimationChangePacket(val lastAnimation: PlayerAnimation, val animation: PlayerAnimation): PeerPacket("PeerPlayerAnimationChangePacket")
class PeerPlayerJumpPacket: PeerPacket("PeerPlayerJumpPacket")
class PeerRotatePlayerPacket(val x: Float, val y: Float, val z: Float, val w: Float): PeerPacket("PeerRotatePlayerPacket")
class PeerSelectItemPacket(val itemID: Int): PeerPacket("PeerSelectItemPacket")
class PeerUseItemPacket: PeerPacket("PeerUseItemPacket")
class PeerDamagePacket(val damage: Float): PeerPacket("PeerDamagePacket")