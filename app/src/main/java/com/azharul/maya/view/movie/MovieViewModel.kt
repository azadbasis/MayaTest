package com.azharul.maya.view.movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azharul.maya.service.model.TMDbMovieResponse
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class MovieViewModel(private val repository: TMDbRepository) : ViewModel() {

    private  val TAG = "MovieViewModel"
    val movieResponse: MutableLiveData<Resource<TMDbMovieResponse>> = MutableLiveData()
    var moviePage = 1
    var tmDbMovieResponse: TMDbMovieResponse? = null

    init {
        getMovie()
    }

    fun getMovie() {
        viewModelScope.launch {
            movieResponse.postValue(Resource.Loading())
            val response = repository.getMovie(moviePage)
            movieResponse.postValue(handleMovieResponse(response))
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
}