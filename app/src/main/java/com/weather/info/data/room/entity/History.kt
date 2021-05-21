package com.weather.info.data.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.weather.info.data.room.converter.LatLngConverter
import kotlinx.parcelize.Parcelize

/**
 * Created by WelCome on 13-02-18.
 */
@Parcelize
@Entity
@TypeConverters(LatLngConverter::class)
data class History(
    @PrimaryKey(autoGenerate = true)
    val historyId: Int,

    @ColumnInfo(name = "latLng")
    @field:SerializedName("latLng")
    val latLng: LatLng,

    @ColumnInfo(name = "address")
    @field:SerializedName("address")
    val address: String
) : Parcelable
