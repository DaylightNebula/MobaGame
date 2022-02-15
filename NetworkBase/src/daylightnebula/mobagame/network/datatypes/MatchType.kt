package daylightnebula.mobagame.network.datatypes

enum class MatchType(val active: Boolean) {
    RANKED(false),
    NORMALS(true),
    ALL_RANDOM(false),
    CHAOS(false)
}