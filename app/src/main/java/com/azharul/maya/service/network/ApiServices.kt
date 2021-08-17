package com.azharul.maya.service.network

import com.azharul.maya.service.model.TMDbMovieDetailResponse
import com.azharul.maya.service.model.TMDbMovieResponse
import com.azharul.maya.service.model.TMDbTvDetailResponse
import com.azharul.maya.service.model.TMDbTvResponse
import com.azharul.maya.service.network.ApiEndPoints.Companion.DISCOVER_MOVIE
import com.azharul.maya.service.network.ApiEndPoints.Companion.DISCOVER_TV
import com.azharul.maya.service.network.ApiEndPoints.Companion.SEARCH_MOVIE
import com.azharul.maya.service.network.ApiEndPoints.Companion.SEARCH_TV
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

    @GET(DISCOVER_MOVIE)
    suspend fun getMovie(@Query("page") pageNumber:Int): Response<TMDbMovieResponse>

    @GET(DISCOVER_TV)
    suspend fun getTv(@Query("page") pageNumber:Int): Response<TMDbTvResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): Response<TMDbMovieDetailResponse>

    @GET("tv/{tv_id}")
    suspend fun getTvDetail(@Path("tv_id") movieId: Int): Response<TMDbTvDetailResponse>

    @GET(SEARCH_MOVIE)
    suspend fun searchMovie(@Query("query") search: String,@Query("page") pageNumber:Int): Response<TMDbMovieResponse>

    @GET(SEARCH_TV)
    suspend fun searchTv(@Query("query") search: String,@Query("page") pageNumber:Int): Response<TMDbTvResponse>
}