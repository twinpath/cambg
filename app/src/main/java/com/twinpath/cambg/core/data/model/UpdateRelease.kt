package com.twinpath.cambg.core.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateRelease(
    val version: String, // Contoh: "v0.1.6-beta.1"
    val releaseType: String, // "stable", "beta", "alpha", "test"
    val date: String,
    val notes: String,
    val assets: List<UpdateAsset>
)

@JsonClass(generateAdapter = true)
data class UpdateAsset(
    val name: String,
    val downloadUrl: String,
    val sizeLabel: String,
    val architecture: String
)
