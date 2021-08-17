package com.azharul.maya.view.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azharul.maya.service.model.TMDbMovieResponse
import com.azharul.maya.service.model.TMDbTvResponse
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchViewModel(private val repository: TMDbRepository) : ViewModel() {

    val movieResponse: MutableLiveData<Resource<TMDbMovieResponse>> = MutableLiveData()
    var moviePage = 1
    var tmDbMovieResponse: TMDbMovieResponse? = null

    val tvResponse: MutableLiveData<Resource<TMDbTvResponse>> = MutableLiveData()
    var tvPage = 1
    var tmDbTvResponse: TMDbTvResponse? = null


    fun searchMovie(search: String) {
        viewModelScope.launch {
            movieResponse.postValue(Resource.Loading())
            val response = repository.searchMovie(search, moviePage)
            movieResponse.postValue(handleMovieResponse(response))
        }
    }

    fun searchTv(search: String) {
        viewModelScope.launch {
            movieResponse.postValue(Resource.Loading())
            val response = repository.searchTv(search, moviePage)
            tvResponse.postValue(handleTvResponse(response))
        }
    }

    private fun handleMovieResponse(response: Response<TMDbMovieResponse>): Resource<TMDbMovieResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                moviePage++
                if (tmDbMovieResponse == null) {
                    tmDbMovieResponse = resultResponse
                } else {
                    val oldArticles = tmDbMovieResponse?.results
                    val newArticles = resultResponse.results
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(tmDbMovieResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleTvResponse(response: Response<TMDbTvResponse>): Resource<TMDbTvResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                tvPage++
                if (tmDbTvResponse == null) {
                    tmDbTvResponse = resultResponse
                } else {
                    val oldArticles = tmDbTvResponse?.results
                    val newArticles = resultResponse.results
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(tmDbTvResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}