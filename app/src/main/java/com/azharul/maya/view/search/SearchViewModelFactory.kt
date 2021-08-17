package com.azharul.maya.view.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.azharul.maya.service.repository.TMDbRepository

class SearchViewModelFactory(private val repository:TMDbRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(repository)as T
    }
}