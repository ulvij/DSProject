package com.example.testapp.data.local

import com.example.testapp.data.local.models.ContributorLocalDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GithubLocalDataSourceImpl(
    private val githubDao: GithubDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GithubLocalDateSource {

    override fun observeContributors(): Flow<List<ContributorLocalDto>> {
        return githubDao.observeContributors()
    }

    override suspend fun flushAndInsertContributors(data: List<ContributorLocalDto>) {
        withContext(ioDispatcher) {
            githubDao.clearData()
            githubDao.insertContributors(data)
        }
    }

}