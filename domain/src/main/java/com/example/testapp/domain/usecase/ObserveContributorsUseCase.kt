package com.example.testapp.domain.usecase

import com.example.testapp.domain.base.BaseFlowUseCase
import com.example.testapp.domain.exception.ErrorConverter
import com.example.testapp.domain.model.Contributor
import com.example.testapp.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class ObserveContributorsUseCase(
    context: CoroutineContext,
    converter: ErrorConverter,
    private val repository: GithubRepository
) : BaseFlowUseCase<Unit, List<Contributor>>(context, converter) {

    override fun createFlow(params: Unit): Flow<List<Contributor>> {
        return repository.observeContributors()
    }
}