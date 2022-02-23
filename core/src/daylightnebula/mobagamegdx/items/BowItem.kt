package daylightnebula.mobagamegdx.items

import daylightnebula.mobagamegdx.players.Player

class BowItem: Item(1, "Bow") {
    override fun applyItem(player: Player, level: Int) {}
    override fun unapplyItem(player: Player, level: Int) {}
    override fun useItem(player: Player, level: Int) {}
    override fun getCooldown(player: Player, level: Int): Double {
        return 0.0
    }
}