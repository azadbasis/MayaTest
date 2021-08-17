package com.azharul.maya.service.model

import java.io.Serializable

class TMDbSearchResponse(
    val id: Int?,
    val name: String?,
    val mImage: String?,
    val overview: String?,
    val movie: Boolean,
) : Serializable