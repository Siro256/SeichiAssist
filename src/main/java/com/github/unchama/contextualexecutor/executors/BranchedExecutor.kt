package com.github.unchama.contextualexecutor.executors

import com.github.unchama.contextualexecutor.ContextualExecutor
import com.github.unchama.contextualexecutor.RawCommandContext

/**
 * コマンドの枝分かれでのルーティングを静的に行うアクションを返す[ContextualExecutor]
 */
data class BranchedExecutor(val branches: Map<String, ContextualExecutor>,
                            val whenArgInsufficient: ContextualExecutor? = PrintUsageExecutor,
                            val whenBranchNotFound: ContextualExecutor? = PrintUsageExecutor) : ContextualExecutor {

  override suspend fun executeWith(rawContext: RawCommandContext) {
    val firstArg = rawContext.args.firstOrNull()
        ?: return whenArgInsufficient?.executeWith(rawContext).let { Unit }

    val branch = branches[firstArg]
        ?: return whenBranchNotFound?.executeWith(rawContext).let { Unit }

    val argShiftedContext = rawContext.copy(args = rawContext.args.drop(1))

    return branch.executeWith(argShiftedContext)
  }

  override fun tabCandidatesFor(context: RawCommandContext): List<String>? {
    val args = context.args

    if (args.size <= 1) return branches.keys.sorted()

    val childExecutor = branches[args.first()] ?: whenBranchNotFound ?: return null

    return childExecutor.tabCandidatesFor(context.copy(args = args.drop(1)))
  }

}