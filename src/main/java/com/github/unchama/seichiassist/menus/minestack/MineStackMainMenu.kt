package com.github.unchama.seichiassist.menus.minestack

import com.github.unchama.itemstackbuilder.IconItemStackBuilder
import com.github.unchama.menuinventory.IndexedSlotLayout
import com.github.unchama.menuinventory.Menu
import com.github.unchama.menuinventory.MenuInventoryView
import com.github.unchama.menuinventory.rows
import com.github.unchama.menuinventory.slot.button.Button
import com.github.unchama.menuinventory.slot.button.action.LeftClickButtonEffect
import com.github.unchama.seichiassist.CommonSoundEffects
import com.github.unchama.seichiassist.Schedulers
import com.github.unchama.seichiassist.SeichiAssist
import com.github.unchama.seichiassist.menus.CommonButtons
import com.github.unchama.seichiassist.minestack.MineStackObjectCategory
import com.github.unchama.seichiassist.minestack.MineStackObjectCategory.*
import com.github.unchama.targetedeffect.TargetedEffect
import com.github.unchama.targetedeffect.computedEffect
import com.github.unchama.targetedeffect.sequentialEffect
import com.github.unchama.targetedeffect.unfocusedEffect
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.entity.Player

object MineStackMainMenu: Menu {
  private object ButtonComputations {
    val categoryButtonLayout = run {
      fun iconMaterialFor(category: MineStackObjectCategory): Material = when (category) {
        ORES -> Material.DIAMOND_ORE
        MOB_DROP -> Material.ENDER_PEARL
        AGRICULTURAL -> Material.SEEDS
        BUILDING -> Material.SMOOTH_BRICK
        REDSTONE_AND_TRANSPORTATION -> Material.REDSTONE
        GACHA_PRIZES -> Material.GOLDEN_APPLE
      }

      val layoutMap = MineStackObjectCategory.values().mapIndexed { index, category ->
        val slotIndex = index + 1 // 0には自動スタック機能トグルが入るので、1から入れ始める
        val iconItemStack = IconItemStackBuilder(iconMaterialFor(category))
            .title("$BLUE$UNDERLINE$BOLD${category.uiLabel}")
            .build()

        val button = Button(
            iconItemStack,
            LeftClickButtonEffect(
                CommonSoundEffects.menuTransitionFenceSound,
                CategorizedMineStackMenu.forCategory(category).open
            )
        )
        slotIndex to button
      }.toMap()

      IndexedSlotLayout(layoutMap)
    }

    /**
     * メインメニュー内の「履歴」機能部分のレイアウトを計算する
     */
    suspend fun Player.computeHistoricalMineStackLayout(): IndexedSlotLayout {
      val playerData = SeichiAssist.playermap[uniqueId]!!

      val buttonMapping = playerData.hisotryData.usageHistory.mapIndexed { index, mineStackObject ->
        val slotIndex = 18 + index // 3行目から入れだす
        val button = with(MineStackButtons) { getMineStackItemButtonOf(mineStackObject) }

        slotIndex to button
      }.toMap()

      return IndexedSlotLayout(buttonMapping)
    }
  }

  private suspend fun Player.computeMineStackMainMenuLayout(): IndexedSlotLayout {
    return with(ButtonComputations) {
      IndexedSlotLayout(
          0 to with (MineStackButtons) { computeAutoMineStackToggleButton() },
          45 to CommonButtons.openStickMenu
      )
          .merge(categoryButtonLayout)
          .merge(computeHistoricalMineStackLayout())
    }
  }

  override val open: TargetedEffect<Player> = computedEffect { player ->
    val session = MenuInventoryView(
        6.rows(),
        "$DARK_PURPLE${BOLD}MineStackメインメニュー"
    ).createNewSession()

    sequentialEffect(
        session.openEffectThrough(Schedulers.sync),
        unfocusedEffect { session.overwriteViewWith(player.computeMineStackMainMenuLayout()) }
    )
  }
}
