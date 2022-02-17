package daylightnebula.mobagamegdx.`class`

abstract class Class(val name: String, val classID: Int, val primeItems: List<Int>, val secondItems: List<Int>, val armorItems: List<Int>) {
    companion object {
        val list = mutableListOf<Class>()
    }

    init {
        list.add(this)
    }
}
class RangerClass: Class("Ranger", 0, listOf(0), listOf(0), listOf(0))
class FighterClass: Class("Fighter", 1, listOf(0), listOf(0), listOf(0))
class AssassinClass: Class("Assassin", 2, listOf(0), listOf(0), listOf(0))
class TankClass: Class("Tank", 3, listOf(0), listOf(0), listOf(0))
class SpecialistClass: Class("Specialists", 4, listOf(0), listOf(0), listOf(0))