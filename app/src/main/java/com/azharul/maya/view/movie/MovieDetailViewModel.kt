package com.azharul.maya.view.movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azharul.maya.service.model.TMDbMovieDetailResponse
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class MovieDetailViewModel(private val repository: TMDbRepository) : ViewModel() {

    val movieDetailResponse: MutableLiveData<Resource<TMDbMovieDetailResponse>> = MutableLiveData()
    var tmDbMovieDetailResponse: TMDbMovieDetailResponse? = null

    fun getMovieDetail(movieId:Int) {
        viewModelScope.launch {
            movieDetailResponse.postValue(Resource.Loading())
            val response = repository.getMovieDetail(movieId)
            movieDetailResponse.postValue(handleMovieResponse(response))
        }
    }

    private fun handleMovieResponse(response: Response<TMDbMovieDetailResponse>): Resource<TMDbMovieDetailResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { responseResult ->
                if (tmDbMovieDetailResponse == null) {
                    tmDbMovieDetailResponse = responseResult
                }
                return Resource.Success(tmDbMovieDetailResponse ?: responseResult)
            }
        }
        return Resource.Error(response.message())
    }
}