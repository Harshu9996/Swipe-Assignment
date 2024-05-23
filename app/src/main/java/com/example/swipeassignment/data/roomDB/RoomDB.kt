package com.example.swipeassignment.data.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.swipeassignment.data.roomDB.entities.RoomProductItem

@Database(entities = [RoomProductItem::class], version = 1, exportSchema = false)
abstract class RoomDB : RoomDatabase() {
    abstract fun getDao() : RoomDao
}