package com.example.testapp.data.local

import com.example.testapp.data.local.models.ContributorLocalDto
import kotlinx.coroutines.flow.Flow

interface GithubLocalDateSource {

    fun observeContributors(): Flow<List<ContributorLocalDto>>

    suspend fun flushAndInsertContributors(data: List<ContributorLocalDto>)
}