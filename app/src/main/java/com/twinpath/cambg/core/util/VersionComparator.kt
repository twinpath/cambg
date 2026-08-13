package com.twinpath.cambg.core.util

object VersionComparator {

    /**
     * Compares two semantic version strings.
     * Returns a negative integer, zero, or a positive integer as the first version
     * is less than, equal to, or greater than the second version.
     *
     * Supports formats like "v1.0.0", "1.0.0-beta.1", "1.0.0-alpha.2", "v0.1.6-test.3" etc.
     */
    fun compare(v1: String, v2: String): Int {
        val clean1 = v1.trim().removePrefix("v").removePrefix("V")
        val clean2 = v2.trim().removePrefix("v").removePrefix("V")

        if (clean1 == clean2) return 0

        val parts1 = clean1.split("-")
        val parts2 = clean2.split("-")

        val main1 = parts1[0]
        val main2 = parts2[0]

        // Compare main major.minor.patch version
        val mainCompare = compareMainVersion(main1, main2)
        if (mainCompare != 0) {
            return mainCompare
        }

        // If main versions are equal, a release with no pre-release suffix is newer than one with a suffix
        val hasSuffix1 = parts1.size > 1
        val hasSuffix2 = parts2.size > 1

        return when {
            !hasSuffix1 && hasSuffix2 -> 1  // v1.0.0 is newer than v1.0.0-beta.1
            hasSuffix1 && !hasSuffix2 -> -1 // v1.0.0-beta.1 is older than v1.0.0
            hasSuffix1 && hasSuffix2 -> compareSuffix(parts1[1], parts2[1]) // Both have suffixes
            else -> 0
        }
    }

    private fun compareMainVersion(main1: String, main2: String): Int {
        val num1 = main1.split(".").map { it.toIntOrNull() ?: 0 }
        val num2 = main2.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(num1.size, num2.size)
        for (i in 0 until maxLength) {
            val val1 = num1.getOrElse(i) { 0 }
            val val2 = num2.getOrElse(i) { 0 }
            if (val1 != val2) {
                return val1.compareTo(val2)
            }
        }
        return 0
    }

    private fun compareSuffix(suffix1: String, suffix2: String): Int {
        // Suffix format is typically "beta.1", "alpha.2", "test.3", etc.
        val parts1 = suffix1.split(".")
        val parts2 = suffix2.split(".")

        val type1 = parts1[0].lowercase()
        val type2 = parts2[0].lowercase()

        val priority1 = getSuffixPriority(type1)
        val priority2 = getSuffixPriority(type2)

        if (priority1 != priority2) {
            return priority1.compareTo(priority2)
        }

        // Suffix types are the same (e.g. beta vs beta), compare numbers
        val num1 = parts1.getOrNull(1)?.toIntOrNull() ?: 0
        val num2 = parts2.getOrNull(1)?.toIntOrNull() ?: 0
        return num1.compareTo(num2)
    }

    private fun getSuffixPriority(type: String): Int {
        return when (type) {
            "alpha" -> 1
            "beta" -> 2
            "test" -> 3
            else -> 0 // unknown suffix
        }
    }
}
