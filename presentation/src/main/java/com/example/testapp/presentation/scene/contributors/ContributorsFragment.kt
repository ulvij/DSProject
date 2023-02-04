package com.example.testapp.presentation.scene.contributors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.testapp.presentation.base.BaseFragment
import com.example.testapp.presentation.databinding.FragmentContributorsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContributorsFragment : BaseFragment<ContributorsState, Unit, ContributorsViewModel, FragmentContributorsBinding>() {

    override val bindingCallback: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContributorsBinding
        get() = FragmentContributorsBinding::inflate

    override val viewModel: ContributorsViewModel by viewModels()

    private val adapter: ContributorListAdapter by lazy { ContributorListAdapter() }

    override val bindViews: FragmentContributorsBinding.() -> Unit = {
        recyclerContributors.adapter = adapter
    }

    override fun observeState(state: ContributorsState) {
        when (state) {
            is ContributorsState.ShowData -> {
                adapter.setData(state.data)
            }
        }
    }

    override fun showLoading() {
        super.showLoading()
        binding.progressBar.isVisible = true
    }

    override fun hideLoading() {
        super.hideLoading()
        binding.progressBar.isVisible = false
    }


}