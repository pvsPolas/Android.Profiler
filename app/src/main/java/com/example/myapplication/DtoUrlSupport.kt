package com.example.myapplication
import com.google.gson.annotations.SerializedName
data class DtoUrlSupport (

    @SerializedName("id"  ) var int  : String? = null,
    @SerializedName("url" ) var text : String? = null

)