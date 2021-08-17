package com.azharul.maya.view.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.azharul.maya.service.repository.TMDbRepository

class MovieDetailViewModelFactory(private val repository:TMDbRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieDetailViewModel(repository)as T
    }
}