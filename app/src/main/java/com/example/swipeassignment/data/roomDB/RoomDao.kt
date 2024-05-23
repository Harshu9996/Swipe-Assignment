package com.example.swipeassignment.data.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.swipeassignment.data.roomDB.entities.RoomProductItem


@Dao
interface RoomDao {
    //Data Access Object for accessing room database
    @Insert
    suspend fun insert(products:List<RoomProductItem>)

    @Insert
    suspend fun insert(productItem: RoomProductItem)

    @Query("SELECT * FROM products")
    suspend fun getProducts() : List<RoomProductItem>
}