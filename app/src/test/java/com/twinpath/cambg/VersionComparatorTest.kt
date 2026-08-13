package com.twinpath.cambg

import com.twinpath.cambg.core.util.VersionComparator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionComparatorTest {

    @Test
    fun testSameVersion() {
        assertEquals(0, VersionComparator.compare("1.0.0", "1.0.0"))
        assertEquals(0, VersionComparator.compare("v1.0.0", "1.0.0"))
        assertEquals(0, VersionComparator.compare("v0.1.6-beta.1", "0.1.6-beta.1"))
    }

    @Test
    fun testMajorDifference() {
        assertTrue(VersionComparator.compare("2.0.0", "1.0.0") > 0)
        assertTrue(VersionComparator.compare("1.0.0", "2.0.0") < 0)
    }

    @Test
    fun testMinorDifference() {
        assertTrue(VersionComparator.compare("0.2.0", "0.1.0") > 0)
        assertTrue(VersionComparator.compare("0.1.0", "0.2.0") < 0)
    }

    @Test
    fun testPatchDifference() {
        assertTrue(VersionComparator.compare("0.1.6", "0.1.5") > 0)
        assertTrue(VersionComparator.compare("0.1.5", "0.1.6") < 0)
    }

    @Test
    fun testPreReleaseVsStable() {
        // Stable is always newer than a pre-release version of the same release
        assertTrue(VersionComparator.compare("1.0.0", "1.0.0-beta.1") > 0)
        assertTrue(VersionComparator.compare("1.0.0-alpha.1", "1.0.0") < 0)
    }

    @Test
    fun testPreReleaseTypePriority() {
        // alpha < beta < test
        assertTrue(VersionComparator.compare("1.0.0-beta.1", "1.0.0-alpha.1") > 0)
        assertTrue(VersionComparator.compare("1.0.0-test.1", "1.0.0-beta.1") > 0)
        assertTrue(VersionComparator.compare("1.0.0-alpha.5", "1.0.0-beta.1") < 0)
    }

    @Test
    fun testPreReleaseNumbers() {
        assertTrue(VersionComparator.compare("1.0.0-beta.2", "1.0.0-beta.1") > 0)
        assertTrue(VersionComparator.compare("1.0.0-beta.1", "1.0.0-beta.2") < 0)
    }
}
