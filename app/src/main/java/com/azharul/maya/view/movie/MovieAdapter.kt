package com.azharul.maya.view.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.azharul.maya.R
import com.azharul.maya.databinding.ItemMovieCellBinding
import com.azharul.maya.service.model.Results


class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<Results>() {
        override fun areItemsTheSame(oldItem: Results, newItem: Results): Boolean {
            return oldItem.backdrop_path == newItem.backdrop_path
        }

        override fun areContentsTheSame(oldItem: Results, newItem: Results): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_movie_cell,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Results) -> Unit)? = null

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.itemMovieCellBinding.movie = differ.currentList[position]
        holder.itemMovieCellBinding.root.setOnClickListener {
            onItemClickListener?.let { it(movie) }
        }
    }

    class MovieViewHolder(val itemMovieCellBinding: ItemMovieCellBinding) :
        RecyclerView.ViewHolder(itemMovieCellBinding.root)

    fun setOnItemClickListener(listener: (Results) -> Unit) {
        onItemClickListener = listener
    }

}













