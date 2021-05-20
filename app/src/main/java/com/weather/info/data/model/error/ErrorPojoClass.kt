package com.weather.info.data.model.error

import com.google.gson.annotations.SerializedName

data class ErrorPojoClass(

    @field:SerializedName("entityName")
    val entityName: String? = null,

    @field:SerializedName("errorCode")
    val errorCode: Int? = null,

    @field:SerializedName("title") //400
    val title: String? = null,

    @field:SerializedName("message") //400, All
    val message: String? = null,

    @field:SerializedName("status") //All
    val status: Int? = null,

    @field:SerializedName("path") // 401, 500
    val path: String? = null,

    @field:SerializedName("detail") // 401
    val detail: String? = null
)
