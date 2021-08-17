package com.azharul.maya.view.tv

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azharul.maya.R
import com.azharul.maya.service.model.Detail
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.ConnectionLiveData
import com.azharul.maya.utils.Constants
import com.azharul.maya.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_movie.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_tv.*

class TvFragment : Fragment(R.layout.fragment_tv) {

    private val TAG = "TvFragment"
    private lateinit var viewModel: TvViewModel
    private lateinit var tvAdapter: TvAdapter
    private lateinit var connectionLiveData: ConnectionLiveData
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = activity?.let { ConnectionLiveData(it) }!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                dismissSnackBar()
                val repository = TMDbRepository()
                val viewModelFactory = TvViewModelFactory(repository)
                viewModel = ViewModelProvider(this, viewModelFactory).get(TvViewModel::class.java)
                viewModel.getTv()
                viewModel.tvResponse.observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgressBar()
                            response.data?.let { tvResponse ->
                                tvAdapter.differ.submitList(tvResponse.results.toList())
                                val totalPages = tvResponse.total_pages
                                isLastPage = viewModel.tvPage == totalPages
                            }
                        }

                        is Resource.Error -> {
                            hideProgressBar()
                            response.message?.let { message ->
                                Toast.makeText(
                                    activity,
                                    "An error occurred: $message",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                        is Resource.Loading -> {
                            showProgressBar()
                        }
                    }

                })
            } else {
                showSnackBar()
            }
        })

        setupRecyclerView()
        tvAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                var detail = Detail("Tv", it.id)
                putSerializable("detail", detail)
            }
            findNavController().navigate(R.id.action_navigation_tv_to_tvDetailFragment, bundle)
        }

    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            connectionLiveData.observe(viewLifecycleOwner, Observer {
                if (it) {
                    dismissSnackBar()
                    if (shouldPaginate) {
                        viewModel.getTv()
                        isScrolling = false
                    } else {
                        rvTv.setPadding(0, 0, 0, 0)
                    }
                } else {
                    showSnackBar()
                }
            })

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        rvTv.apply {
            rvTv.layoutManager = LinearLayoutManager(activity)
            tvAdapter = TvAdapter()
            adapter = tvAdapter
            addOnScrollListener(this@TvFragment.scrollListener)
        }
    }

    private fun showSnackBar() {
        view?.let {
            snackbar =
                Snackbar.make(it, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
            snackbar?.show()
        }
    }

    private fun dismissSnackBar() {
        snackbar?.dismiss()
    }
}