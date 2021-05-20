package com.weather.info.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by WelCome on 13-02-18.
 */
@Entity
data class History(

    @PrimaryKey(autoGenerate = true)
    val historyId: Int,

    @ColumnInfo(name = "id")
    @field:SerializedName("id")
    val userId: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("gender")
    val gender: String,

    @field:SerializedName("dateOfBirth")
    val dateOfBirth: String? = null,

    @field:SerializedName("mobileNo")
    val mobileNo: String,

    @field:SerializedName("aadharNo")
    val aadharNo: String,

    @field:SerializedName("password")
    var password: String? = null,

    @field:SerializedName("fatherName")
    val fatherName: String? = null,

    @field:SerializedName("gotra")
    val gotra: String? = null,

    @field:SerializedName("timeOfBirth")
    val timeOfBirth: String? = null,

    @field:SerializedName("birthPlace")
    val birthPlace: String? = null,

    @field:SerializedName("height")
    val height: Int? = null,

    @field:SerializedName("manglik")
    val manglik: String? = null,

    @field:SerializedName("marritalStatus")
    val marritalStatus: Int? = null,

    @field:SerializedName("profilePicture")
    val profilePicture: String? = null,

    @field:SerializedName("flatNo")
    val flatNo: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("education")
    val education: String? = null,

    @field:SerializedName("occupation")
    val occupation: String? = null,

    @field:SerializedName("annualIncome")
    val annualIncome: Int? = null,

    @field:SerializedName("companyName")
    val companyName: String? = null,

    @field:SerializedName("mamaName")
    val mamaName: String? = null,

    @field:SerializedName("mamaGotra")
    val mamaGotra: String? = null,

    @field:SerializedName("mamaAddress")
    val mamaAddress: String? = null,

    @field:SerializedName("brotherMarried")
    val brotherMarried: String? = null,

    @field:SerializedName("brotherUnMarried")
    val brotherUnMarried: String? = null,

    @field:SerializedName("sisterMarried")
    val sisterMarried: String? = null,

    @field:SerializedName("sisterUnMarried")
    val sisterUnMarried: String? = null,

    @field:SerializedName("isDisabledAccount")
    val isDisabledAccount: Int? = null,

    @field:SerializedName("deviceToken")
    val deviceToken: String? = null,


    /**
     * EXTRA PROPERTIES
     * */
    @field:SerializedName("about")
    val about: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("email_token")
    val emailToken: String? = null,

    @field:SerializedName("experience")
    val experience: String? = null,

    @field:SerializedName("last_activity_andriod")
    val lastActivityAndriod: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("device_id")
    val deviceId: String? = null,

    @field:SerializedName("idproof2")
    val idproof2: String? = null,

    @field:SerializedName("idproof1")
    val idproof1: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("ios_auth_token")
    val iosAuthToken: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_role")
    val userRole: Int? = null,

    @field:SerializedName("andriod_auth_token")
    val andriodAuthToken: String? = null,

    @field:SerializedName("mobile_ispublic")
    val mobileIspublic: String? = null,

    @field:SerializedName("is_verified")
    val isVerified: Int? = null

)
