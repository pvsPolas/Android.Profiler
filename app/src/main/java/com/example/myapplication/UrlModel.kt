package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable

class UrlModel(val urlId: String?, val url: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(urlId)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UrlModel> {
        override fun createFromParcel(parcel: Parcel): UrlModel {
            return UrlModel(parcel)
        }

        override fun newArray(size: Int): Array<UrlModel?> {
            return arrayOfNulls(size)
        }
    }
}