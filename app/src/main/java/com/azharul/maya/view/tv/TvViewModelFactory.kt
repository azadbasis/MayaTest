package com.azharul.maya.view.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.azharul.maya.service.repository.TMDbRepository

class TvViewModelFactory(private val repository:TMDbRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TvViewModel(repository)as T
    }
}