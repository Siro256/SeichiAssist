package com.github.unchama.seichiassist

import com.github.unchama.seichiassist.ManagedWorld.*
import org.bukkit.World

enum class ManagedWorld(
    val alphabetName: String,
    val japaneseName: String) {

  WORLD_SPAWN("world_spawn", "スポーンワールド"),
  WORLD("world", "メインワールド"),
  WORLD_SW("world_SW", "第一整地ワールド"),
  WORLD_SW_2("world_SW_2", "第二整地ワールド"),
  WORLD_SW_3("world_SW_3", "第三整地ワールド"),
  WORLD_SW_NETHER("world_SW_nether", "整地ネザー"),
  WORLD_SW_END("world_SW_the_end", "整地エンド");

  companion object {
    val seichiWorlds = values().filter { it.isSeichi }

    fun fromName(worldName: String): ManagedWorld? = values().find { it.name == worldName }

    fun fromBukkitWorld(world: World): ManagedWorld? = fromName(world.name)
  }
}

val ManagedWorld.isSeichi: Boolean
  get() = when (this) {
    WORLD_SW, WORLD_SW_2, WORLD_SW_3, WORLD_SW_NETHER, WORLD_SW_END -> true
    else -> false
  }

/**
 * 保護を掛けて整地するワールドであるかどうか
 */
val ManagedWorld.isRegionSeichi: Boolean
  get() = this == WORLD_SW_2

val ManagedWorld.shouldMuteCoreProtect: Boolean
  get() = this.isRegionSeichi

fun World.asManagedWorld(): ManagedWorld? = Companion.fromBukkitWorld(this)
