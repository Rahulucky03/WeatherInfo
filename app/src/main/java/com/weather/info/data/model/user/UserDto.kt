package com.weather.info.data.model.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserDto(

    @field:Expose
    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:Expose
    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:Expose
    @field:SerializedName("id")
    val id: Int? = null,
)

