package com.example.myapplication
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList


class UrlDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertUrl(url: UrlModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UrlEntry.COLUMN_URL_ID, url.urlId)
        values.put(DBContract.UrlEntry.COLUMN_URL, url.url)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.UrlEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUrl(urlId: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.UrlEntry.COLUMN_URL_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(urlId)
        // Issue SQL statement.
        db.delete(DBContract.UrlEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteDB(): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
        return true
    }
    @SuppressLint("Range")
    fun readUser(urlId: String): ArrayList<UrlModel> {
        val urls = ArrayList<UrlModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UrlEntry.TABLE_NAME + " WHERE " + DBContract.UrlEntry.COLUMN_URL_ID + "='" + urlId + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var url: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                url = cursor.getString(cursor.getColumnIndex(DBContract.UrlEntry.COLUMN_URL))

                urls.add(UrlModel(urlId, url))
                cursor.moveToNext()
            }
        }
        return urls
    }

    @SuppressLint("Range")
    fun readAllUrls(): ArrayList<UrlModel> {
        val users = ArrayList<UrlModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UrlEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var urlid: String
        var url: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                urlid = cursor.getString(cursor.getColumnIndex(DBContract.UrlEntry.COLUMN_URL_ID))
                url = cursor.getString(cursor.getColumnIndex(DBContract.UrlEntry.COLUMN_URL))
                users.add(UrlModel(urlid, url))
                cursor.moveToNext()
            }
        }
        return users
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "UrlReader.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.UrlEntry.TABLE_NAME + " (" +
                    DBContract.UrlEntry.COLUMN_URL_ID + " TEXT PRIMARY KEY," +
                    DBContract.UrlEntry.COLUMN_URL + " TEXT" + " )"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UrlEntry.TABLE_NAME
    }

}