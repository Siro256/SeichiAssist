package com.github.unchama.seichiassist.data

import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.DARK_RED
import org.bukkit.ChatColor.DARK_GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.entity.Player

/**
 * @author karayuu
 */
enum class MiningSpeed(
    val speed: Int,
    val description: String
) {
    INF(25565, "ON(無制限)"),
    LV1(127, "ON(127制限)"),
    LV2(200, "ON(200制限)"),
    LV3(400, "ON(400制限)"),
    LV4(600, "ON(600制限)"),
    OFF(0, "OFF")
    ;

    fun getNext(): MiningSpeed {
        val index = values().indexOf(this)
        return values()[(index + 1) % values().size]
    }
}

class MiningSpeedFlag(var miningSpeed: MiningSpeed, val player: Player) {
    /**
     * メニューで使用する説明文を取得します.
     */
    fun lore(): List<String> {
        return if (miningSpeed != MiningSpeed.OFF) {
            listOf(
                "${GREEN}現在${miningSpeed.description}",
                "${DARK_RED}クリックで${miningSpeed.getNext().description}"
            )
        } else {
            listOf(
                "${RED}現在${miningSpeed.description}",
                "${DARK_GREEN}クリックで${miningSpeed.getNext().description}"
            )
        }
    }

    /**
     * 現在の [MiningSpeed] の次の [MiningSpeed] にセットさせます.
     */
    fun setNextFlag() {
        miningSpeed = miningSpeed.getNext()
    }

    /**
     * 現在の [MiningSpeed] をプレイヤーに通知します.
     */
    fun notifyCurrentFlag(): String {
        return if (miningSpeed != MiningSpeed.OFF) {
            "${GREEN}採掘速度上昇:${miningSpeed.description}"
        } else {
            "${RED}採掘速度上昇:${miningSpeed.description}"
        }
    }
}