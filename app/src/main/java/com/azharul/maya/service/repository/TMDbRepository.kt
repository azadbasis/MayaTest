package com.azharul.maya.service.repository

import com.azharul.maya.service.model.TMDbMovieDetailResponse
import com.azharul.maya.service.model.TMDbMovieResponse
import com.azharul.maya.service.model.TMDbTvDetailResponse
import com.azharul.maya.service.model.TMDbTvResponse
import com.azharul.maya.service.network.ApiClient
import retrofit2.Response

class TMDbRepository {

    suspend fun getMovie(pageNumber:Int): Response<TMDbMovieResponse> {
        return ApiClient.api.getMovie(pageNumber)
    }
    suspend fun getTv(pageNumber:Int): Response<TMDbTvResponse> {
        return ApiClient.api.getTv(pageNumber)
    }
    suspend fun getMovieDetail(movieId:Int): Response<TMDbMovieDetailResponse> {
        return ApiClient.api.getMovieDetail(movieId)
    }
    suspend fun getTvDetail(tvId:Int): Response<TMDbTvDetailResponse> {
        return ApiClient.api.getTvDetail(tvId)
    }
    suspend fun searchTv(search:String,pageNumber:Int): Response<TMDbTvResponse> {
        return ApiClient.api.searchTv(search,pageNumber)
    }
    suspend fun searchMovie(search:String,pageNumber:Int): Response<TMDbMovieResponse> {
        return ApiClient.api.searchMovie(search,pageNumber)
    }
}