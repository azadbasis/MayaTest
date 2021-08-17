package com.azharul.maya.view.search

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azharul.maya.R
import com.azharul.maya.service.model.Detail
import com.azharul.maya.service.model.TMDbSearchResponse
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.ConnectionLiveData
import com.azharul.maya.utils.Constants
import com.azharul.maya.utils.Constants.Companion.SEARCH_ITEM_DELAY
import com.azharul.maya.utils.Constants.Companion.SEARCH_MOVIE
import com.azharul.maya.utils.Constants.Companion.SEARCH_TV
import com.azharul.maya.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val TAG = "SearchFragment"
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchAdapter: SearchAdapter
    val searchItems = mutableListOf<TMDbSearchResponse>()
    private lateinit var connectionLiveData: ConnectionLiveData
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = activity?.let { ConnectionLiveData(it) }!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        connectionLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                dismissSnackBar()
                val repository = TMDbRepository()
                val viewModelFactory = SearchViewModelFactory(repository)
                searchViewModel =
                    ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
                searchAdapter.setOnItemClickListener {
                    if (it.movie) {
                        val bundle = Bundle().apply {
                            var detail = it.id?.let { it1 -> Detail(SEARCH_MOVIE, it1) }
                            putSerializable("detail", detail)
                        }
                        findNavController().navigate(
                            R.id.action_navigation_search_to_movieDetailFragment,
                            bundle
                        )
                    } else {
                        val bundle = Bundle().apply {
                            var detail = it.id?.let { it1 -> Detail(SEARCH_TV, it1) }
                            putSerializable("detail", detail)
                        }
                        findNavController().navigate(
                            R.id.action_navigation_search_to_tvDetailFragment,
                            bundle
                        )
                    }
                }
                searchViewModel.movieResponse.observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgressBar()
                            response.data?.let { movieResponse ->
                                //    movieAdapter.differ.submitList(movieResponse.results.toList())
                                val totalPages = movieResponse.total_pages
                                isLastPage = searchViewModel.moviePage == totalPages

                                movieResponse.results.forEach { movie ->
                                    searchItems.add(
                                        TMDbSearchResponse(
                                            movie.id,
                                            movie.original_title,
                                            movie.backdrop_path,
                                            movie.overview,
                                            true
                                        )
                                    )

                                }
                                searchAdapter.differ.submitList(searchItems)
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
                searchViewModel.tvResponse.observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgressBar()
                            response.data?.let { tvResponse ->
//                            movieAdapter.differ.submitList(movieResponse.results.toList())
                                val totalPages = tvResponse.total_pages
                                isLastPage = searchViewModel.tvPage == totalPages
                                Log.d(
                                    TAG,
                                    "onViewCreated: tv->" + tvResponse.results[0].original_name
                                )
                                tvResponse.results.forEach { tv ->
                                    searchItems.add(
                                        TMDbSearchResponse(
                                            tv.id,
                                            tv.original_name,
                                            tv.backdrop_path,
                                            tv.overview,
                                            false
                                        )
                                    )

                                }
                                searchAdapter.differ.submitList(searchItems)
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
        var job: Job? = null
        et_search.addTextChangedListener { text: Editable? ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_ITEM_DELAY)
                text?.let {
                    if (text.toString().isNotEmpty()) {
                        img_reset.visibility = View.VISIBLE
                        searchItems.clear()
                        searchAdapter.notifyDataSetChanged()
                        searchResult(text.toString())
                    } else {
                        img_reset.visibility = View.INVISIBLE
                        searchItems.clear()
                        searchAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        img_reset.setOnClickListener { v: View? ->
            et_search.setText("")
            searchItems.clear()
            searchAdapter.notifyDataSetChanged()
        }
    }

    private fun searchResult(toString: String) {
        connectionLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                dismissSnackBar()
                searchViewModel.searchMovie(toString)
                searchViewModel.searchTv(toString)
            } else {
                showSnackBar()
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgress.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgress.visibility = View.VISIBLE
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
                        searchViewModel.searchMovie("Me")
                        isScrolling = false
                    } else {
                        rvSearch.setPadding(0, 0, 0, 0)
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
        rvSearch.apply {
            rvSearch.layoutManager = LinearLayoutManager(activity)
            searchAdapter = SearchAdapter()
            adapter = searchAdapter
            addOnScrollListener(this@SearchFragment.scrollListener)
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