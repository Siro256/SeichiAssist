package com.github.unchama.seichiassist.commands

import com.github.unchama.contextualexecutor.ContextualExecutor
import com.github.unchama.contextualexecutor.asNonBlockingTabExecutor
import com.github.unchama.contextualexecutor.executors.BranchedExecutor
import com.github.unchama.seichiassist.SeichiAssist
import com.github.unchama.seichiassist.commands.contextual.builder.BuilderTemplates.playerCommandBuilder
import com.github.unchama.targetedeffect.EmptyEffect
import com.github.unchama.targetedeffect.asMessageEffect
import com.github.unchama.targetedeffect.ops.plus
import org.bukkit.ChatColor

object EffectCommand {
  private val printUsageExecutor: ContextualExecutor = playerCommandBuilder
      .execution {
        listOf(
            "${ChatColor.YELLOW}${ChatColor.BOLD}[コマンドリファレンス]",
            "${ChatColor.RED}/ef",
            "採掘速度上昇効果の制限を変更することができます。",
            "${ChatColor.RED}/ef smart",
            "採掘速度上昇効果の内訳を表示するかしないかを変更することができます。"
        ).asMessageEffect()
      }
      .build()

  private val toggleExecutor = playerCommandBuilder
      .execution { context ->
        val playerData = SeichiAssist.playermap[context.sender.uniqueId] ?: return@execution EmptyEffect
        val toggleResponse = playerData.settings.fastDiggingEffectSuppression.suppressionDegreeToggleEffect
        val guidance = "再度 /ef コマンドを実行することでトグルします。".asMessageEffect()

        toggleResponse + guidance
      }
      .build()

  private val messageFlagToggleExecutor = playerCommandBuilder
      .execution { context ->
        val playerData = SeichiAssist.playermap[context.sender.uniqueId] ?: return@execution EmptyEffect

        playerData.toggleMessageFlag()
      }
      .build()

  val executor = BranchedExecutor(
      mapOf("smart" to messageFlagToggleExecutor),
      whenArgInsufficient = toggleExecutor, whenBranchNotFound = printUsageExecutor
  ).asNonBlockingTabExecutor()

}
