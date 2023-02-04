package com.example.testapp.domain.usecase

import com.example.testapp.domain.base.BaseUseCase
import com.example.testapp.domain.exception.ErrorConverter
import com.example.testapp.domain.repository.GithubRepository
import kotlin.coroutines.CoroutineContext

class SyncContributorsUseCase(
    context: CoroutineContext,
    converter: ErrorConverter,
    private val githubRepository: GithubRepository
) : BaseUseCase<Unit, Unit>(context, converter) {


    override suspend fun executeOnBackground(params: Unit) {
        return githubRepository.syncContributors()
    }
}