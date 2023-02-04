package com.example.testapp.presentation.scene.contributors

import com.example.testapp.domain.model.Contributor
import com.example.testapp.domain.usecase.ObserveContributorsUseCase
import com.example.testapp.domain.usecase.SyncContributorsUseCase
import com.example.testapp.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    observeContributorsUseCase: ObserveContributorsUseCase,
    syncContributorsUseCase: SyncContributorsUseCase,
) : BaseViewModel<ContributorsState, Unit>() {


    init {
        syncContributorsUseCase.launch(Unit)

        observeContributorsUseCase.execute(Unit)
            .onEach { postState(ContributorsState.ShowData(it)) }
            .launchNoLoading()
    }
}


sealed class ContributorsState {
    class ShowData(val data: List<Contributor>) : ContributorsState()
}