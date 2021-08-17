package com.azharul.maya.view.tv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.azharul.maya.R
import com.azharul.maya.databinding.ItemTvCellBinding
import com.azharul.maya.service.model.TVResults


class TvAdapter : RecyclerView.Adapter<TvAdapter.TvViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<TVResults>() {
        override fun areItemsTheSame(oldItem: TVResults, newItem: TVResults): Boolean {
            return oldItem.backdrop_path == newItem.backdrop_path
        }

        override fun areContentsTheSame(oldItem: TVResults, newItem: TVResults): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvViewHolder {
        return TvViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_tv_cell,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((TVResults) -> Unit)? = null

    override fun onBindViewHolder(holder: TvViewHolder, position: Int) {
        val tv = differ.currentList[position]
        holder.itemTvCellBinding.tv = differ.currentList[position]
        holder.itemTvCellBinding.root.setOnClickListener {
            onItemClickListener?.let { it(tv) }
        }
    }

    class TvViewHolder(val itemTvCellBinding: ItemTvCellBinding) :
        RecyclerView.ViewHolder(itemTvCellBinding.root)

    fun setOnItemClickListener(listener: (TVResults) -> Unit) {
        onItemClickListener = listener
    }
}













