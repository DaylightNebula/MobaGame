package daylightnebula.mobagame.networking.base.server

import daylightnebula.mobagame.network.JoinMatchPacket
import daylightnebula.mobagame.network.MatchPacket
import daylightnebula.mobagame.network.datatypes.MatchType

class Match(val matchType: MatchType): Thread() {

    companion object {
        private var currentID = 0L // todo load last match id from the database and add 1

        fun getID(): Long {
            currentID++
            return currentID--
        }
    }

    // user stuff
    val matchID = Match.getID()
    val connections = mutableListOf<Connection>()

    // state stuff
    var matchState = MatchState.SETUP
    var timeLeft = 10

    override fun run() {
        // send join packets
        val userIDs = connections.map { it.userID }
        connections.forEach {
            it.sendPacket(
                JoinMatchPacket(
                    matchID, matchType, userIDs
                )
            )
        }
    }

    fun processPacket(matchPacket: MatchPacket) {

    }

    enum class MatchState {
        SETUP,
        IN_GAME_1,
        UPDATE_1,
        IN_GAME_2,
        UPDATE_2,
        IN_GAME_3,
        CLOSED
    }
}