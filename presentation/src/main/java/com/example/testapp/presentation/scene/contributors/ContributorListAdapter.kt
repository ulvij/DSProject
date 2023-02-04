package com.example.testapp.presentation.scene.contributors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.domain.model.Contributor
import com.example.testapp.presentation.databinding.ItemContributorBinding

class ContributorListAdapter : RecyclerView.Adapter<ContributorListItemViewHolder>() {

    private val items = arrayListOf<Contributor>()

    fun setData(data: List<Contributor>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContributorListItemViewHolder {
        val view =
            ItemContributorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContributorListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContributorListItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

}