package com.example.swipeassignment.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swipeassignment.R
import com.example.swipeassignment.domain.ProductItem
import com.example.swipeassignment.domain.Result
import com.example.swipeassignment.ui.home.events.Events
import com.example.swipeassignment.ui.home.adapters.HomeAdapter
import com.example.swipeassignment.ui.home.viewModels.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeAdapter
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var tryAgainButton: TextView
    private lateinit var errorIcon:ImageView
    private lateinit var addButton:FloatingActionButton
    private lateinit var searchField: TextInputEditText
    private val viewModel by sharedViewModel<HomeViewModel>()
    val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.homeFragRecyclerView)
        progressBar = view.findViewById<CircularProgressIndicator>(R.id.homeFragProgressBar)
        tryAgainButton = view.findViewById<TextView>(R.id.homeFragTryAgainButton)
        errorIcon = view.findViewById<ImageView>(R.id.homeFragErrorIcon)
        addButton = view.findViewById<FloatingActionButton>(R.id.homeFragAddButton)
        searchField = view.findViewById<TextInputEditText>(R.id.homeFragSearchEditText)


        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = HomeAdapter(requireActivity())
        adapter.updateData(listOf())
        recyclerView.adapter = adapter


        tryAgainButton.setOnClickListener{
            viewModel.onEvent(Events.FetchDataAgain)
        }


        addButton.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_homeBottomSheetFragment)
        }


        searchField.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onEvent(Events.search(searchField.text.toString()))
                v.clearFocus()
                true
            } else {
                false
            }

        }



        //Observing Network connectivity
        viewModel.connectivityStatus.observe(viewLifecycleOwner, Observer { status->
            Log.d(TAG, "onCreateView: connectivity: "+status)
            Snackbar.make(requireActivity(),view, "Internet: $status",Snackbar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show()
        })



        //Observe products live data
        viewModel.products.observe(viewLifecycleOwner, Observer {response->
            Log.d(TAG, "onCreateView: products observer called")
            //Handle Success, Loading and Errors
          when(response){
              is Result.Loading->{
                if(!progressBar.isVisible){
                    progressBar.visibility = CircularProgressIndicator.VISIBLE
                }
              }
              is Result.Success->{
                  if(progressBar.isVisible){
                      progressBar.visibility = CircularProgressIndicator.GONE
                  }
                  Log.d(TAG, "onCreateView: response data = "+response.data)
                  adapter.updateData(response.data!!)

              }
              is Result.Error->{
                  if(progressBar.isVisible){
                      progressBar.visibility = CircularProgressIndicator.GONE
                  }
                  errorIcon.visibility = ImageView.VISIBLE
                  tryAgainButton.visibility = TextView.VISIBLE


              }
              else->{
                  Log.d(TAG, "onCreateView: No Matching result from API")
              }
          }
        })


        return view
    }

}