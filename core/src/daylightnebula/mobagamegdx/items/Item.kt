package daylightnebula.mobagamegdx.items

import daylightnebula.mobagamegdx.players.Player

abstract class Item(val id: Int) {
    companion object {
        // list stuffs
        var id = 0
        val items = mutableListOf<Item>()

        val knifeItem = KnifeItem(0)

        // returns an item based on its ID
        fun getItem(itemID: Int): Item {
            return items[itemID]
        }
    }

    init {
        Item.items.add(this)
    }

    abstract fun applyItem(player: Player, level: Int)
    abstract fun unapplyItem(player: Player, level: Int)
    abstract fun useItem(player: Player, level: Int)
    abstract fun getCooldown(player: Player, level: Int): Double
}