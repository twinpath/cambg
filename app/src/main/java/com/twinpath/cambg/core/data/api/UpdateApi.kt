package com.twinpath.cambg.core.data.api

import com.twinpath.cambg.core.data.model.UpdateRelease
import retrofit2.http.GET
import retrofit2.http.Url

interface UpdateApi {
    @GET
    suspend fun getReleases(@Url url: String): List<UpdateRelease>
}
