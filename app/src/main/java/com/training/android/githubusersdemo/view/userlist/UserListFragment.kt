package com.training.android.githubusersdemo.view.userlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.training.android.githubusersdemo.R
import com.training.android.githubusersdemo.databinding.FragmentUserListBinding
import com.training.android.githubusersdemo.helper.Status
import com.training.android.githubusersdemo.helper.addMenu
import com.training.android.githubusersdemo.helper.removeMenu
import com.training.android.githubusersdemo.view.StatusCallbacks
import com.training.android.githubusersdemo.view.userlist.loadstate.LoadStateAdapter
import com.training.android.githubusersdemo.viewmodel.userlist.UserListViewModel
import com.training.android.githubusersdemo.viewmodel.userlist.UserListViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private val CLASS_NAME = UserListFragment::class.java.simpleName
private val INTERFACE_NAME = UserListFragment.Callbacks::class.java.simpleName
private val TAG = CLASS_NAME

class UserListFragment :
    Fragment(R.layout.fragment_user_list),
    MenuProvider {

    interface Callbacks : StatusCallbacks {
        fun showUserDetails(login: String)
        fun onMyProfileMenuItemClick()
        fun onSignInMenuItemClick()
    }

    private lateinit var binding: FragmentUserListBinding
    private lateinit var signInMenuItem: MenuItem
    private lateinit var myProfileMenuItem: MenuItem
    private lateinit var adapter: UserPagingDataAdapter
    private lateinit var callbacks: Callbacks
    private val viewModel: UserListViewModel by viewModels {
        UserListViewModelFactory(requireContext())
    }

    private val isMenuItemsInitialized: Boolean
        get() = ::signInMenuItem.isInitialized && ::myProfileMenuItem.isInitialized

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks
        } catch (e: ClassCastException) {
            val contextName = context::class.java.simpleName
            throw ClassCastException("$contextName must implement $CLASS_NAME.$INTERFACE_NAME")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addMenu()
        binding = FragmentUserListBinding.inflate(layoutInflater, container, false)
        adapter = UserPagingDataAdapter(callbacks)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setUpSwipeToRefresh()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_user_list, menu)
        initializeMenuItems(menu)
        updateMenu()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.my_profile_menu_item -> {
                callbacks.onMyProfileMenuItemClick()
                true
            }
            R.id.sign_in_menu_item -> {
                callbacks.onSignInMenuItemClick()
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setUpSwipeToRefresh() {
        binding.userListSwipeRefreshLayout.apply {

            setOnRefreshListener {
                adapter.refresh()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    isRefreshing = loadStates.mediator?.refresh is LoadState.Loading
                }
            }
        }
    }

    private fun setUpAdapter() {
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collectLatest {
                adapter.submitData(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            Log.d(TAG, "launchWhenResumed")
            adapter.retry()

            adapter.loadStateFlow
                .filter { it.refresh is LoadState.Error || it.append is LoadState.Error }
                .collectLatest {
                    val loadState = it.refresh as? LoadState.Error ?: it.append as LoadState.Error
                    splitErrorMessage(loadState)?.let { (status, resetEpochSecond) ->
                        handleErrorStatus(status, resetEpochSecond)
                    }
                }
        }
    }

    private fun splitErrorMessage(loadState: LoadState.Error): Pair<Status, Int?>? {
        loadState.error.message?.split(" ")?.let { errorList ->
            val status = Status.valueOf(errorList.first())
            val resetEpochSecond = errorList.elementAtOrNull(index = 1)?.toInt()
            return Pair(first = status, second = resetEpochSecond)
        }
        return null
    }

    private fun handleErrorStatus(status: Status, resetEpochSecond: Int?) {
        when (status) {
            Status.DEFAULT_REQUEST_LIMIT_EXCEEDED -> {
                callbacks.onDefaultRequestLimitExceeded(resetEpochSecond!!)
            }
            Status.AUTHORIZED_USER_REQUEST_LIMIT_EXCEEDED -> {
                callbacks.onAuthorizedUserRequestLimitExceeded(resetEpochSecond!!)
            }
            Status.NO_INTERNET_CONNECTION -> {
                callbacks.onUnavailableInternetConnection()
            }
            Status.ACCESS_TOKEN_IS_INVALID -> {
                showSignInMenuItem()
                callbacks.onInvalidAccessToken()
            }
            else -> {

            }
        }
    }

    private fun showSignInMenuItem() {
        signInMenuItem.isVisible = true
        myProfileMenuItem.isVisible = false
    }

    private fun initializeMenuItems(menu: Menu) {
        signInMenuItem = menu.findItem(R.id.sign_in_menu_item)
        myProfileMenuItem = menu.findItem(R.id.my_profile_menu_item)
    }

    private fun updateMenu() {
        if (!isMenuItemsInitialized) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val isUserAuthorized = viewModel.isUserAuthorized()
            signInMenuItem.isVisible = !isUserAuthorized
            myProfileMenuItem.isVisible = isUserAuthorized
        }
    }
}