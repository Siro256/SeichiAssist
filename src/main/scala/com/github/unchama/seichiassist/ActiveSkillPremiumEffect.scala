package com.github.unchama.seichiassist

import com.github.unchama.seichiassist.ActiveSkillPremiumEffect.plugin
import com.github.unchama.seichiassist.data.XYZTuple
import com.github.unchama.seichiassist.effect.arrow.ArrowEffects
import com.github.unchama.seichiassist.effect.breaking.MagicTask
import com.github.unchama.targetedeffect.TargetedEffect
import enumeratum._
import org.bukkit.ChatColor._
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.{Location, Material}

sealed abstract class ActiveSkillPremiumEffect(val num: Int,
                                               val sql_name: String,
                                               val desc: String,
                                               val explain: String,
                                               val usePoint: Int,
                                               val material: Material) extends EnumEntry {
  @Deprecated
  def getsqlName: String = this.sql_name

  def runBreakEffect(player: Player, tool: ItemStack, breaklist: Set[Block], start: XYZTuple, end: XYZTuple, standard: Location): Unit = {
    this match {
      case ActiveSkillPremiumEffect.MAGIC => if (SeichiAssist.DEBUG) {
        new MagicTask(player, tool, breaklist, start, end, standard).runTaskTimer(plugin, 0, 100)
      } else {
        new MagicTask(player, tool, breaklist, start, end, standard).runTaskTimer(plugin, 0, 10)
      }
    }
  }

  //エフェクトの実行処理分岐
  def arrowEffect(player: Player): TargetedEffect[Player] =
    this match {
      case ActiveSkillPremiumEffect.MAGIC => ArrowEffects.singleArrowMagicEffect
    }
}

case object ActiveSkillPremiumEffect extends Enum[ActiveSkillPremiumEffect] {

  val values: IndexedSeq[ActiveSkillPremiumEffect] = findValues
  /**
   * @deprecated for interop purpose only
   */
  @Deprecated()
  val arrayValues: Array[ActiveSkillPremiumEffect] = values.toArray
  private val plugin = SeichiAssist.instance

  def fromSqlName(sqlName: String): Option[ActiveSkillPremiumEffect] = values.find(sqlName == _.sql_name)

  case object MAGIC extends ActiveSkillPremiumEffect(1, "ef_magic", s"$RED$UNDERLINE${BOLD}マジック", "鶏が出る手品", 10, Material.RED_ROSE)
}
