package com.tushant.swipe.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromListToJson(list: List<String>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToList(json: String?): List<String>? {
        return Gson().fromJson(json, object : TypeToken<List<String>>() {}.type)
    }
}