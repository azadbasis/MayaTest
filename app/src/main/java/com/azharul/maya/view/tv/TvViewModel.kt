package com.azharul.maya.view.tv

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azharul.maya.service.model.TMDbTvResponse
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class TvViewModel(private val repository: TMDbRepository) : ViewModel() {

    val tvResponse: MutableLiveData<Resource<TMDbTvResponse>> = MutableLiveData()
    var tvPage = 1
    var tmDbTvResponse: TMDbTvResponse? = null

    init {
        getTv()
    }

    fun getTv() {
        viewModelScope.launch {
            tvResponse.postValue(Resource.Loading())
            val response = repository.getTv(tvPage)
            tvResponse.postValue(handleTvResponse(response))
        }
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