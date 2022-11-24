package com.training.android.githubusersdemo.view.authuser

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.training.android.githubusersdemo.databinding.FragmentAuthorizedUserBinding
import com.training.android.githubusersdemo.helper.*
import com.training.android.githubusersdemo.model.entity.AuthorizedUser
import com.training.android.githubusersdemo.view.StatusCallbacks
import com.training.android.githubusersdemo.viewmodel.authuser.AuthorizedUserViewModel
import com.training.android.githubusersdemo.viewmodel.authuser.AuthorizedUserViewModelFactory

private val CLASS_NAME = AuthorizedUserFragment::class.java.simpleName
private val INTERFACE_NAME = AuthorizedUserFragment.Callbacks::class.java.simpleName
private val TAG = CLASS_NAME

class AuthorizedUserFragment : Fragment() {

    interface Callbacks : StatusCallbacks {
        fun onSignOutButtonClick()
    }

    private lateinit var callbacks: Callbacks
    private lateinit var binding: FragmentAuthorizedUserBinding
    private val viewModel: AuthorizedUserViewModel by viewModels {
        AuthorizedUserViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUser()
    }

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizedUserBinding.inflate(layoutInflater, container, false)
        showActionBarBackButton()
        setUpSignOutButton()
        setUpSwipeRefresh()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.authorizedUserLiveData.observe(viewLifecycleOwner) { result ->
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
                    val resetEpochSecond = result.data!! as Int
                    callbacks.onDefaultRequestLimitExceeded(resetEpochSecond)
                }
                Status.AUTHORIZED_USER_REQUEST_LIMIT_EXCEEDED -> {
                    val resetEpochSecond = result.data!! as Int
                    callbacks.onAuthorizedUserRequestLimitExceeded(resetEpochSecond)
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

    override fun onDestroy() {
        super.onDestroy()
        resetActionBarTitle()
        hideActionBarBackButton()
    }

    //--------------------status handlers-----------------------------------------------------------

    private fun handleLoadingStatus() {
        Log.d(TAG, "LOADING")
    }

    private fun handleSuccessStatus(result: Result<*>) {
        val authorizedUser = result.data!! as AuthorizedUser
        binding.user = authorizedUser
        Utils.loadImage(
            url = authorizedUser.avatarUrl,
            into = binding.imageView,
        )
        setActionBarTitle(authorizedUser.login)
    }

    private fun handleErrorStatus(result: Result<*>) {
        showLongMessage(result.message)
    }

    private fun handleNotLoadingStatus() {
        Log.d(TAG, "NOT_LOADING")
        binding.swipeRefresh.isRefreshing = false
    }

    //----------------------------------------------------------------------------------------------

    private fun setUpSignOutButton() {
        binding.signOutButton.setOnClickListener {
            viewModel
                .signOut()
                .invokeOnCompletion {
                    callbacks.onSignOutButtonClick()
                }
        }
    }

    private fun setUpSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshUser()
        }
    }
}