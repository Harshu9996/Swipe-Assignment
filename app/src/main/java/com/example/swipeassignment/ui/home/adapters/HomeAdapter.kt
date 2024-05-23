package com.example.swipeassignment.ui.home.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.swipeassignment.R
import com.example.swipeassignment.domain.ProductItem


class HomeAdapter(private val context: Context) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

        private val dataList:MutableList<ProductItem> = mutableListOf()

        val TAG = "HomeAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_product_list_item,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        // Tells the size of the data list
      return dataList.size
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProductItem = dataList[position]
        holder.productName.setText(currentProductItem.product_name.toString())
        holder.productType.setText(currentProductItem.product_type)
        holder.productPrice.text = "Price: Rs.${currentProductItem.price}"
        holder.productTax.text = "Tax: Rs.${currentProductItem.tax}"


        holder.progressBar.visibility = ProgressBar.VISIBLE
        //Use Glide to fetch image
        var imageUrl = currentProductItem.image
        if(imageUrl.isEmpty() || imageUrl.isBlank()){
            //Do not use glide if url is empty.
            holder.productImage.setImageDrawable(context.getDrawable(R.drawable.baseline_image_24))
        }else{
            Glide.with(context)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.baseline_image_24) // Placeholder image
                        .error(R.drawable.baseline_image_24) // Error image in case of loading failure
                    //In our case both placeholder and error image are same
                )
                .dontAnimate()
                .into(holder.productImage)
        }
        holder.progressBar.visibility = ProgressBar.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list:List<ProductItem>){

        if(list.size>=dataList.size){
            //New Product added. Handle accordingly
            val newItems = list - dataList
            this.dataList.addAll(newItems)
        }else{
            //List has been filtered
            this.dataList.clear()
            this.dataList.addAll(list)
        }
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productImage = itemView.findViewById<ImageView>(R.id.homeProductImage)
        val productName = itemView.findViewById<TextView>(R.id.homeProductName)
        val productType = itemView.findViewById<TextView>(R.id.homeProductType)
        val productPrice = itemView.findViewById<TextView>(R.id.homeProductPrice)
        val productTax = itemView.findViewById<TextView>(R.id.homeProductTax)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.homeProgressBar)

    }
}