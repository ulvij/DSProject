package com.example.testapp.presentation.scene.contributors

import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.domain.model.Contributor
import com.example.testapp.presentation.databinding.ItemContributorBinding
import com.example.testapp.presentation.utils.loadUrl

class ContributorListItemViewHolder(private val item: ItemContributorBinding) :
    RecyclerView.ViewHolder(item.root) {

    fun bind(contributor: Contributor) {
        item.imageAvatar.loadUrl(contributor.avatar_url)
        item.textUsername.text = contributor.login
    }
}