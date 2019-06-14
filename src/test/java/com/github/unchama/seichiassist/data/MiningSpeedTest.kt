package com.github.unchama.seichiassist.data

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * @author karayuu
 */
internal class MiningSpeedTest {
    @Test
    fun testGetNext() {
        assertEquals(MiningSpeed.INF, MiningSpeed.OFF.getNext())
        assertEquals(MiningSpeed.OFF, MiningSpeed.LV4.getNext())
    }
}