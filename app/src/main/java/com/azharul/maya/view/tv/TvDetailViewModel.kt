package com.azharul.maya.view.tv

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azharul.maya.service.model.TMDbTvDetailResponse
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class TvDetailViewModel(private val repository: TMDbRepository) : ViewModel() {

    val tvDetailResponse: MutableLiveData<Resource<TMDbTvDetailResponse>> = MutableLiveData()
    var tmDbTvDetailResponse: TMDbTvDetailResponse? = null

    fun getMovieDetail(tvId: Int) {
        viewModelScope.launch {
            tvDetailResponse.postValue(Resource.Loading())
            val response = repository.getTvDetail(tvId)
            tvDetailResponse.postValue(handleMovieResponse(response))
        }
    }

    private fun handleMovieResponse(response: Response<TMDbTvDetailResponse>): Resource<TMDbTvDetailResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { responseResult ->
                if (tmDbTvDetailResponse == null) {
                    tmDbTvDetailResponse = responseResult
                }
                return Resource.Success(tmDbTvDetailResponse ?: responseResult)
            }
        }
        return Resource.Error(response.message())
    }
}