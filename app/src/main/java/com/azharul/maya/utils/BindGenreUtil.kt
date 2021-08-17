package com.azharul.maya.utils

import com.azharul.maya.R
import com.azharul.maya.service.model.GenreName
import com.azharul.maya.service.model.TMDbMovieDetailResponse
import com.azharul.maya.service.model.TMDbTvDetailResponse
import com.google.android.material.snackbar.Snackbar

object BindGenreUtil {

    fun genreName(movieResponse: TMDbMovieDetailResponse): GenreName {
        val arrayList = ArrayList<String>()
        movieResponse.genres.forEach {
            arrayList.add(it.name)
        }
        val stringOfNames = arrayList.joinToString(", ")
        val genreName = GenreName(stringOfNames)
        return genreName
    }

    fun genreNameTv(movieResponse: TMDbTvDetailResponse): GenreName {
        val arrayList = ArrayList<String>()
        movieResponse.genres.forEach {
            arrayList.add(it.name)
        }
        val stringOfNames = arrayList.joinToString(", ")
        val genreName = GenreName(stringOfNames)
        return genreName
    }
}