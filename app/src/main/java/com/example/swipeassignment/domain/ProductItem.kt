package com.example.swipeassignment.domain

import com.example.swipeassignment.data.roomDB.entities.RoomProductItem

data class ProductItem(
    val image: String,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double
){


    //Function used for filtering the list of ProductItem according to the given query
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            product_name,
            "${product_name.first()}",
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }


    //Convert this dataclass object into RoomProductItem object which is stored in room and has an id extra
    fun toRoomProductItem(id:Int?): RoomProductItem{
        return RoomProductItem(
            id = id,
            product_name = product_name,
            product_type = product_type,
            price = price,
            tax = tax,
            image = image
        )
    }


}