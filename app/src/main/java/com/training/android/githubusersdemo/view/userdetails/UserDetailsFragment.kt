package com.training.android.githubusersdemo.view.userdetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.training.android.githubusersdemo.databinding.FragmentUserDetailsBinding
import com.training.android.githubusersdemo.helper.*
import com.training.android.githubusersdemo.model.entity.UserListItem
import com.training.android.githubusersdemo.view.StatusCallbacks
import com.training.android.githubusersdemo.viewmodel.userdetails.UserDetailsViewModel
import com.training.android.githubusersdemo.viewmodel.userdetails.UserDetailsViewModelFactory

private val CLASS_NAME = UserDetailsFragment::class.java.simpleName
private val INTERFACE_NAME = UserDetailsFragment.Callbacks::class.java.simpleName
private val TAG = CLASS_NAME

class UserDetailsFragment : Fragment() {

    interface Callbacks : StatusCallbacks

    private lateinit var callbacks: Callbacks
    private val args: UserDetailsFragmentArgs by navArgs()
    private val viewModel: UserDetailsViewModel by viewModels {
        UserDetailsViewModelFactory(requireContext(), args.login)
    }
    private lateinit var binding: FragmentUserDetailsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks
        } catch (e: ClassCastException) {
            val contextName = context::class.java.simpleName
            throw ClassCastException("$contextName must implement $CLASS_NAME.$INTERFACE_NAME")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeToRefresh()
        setUpRetryButton()
        observeUserData()
    }

    override fun onResume() {
        super.onResume()
        setActionBarTitle(args.login)
        showActionBarBackButton()
        viewModel.loadUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetActionBarTitle()
        hideActionBarBackButton()
    }

    private fun observeUserData() {
        viewModel.userLiveData.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.LOADING -> {
                    handleLoadingStatus()
                }
                Status.SUCCESS -> {
                    handleSuccessStatus(result)
                }
                Status.ERROR -> {
                    handleErrorStatus(result)
                }
                Status.DEFAULT_REQUEST_LIMIT_EXCEEDED -> {
                    callbacks.onDefaultRequestLimitExceeded(resetEpochSecond = result.data!! as Int)
                }
                Status.AUTHORIZED_USER_REQUEST_LIMIT_EXCEEDED -> {
                    callbacks.onAuthorizedUserRequestLimitExceeded(resetEpochSecond = result.data!! as Int)
                }
                Status.NO_INTERNET_CONNECTION -> {
                    callbacks.onUnavailableInternetConnection()
                }
                Status.ACCESS_TOKEN_IS_INVALID -> {
                    callbacks.onInvalidAccessToken()
                }
                Status.NOT_LOADING -> {
                    handleNotLoadingStatus()
                }
            }
        }
    }

    //--------------------status handlers-----------------------------------------------------------

    private fun handleLoadingStatus() {
        Log.d(TAG, "LOADING")
        binding.apply {
            progressBar.visibility = if (swipeRefresh.isRefreshing) View.GONE else View.VISIBLE
            retryButton.visibility = View.GONE
            userDetailsLayout.visibility = View.GONE
        }
    }

    private fun handleSuccessStatus(result: Result<*>) {
        Log.d(TAG, "SUCCESS")
        val user = result.data!! as UserListItem
        binding.apply {
            this.user = user
            Utils.loadImage(
                url = user.avatarUrl,
                into = imageView
            )
            userDetailsLayout.visibility = View.VISIBLE
            retryButton.visibility = View.GONE
        }
    }

    private fun handleErrorStatus(result: Result<*>) {
        Log.d(TAG, "ERROR")
        showLongMessage(result.message)
        binding.apply {
            retryButton.visibility = View.VISIBLE
            userDetailsLayout.visibility = View.GONE
        }
    }

    private fun handleNotLoadingStatus() {
        Log.d(TAG, "NOT_LOADING")
        binding.apply {
            progressBar.visibility = View.GONE
            swipeRefresh.isRefreshing = false
        }
    }

    //----------------------------------------------------------------------------------------------

    private fun setUpSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshUser()
        }
    }

    private fun setUpRetryButton() {
        binding.retryButton.setOnClickListener {
            viewModel.refreshUser()
        }
    }
}