package com.azharul.maya.view.movie


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.azharul.maya.R
import com.azharul.maya.databinding.FragmentMovieDetailBinding
import com.azharul.maya.service.model.Detail
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.BindGenreUtil
import com.azharul.maya.utils.ConnectionLiveData
import com.azharul.maya.utils.Constants.Companion.SEARCH_MOVIE
import com.azharul.maya.utils.Resource
import com.google.android.material.snackbar.Snackbar


class MovieDetailFragment : Fragment() {

    private val TAG = "MovieDetailFragment"
    private lateinit var detail: Detail
    private lateinit var viewModel: MovieDetailViewModel
    private lateinit var connectionLiveData: ConnectionLiveData
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        connectionLiveData = activity?.let { ConnectionLiveData(it) }!!
        arguments?.let { bundle ->
            detail = (bundle.getSerializable("detail") as Detail?)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMovieDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false)
        connectionLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                dismissSnackBar()
                val repository = TMDbRepository()
                val viewModelFactory = MovieDetailViewModelFactory(repository)
                viewModel =
                    ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel::class.java)
                viewModel.getMovieDetail(detail.id)
                viewModel.movieDetailResponse.observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.let { movieResponse ->
                                val genreName = BindGenreUtil.genreName(movieResponse)
                                binding.detail = movieResponse
                                binding.genreName = genreName
                            }
                        }
                        is Resource.Error -> {
                            response.message?.let { message ->
                                Log.e(TAG, "An error occurred: $message")
                            }
                        }
                        is Resource.Loading -> {
                        }
                    }
                })
            } else {
                showSnackBar()
            }
        })
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home ->
                if (detail.name.equals(SEARCH_MOVIE)) {
                    findNavController().navigate(R.id.action_movieDetailFragment_to_navigation_search)
                } else {
                    findNavController().navigate(R.id.action_movieDetailFragment_to_navigation_movie)
                }
        }
        return true
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