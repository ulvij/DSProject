package com.example.testapp.data.remote

import com.example.testapp.data.remote.model.ContributorRemoteDto
import retrofit2.http.GET

interface GithubApi {

    @GET("repos/JakeWharton/timber/contributors")
    suspend fun getContributors(): List<ContributorRemoteDto>

}