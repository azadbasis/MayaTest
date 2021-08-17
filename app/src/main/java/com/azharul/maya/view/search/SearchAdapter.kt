package com.azharul.maya.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.azharul.maya.R
import com.azharul.maya.databinding.ItemSearchCellBinding
import com.azharul.maya.service.model.TMDbSearchResponse


class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<TMDbSearchResponse>() {
        override fun areItemsTheSame(
            oldItem: TMDbSearchResponse,
            newItem: TMDbSearchResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TMDbSearchResponse,
            newItem: TMDbSearchResponse
        ): Boolean {
            return oldItem.mImage == newItem.mImage
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_search_cell,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((TMDbSearchResponse) -> Unit)? = null

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.itemSearchCellBinding.searchItem = differ.currentList[position]
        holder.itemSearchCellBinding.root.setOnClickListener {
            onItemClickListener?.let { it(movie) }
        }
    }

    class SearchViewHolder(val itemSearchCellBinding: ItemSearchCellBinding) :
        RecyclerView.ViewHolder(itemSearchCellBinding.root)

    fun setOnItemClickListener(listener: (TMDbSearchResponse) -> Unit) {
        onItemClickListener = listener
    }
}













