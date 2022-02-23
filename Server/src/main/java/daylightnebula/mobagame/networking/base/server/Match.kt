package daylightnebula.mobagame.networking.base.server

import daylightnebula.mobagame.network.*
import daylightnebula.mobagame.network.datatypes.MatchType
import daylightnebula.mobagame.network.playerstuffs.GameClass
import daylightnebula.mobagame.networking.base.server.Server.Companion.MAX_ITEMS

class Match(val matchType: MatchType): Thread() {

    companion object {
        private var currentID = 0L // todo load last match id from the database and add 1

        const val roundLength = 160
        const val classSelectLength = 20
        const val selectLength = 45
        const val updateLength = 20

        fun getID(): Long {
            currentID++
            return currentID--
        }
    }

    // user stuff
    val matchID = getID()
    val players = mutableListOf<MatchPlayer>()
    val currentRounds = mutableListOf<Triple<MatchPlayer, MatchPlayer, Boolean>>() // player 1, player 2, active?

    // state stuff
    var matchState = MatchState.CLASS_SELECTION
    var timeLeft = classSelectLength
    var rounds = 0
    val ready = mutableListOf<Long>()

    override fun run() {
        // send join packets
        val userIDs = players.map { it.conn.userID }
        players.forEach {
            it.conn.sendPacket(
                JoinMatchPacket(
                    matchID, matchType, userIDs
                )
            )
        }

        // loop, only update once per second
        while (true) {
            // record the start time
            val start = System.currentTimeMillis()

            // check if ready
            println("Ready check ${ready.size} ${players.size}")
            if (ready.size == players.size) {
                println("Ready")
                timeLeft = 0
                ready.clear()
            }

            // check if the state needs to be updated
            if (timeLeft <= 0) {
                println("Updated match state")
                when (matchState) {
                    MatchState.CLASS_SELECTION -> proceedToItemSelect()
                    MatchState.ITEM_SELECTION -> startRound(0)
                    MatchState.ROUND_1 -> endRound()
                    MatchState.UPDATE_1 -> startRound(1)
                    MatchState.ROUND_2 -> endRound()
                    MatchState.UPDATE_2 -> startRound(2)
                    MatchState.ROUND_3 -> scoreboardAndEnd()
                    else -> println("Unknown state $matchState")
                }
            }

            // update current state time left
            timeLeft--

            // send time left packet
            players.forEach {
                it.conn.sendPacket(
                    TimeLeftPacket(matchID, it.conn.userID, timeLeft)
                )
            }

            // make tick 1000 ms long
            val end = System.currentTimeMillis()
            val diff = end - start
            if (diff >= 0) {
                if (diff >= 1000)
                    println("Last game tick took longer than 1 second!")
                else
                    sleep(1000 - diff)
            }
        }
    }

    private fun proceedToItemSelect() {
        // build class select hash map
        val classMap = HashMap<Long, Int>()
        players.forEach {
            if (it.classID == -1) it.classID = GameClass.list.random().classID
            classMap[it.conn.userID] = it.classID
        }

        // send class map to each player
        players.forEach {
            it.conn.sendPacket(ProceedToItemSelectPacket(
                matchID, it.conn.userID, classMap
            ))
        }

        matchState = MatchState.ITEM_SELECTION
        timeLeft = selectLength
    }

    private fun startRound(round: Int) {
        timeLeft = roundLength

        // prepare to send players to their matches
        sortByKD()

        // send players to their matches
        currentRounds.clear()
        for (i in 0 until (players.size / 2)) {
            // get connections and add them to the list
            val p1 = players[i * 2]
            val p2 = players[i * 2 + 1]
            currentRounds.add(Triple(p1, p2, true))

            // reset taken and recorded damage
            p1.damageTaken = 0f
            p2.damageRecorded = 0f

            // tell them to connect to each other
            p1.conn.sendPacket(
                ConnectToOpponentPacket(
                    matchID, p1.conn.userID, p2.conn.userID, p2.conn.socket.inetAddress.hostAddress, p2.conn.socket.port, true
                )
            )
            p2.conn.sendPacket(
                ConnectToOpponentPacket(
                    matchID, p2.conn.userID, p1.conn.userID, p1.conn.socket.inetAddress.hostAddress, p2.conn.socket.port, false
                )
            )

            // tell them to start their round
            p1.conn.sendPacket(StartRoundPacket(matchID, p1.conn.userID))
            p2.conn.sendPacket(StartRoundPacket(matchID, p2.conn.userID))
        }
    }

    fun endRound() {
        // for each round, tell all of the players to end their rounds
        currentRounds.forEach {
            // up back entry
            val p1 = it.first
            val p2 = it.second
            val stillActive = it.third

            // if tied, award a winner
            if (stillActive) {
                val p1taken = if (p1.damageTaken != p1.damageRecorded) (p1.damageTaken + p1.damageRecorded) / 2 else p1.damageTaken
                val p2taken = if (p2.damageTaken != p2.damageRecorded) (p2.damageTaken + p2.damageRecorded) / 2 else p2.damageTaken
                val winner = if (p1taken > p2taken) p2 else p1
                val loser = if (p1taken <= p2taken) p2 else p1

                p1.conn.sendPacket(WhoWonPacket(matchID, p1.conn.userID, winner.conn.userID, loser.conn.userID))
                p2.conn.sendPacket(WhoWonPacket(matchID, p2.conn.userID, winner.conn.userID, loser.conn.userID))
            }

            // send round end packet
            p1.conn.sendPacket(RoundEndPacket(matchID, p1.conn.userID))
            p2.conn.sendPacket(RoundEndPacket(matchID, p2.conn.userID))
        }

        // update round counter
        rounds++
    }

