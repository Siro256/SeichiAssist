package com.github.unchama.seichiassist.data.player

// FIXME semantics is not clear
class MineBlock {
  var after: Long = 0
  var before: Long = 0
  var increase: Long = 0

  fun setIncrease() {
    increase = after - before
  }
}
