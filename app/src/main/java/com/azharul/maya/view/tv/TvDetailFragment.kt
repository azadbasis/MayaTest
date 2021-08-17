package com.azharul.maya.view.tv


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
import com.azharul.maya.databinding.FragmentTvDetailBinding
import com.azharul.maya.service.model.Detail
import com.azharul.maya.service.repository.TMDbRepository
import com.azharul.maya.utils.BindGenreUtil
import com.azharul.maya.utils.ConnectionLiveData
import com.azharul.maya.utils.Constants.Companion.SEARCH_TV
import com.azharul.maya.utils.Resource
import com.google.android.material.snackbar.Snackbar


class TvDetailFragment : Fragment() {

    private val TAG = "MovieDetailFragment"
    private lateinit var detail: Detail
    private lateinit var viewModel: TvDetailViewModel
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
        val binding: FragmentTvDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tv_detail, container, false)
        connectionLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                dismissSnackBar()
                val repository = TMDbRepository()
                val viewModelFactory = TvDetailViewModelFactory(repository)
                viewModel =
                    ViewModelProvider(this, viewModelFactory).get(TvDetailViewModel::class.java)
                viewModel.getMovieDetail(detail.id)
                viewModel.tvDetailResponse.observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.let { tvResponse ->
                                val genreName = BindGenreUtil.genreNameTv(tvResponse)
                                binding.detail = tvResponse
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
                if (detail.name.equals(SEARCH_TV)) {
                    findNavController().navigate(R.id.action_tvDetailFragment_to_navigation_search)
                } else {
                    findNavController().navigate(R.id.action_tvDetailFragment_to_navigation_tv)
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