    private fun scoreboardAndEnd() {
        // for each player, tell them to end match, and handle their discrepancies
        sortByKD()
        val list = players.map { it.conn.userID }
        players.forEach {
            // packet
            it.conn.sendPacket(
                EndMatchPacket(matchID, it.conn.userID, list)
            )

            // discrepancies
            if (it.discrepancies > 0)
                println("User ${it.conn.userID} had ${it.discrepancies} discrepancies")
        }
    }

    private fun sortByKD() {
        if (rounds > 0) players.sortByDescending { it.wins / rounds }
    }

    // TODO discrepancy checks
    // TODO death checks
    // TODO move and jump checks
    // TODO damage range checking

    fun processPacket(connection: Connection, packet: MatchPacket) {
        if (packet is IDiedPacket) {
            // get winner and loser
            val loser = players.firstOrNull { it.conn.userID == packet.userID } ?: run {
                players.first { it.conn == connection }.discrepancies++
                return
            }
            val roundEntry = currentRounds.firstOrNull { it.first == loser || it.second == loser } ?: run {
                players.first { it.conn == connection }.discrepancies++
                return
            }
            val winner = if (roundEntry.first == loser) roundEntry.second else roundEntry.first

            // send packets
            winner.conn.sendPacket(
                WhoWonPacket(matchID, winner.conn.userID, winner.conn.userID, loser.conn.userID)
            )
            loser.conn.sendPacket(
                WhoWonPacket(matchID, loser.conn.userID, winner.conn.userID, loser.conn.userID)
            )

            // update wins
            winner.wins++

            // update round entry
            val newEntry = roundEntry.copy(third = false)
            currentRounds.remove(roundEntry)
            currentRounds.add(newEntry)
        } else if (packet is ISelectItemPacket) {
            // check if the player can select that item and update vars
            val player = players.firstOrNull { it.conn.userID == packet.userID } ?: run {
                players.first { it.conn == connection }.discrepancies++
                return
            }
            if (player.primeItem != packet.itemID && player.secondItem != packet.itemID && player.armorItem != packet.itemID)
                player.discrepancies++
            player.currentItem = packet.itemID
        } else if (packet is IUseItemPacket) {
            // check if the player can use this item
            val player = players.firstOrNull { it.conn.userID == packet.userID } ?: run {
                players.first { it.conn == connection }.discrepancies++
                return
            }
            if (player.currentItem != packet.itemID)
                player.discrepancies++
        } else if (packet is IDealDamagePacket) {
            // update damage
            val target = players.firstOrNull { it.conn.userID == packet.target } ?: run {
                players.first { it.conn == connection }.discrepancies++
                return
            }
            target.damageTaken += packet.damage
        } else if (packet is ITakeDamagePacket) {
            // update damage
            val target = players.first { it.conn == connection }
            target.damageTaken += packet.damage
        } else if (packet is IMovePacket) {
            // update position
            val player = players.first { it.conn == connection }
            player.xPos = packet.xPos
            player.yPos = packet.yPos
            player.zPos = packet.zPos
        } else if (packet is IRotationPacket) {
            // update rotation
            val player = players.first { it.conn == connection }
            player.yaw = packet.yaw
            player.pitch = packet.pitch
            player.roll = packet.roll
        } else if (packet is IBuyItemPacket) {
            // update item
            val player = players.first { it.conn == connection }
            if (packet.itemSlot !in 0..2)
                player.discrepancies++
            else if (packet.itemSlot == 0)
                player.primeItem = packet.itemID
            else if (packet.itemSlot == 1)
                player.secondItem = packet.itemID
            else if (packet.itemSlot == 2)
                player.armorItem = packet.itemID
        } else if (packet is IUpgradeItemPacket) {
            val player = players.first { it.conn == connection }
            if (player.primeItem != packet.itemID && player.secondItem != packet.itemID && player.armorItem != packet.itemID)
                player.discrepancies++
            // todo item upgrade functionality
        } else if (packet is ISelectClassPacket) {
            println("Checking connections ${players.size}")
            val player = players.first {
                println("${it.conn} $connection")
                it.conn == connection
            }
            if (GameClass.list.any { it.classID == packet.classID })
                player.classID = packet.classID
            else {
                player.discrepancies++
                player.classID = GameClass.list.random().classID
            }
        } else if (packet is IReadyPacket) {
            val playerID = players.first { it.conn == connection }.conn.userID
            if (!ready.contains(playerID))
                ready.add(playerID)
        }
    }

    enum class MatchState {
        CLASS_SELECTION,
        ITEM_SELECTION,
        ROUND_1,
        UPDATE_1,
        ROUND_2,
        UPDATE_2,
        ROUND_3,
        CLOSED
    }

    class MatchPlayer(
        val conn: Connection,
        var wins: Int = 0,
        var damageTaken: Float = 0f,
        var damageRecorded: Float = 0f,
        var discrepancies: Int = 0,
        var classID: Int = -1,
        var primeItem: Int = -1,
        var secondItem: Int = -1,
        var armorItem: Int = -1,
        var currentItem: Int = 0,
        var xPos: Float = 0f, var yPos: Float = 0f, var zPos: Float = 0f,
        var yaw: Float = 0f, var pitch: Float = 0f, var roll: Float = 0f
    )
}