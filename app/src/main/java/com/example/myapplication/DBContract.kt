package com.example.myapplication
import android.provider.BaseColumns
object DBContract {
    /* Inner class that defines the table contents */
    class UrlEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "urls"
            val COLUMN_URL_ID = "id"
            val COLUMN_URL = "url"
        }
    }
}