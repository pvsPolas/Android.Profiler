package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class DtoUrlData (

    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("url"      ) var url     : String? = null

)