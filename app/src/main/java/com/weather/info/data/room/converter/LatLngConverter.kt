package com.weather.info.data.room.converter

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object LatLngConverter {

    @JvmStatic
    @TypeConverter
    fun stringToModel(json: String?): LatLng? {
        val gson = Gson()
        return gson.fromJson(json, object : TypeToken<LatLng?>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun modelToString(position: LatLng?): String? {
        val gson = Gson()
        return gson.toJson(position, object : TypeToken<LatLng?>() {}.type)
    }

}