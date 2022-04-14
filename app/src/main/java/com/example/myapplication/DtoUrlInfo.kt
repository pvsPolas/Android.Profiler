package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class DtoUrlInfo (
    @SerializedName("data"        ) var data       : ArrayList<DtoUrlData> = arrayListOf()
)