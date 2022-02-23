package daylightnebula.mobagame.network.playerstuffs

abstract class GameClass(val name: String, val classID: Int, val primeItems: List<Int>, val secondItems: List<Int>, val armorItems: List<Int>) {
    companion object {
        val list = mutableListOf<GameClass>()
        val ranger = RangerClass()
        val fighter = FighterClass()
        val assassin = AssassinClass()
        val tank = TankClass()
        val specialist = SpecialistClass()
    }

    init {
        list.add(this)
    }
}
class RangerClass: GameClass("Ranger", 0, listOf(0), listOf(0), listOf(0))
class FighterClass: GameClass("Fighter", 1, listOf(0), listOf(0), listOf(0))
class AssassinClass: GameClass("Assassin", 2, listOf(0), listOf(0), listOf(0))
class TankClass: GameClass("Tank", 3, listOf(0), listOf(0), listOf(0))
class SpecialistClass: GameClass("Specialists", 4, listOf(0), listOf(0), listOf(0))