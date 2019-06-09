package com.github.unchama.seichiassist.data.menus

import arrow.core.left
import com.github.unchama.seichiassist.SeichiAssist
import com.github.unchama.seichiassist.data.descrptions.PlayerInformationDescriptions
import com.github.unchama.seichiassist.data.itemstack.builder.SkullItemStackBuilder
import com.github.unchama.seichiassist.data.menu.InventoryView
import com.github.unchama.seichiassist.data.slot.button.ButtonBuilder
import com.github.unchama.seichiassist.data.slot.handler.ClickEventFilter
import com.github.unchama.seichiassist.data.slot.handler.SlotAction
import com.github.unchama.seichiassist.util.setLoreNotNull
import org.bukkit.ChatColor.*
import org.bukkit.entity.Player

/**
 * 木の棒メニュー
 *
 * @author karayuu
 */
object StickMenu {
    private val stickMenu: InventoryView

    init {
        val property = (4 * 9).left()
        stickMenu = InventoryView(property,"{$LIGHT_PURPLE}木の棒メニュー")
    }

    fun openBy(player: Player) {
        val data = SeichiAssist.playermap[player.uniqueId]!!
        stickMenu.setSlot(0, ButtonBuilder
            .from(
                SkullItemStackBuilder
                    .of()
                    .owner(data.uuid)
                    .title("$YELLOW$BOLD$UNDERLINE${data.name}の統計データ")
                    .lore(PlayerInformationDescriptions.playerInfoLore(data))
                    .build()
            )
            .appendAction(SlotAction(
                ClickEventFilter.LEFT_CLICK
            ) { event ->
                data.toggleExpBarVisibility()
                data.notifyExpBarVisibility()
                event.currentItem.setLoreNotNull(
                    PlayerInformationDescriptions.playerInfoLore(data))
            })
            .build()
        )

        player.openInventory(stickMenu.inventory)
    }
}
