package com.example.testapp.data.repository

import com.example.testapp.data.local.GithubLocalDateSource
import com.example.testapp.data.mapper.toDomain
import com.example.testapp.data.mapper.toLocal
import com.example.testapp.data.remote.GithubApi
import com.example.testapp.domain.model.Contributor
import com.example.testapp.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GithubRepositoryImpl(
    private val githubApi: GithubApi,
    private val githubLocalDataSource: GithubLocalDateSource
) : GithubRepository {

    override fun observeContributors(): Flow<List<Contributor>> {
        return githubLocalDataSource
            .observeContributors()
            .map { contributors ->
                contributors.map { tvShowLocalDto -> tvShowLocalDto.toDomain() }
            }
    }

    override suspend fun syncContributors() {
        val contributors = githubApi.getContributors()

        val contributorsLocalData = contributors.map { it.toLocal() }

        githubLocalDataSource.flushAndInsertContributors(contributorsLocalData)
    }

}