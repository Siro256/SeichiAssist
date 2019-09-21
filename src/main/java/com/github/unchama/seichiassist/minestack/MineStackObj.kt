package com.github.unchama.seichiassist.minestack

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

open class MineStackObj(val mineStackObjName: String,
                        val uiName: String?,
                        val level: Int,
                        itemStack: ItemStack,
                        val hasNameLore: Boolean,
                        val gachaType: Int,
                        val stackType: MineStackObjectCategory) {

  val itemStack: ItemStack = itemStack.clone().apply {
    amount = 1
  }

  constructor(objName: String, uiName: String?,
              level: Int, material: Material, durability: Int,
              nameLoreFlag: Boolean, gachaType: Int, stackType: MineStackObjectCategory): this(
      objName, uiName, level, ItemStack(material, 1, durability.toShort()), nameLoreFlag, gachaType, stackType
  )

  val material: Material
    get() = itemStack.type

  val durability: Int
    get() = itemStack.durability.toInt()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    val that = other as MineStackObj?
    return mineStackObjName == that?.mineStackObjName
  }

  override fun hashCode(): Int = Objects.hash(mineStackObjName)
}
