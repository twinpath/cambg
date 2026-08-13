package com.twinpath.cambg.core.data.api

import com.twinpath.cambg.core.constant.UpdateConstants
import com.twinpath.cambg.core.data.model.UpdateRelease
import retrofit2.http.GET

interface UpdateApi {
    @GET(UpdateConstants.UPDATE_API_URL)
    suspend fun getReleases(): List<UpdateRelease>
}
