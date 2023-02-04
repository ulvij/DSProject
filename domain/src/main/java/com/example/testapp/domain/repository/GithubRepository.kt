package com.example.testapp.domain.repository

import com.example.testapp.domain.model.Contributor
import kotlinx.coroutines.flow.Flow

interface GithubRepository {

    fun observeContributors(): Flow<List<Contributor>>

    suspend fun syncContributors()

}