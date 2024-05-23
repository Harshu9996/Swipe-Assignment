package com.example.swipeassignment.data.roomDB.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swipeassignment.domain.ProductItem

@Entity(tableName = "products")
data class RoomProductItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val image: String,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double
){

    //Function to convert RoomProductItem object in to ProductItem object to be used in elsewhere in the app outside room
    fun toProductItem():ProductItem{
        return ProductItem(
            image = image,
            product_name = product_name,
            product_type = product_type,
            price = price,
            tax = tax
        )
    }
}
