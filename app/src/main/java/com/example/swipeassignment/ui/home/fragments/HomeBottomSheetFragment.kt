package com.example.swipeassignment.ui.home.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.swipeassignment.R
import com.example.swipeassignment.domain.ProductItem
import com.example.swipeassignment.domain.Result
import com.example.swipeassignment.ui.home.events.Events
import com.example.swipeassignment.ui.home.viewModels.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeBottomSheetFragment : BottomSheetDialogFragment() {

    val TAG = "HomeBottomSheetFragment"
    val typeMenu = arrayOf("Service","Grocery","Electronics","Other","Clothing","Product")
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var productNameTextView: TextInputEditText
    private lateinit var productPriceTextView: TextInputEditText
    private lateinit var productTaxTextView: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var progressBar:LinearProgressIndicator
    val viewModel by sharedViewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home_bottom_sheet, container, false)

        autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.sheetProductType)
        productNameTextView = view.findViewById<TextInputEditText>(R.id.sheetProductName)
        productPriceTextView = view.findViewById<TextInputEditText>(R.id.sheetProductPrice)
        productTaxTextView = view.findViewById<TextInputEditText>(R.id.sheetProductTax)
        saveButton = view.findViewById<MaterialButton>(R.id.sheetSaveButton)
        progressBar = view.findViewById<LinearProgressIndicator>(R.id.sheetProgressBar)


        arrayAdapter = ArrayAdapter(requireActivity(),R.layout.bottom_sheet_drop_down_item_layout,typeMenu)
        autoCompleteTextView.setAdapter(arrayAdapter)




        viewModel.connectivityStatus.observe(viewLifecycleOwner, Observer { status->
            Log.d(TAG, "onCreateView: connectivity: "+status)
            Snackbar.make(requireActivity(),view, "Internet: $status", Snackbar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show()
        })

        //Handle Product Addition status and give feedback to user using dialog
        viewModel.addProductStatus.observe(viewLifecycleOwner, Observer {status->
            when(status){
                is Result.Loading->{
                    progressBar.visibility = LinearProgressIndicator.VISIBLE
                }
                is Result.Success->{
                    progressBar.visibility = LinearProgressIndicator.GONE

                    val message = status.data!!.product_details.product_name + " added successfully"
                    FeedbackDialog.newInstance(title = getString(R.string.alert_add_product_success) , text = message).show(
                        childFragmentManager,FeedbackDialog.TAG
                    )

                }
                is Result.Error->{
                    progressBar.visibility = LinearProgressIndicator.GONE

                    val message = getString(R.string.alert_error)
                    val title = "Adding product failed"
                    FeedbackDialog.newInstance(title = title , text = message).show(
                        childFragmentManager,FeedbackDialog.TAG
                    )

                }
                else->{

                    progressBar.visibility = LinearProgressIndicator.GONE
                    val message = "Adding "+status.data!!.product_details.product_name + " failed."
                    FeedbackDialog.newInstance(title = getString(R.string.alert_error) , text = message).show(
                        childFragmentManager,FeedbackDialog.TAG
                    )
                }
            }
        })




        saveButton.setOnClickListener {
            //Check If all field data are entered by users or not
            //Validate fields
            if(productNameTextView.text.isNullOrEmpty() || productPriceTextView.text.isNullOrEmpty() || productTaxTextView.text.isNullOrEmpty() ||
                autoCompleteTextView.text.isNullOrEmpty()){
                //Showing a Toast message
                Toast.makeText(requireActivity(),R.string.toast_alert_empty_fields,Toast.LENGTH_SHORT).show()
            }else{


                //Validate decimal values
                val price = productPriceTextView.text.toString().toDoubleOrNull()
                val tax = productTaxTextView.text.toString().toDoubleOrNull()
                if(price==null){
                    Toast.makeText(requireActivity(),R.string.ask_valid_price,Toast.LENGTH_SHORT).show()
                }else if(tax == null){
                    Toast.makeText(requireActivity(),R.string.ask_valid_tax,Toast.LENGTH_SHORT).show()
                }else{
                    //Save the product
                    val productItem = ProductItem(image = "", price =
                    price,
                        product_name = productNameTextView.text.toString(),
                        product_type = autoCompleteTextView.text.toString(),
                        tax = tax)
                    viewModel.onEvent(Events.AddProduct(productItem))
                }

            }
        }
        return view
    }


}