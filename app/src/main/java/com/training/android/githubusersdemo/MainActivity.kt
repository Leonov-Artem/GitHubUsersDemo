package com.training.android.githubusersdemo

import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.training.android.githubusersdemo.helper.minutesUntilLimitReset
import com.training.android.githubusersdemo.helper.showInfoDialog
import com.training.android.githubusersdemo.helper.showShortMessage
import com.training.android.githubusersdemo.helper.showSignInDialog
import com.training.android.githubusersdemo.model.data.source.remote.GitHubApi
import com.training.android.githubusersdemo.view.authuser.AuthorizedUserFragment
import com.training.android.githubusersdemo.view.authuser.AuthorizedUserFragmentDirections
import com.training.android.githubusersdemo.view.dialog.SignInDialogFragment
import com.training.android.githubusersdemo.view.userdetails.UserDetailsFragment
import com.training.android.githubusersdemo.view.userdetails.UserDetailsFragmentDirections
import com.training.android.githubusersdemo.view.userlist.UserListFragment
import com.training.android.githubusersdemo.view.userlist.UserListFragmentDirections
import com.training.android.githubusersdemo.viewmodel.mainactivity.MainActivityViewModel
import com.training.android.githubusersdemo.viewmodel.mainactivity.MainActivityViewModelFactory

private val TAG = MainActivity::class.java.simpleName

class MainActivity :
    AppCompatActivity(R.layout.activity_main),
    UserListFragment.Callbacks,
    UserDetailsFragment.Callbacks,
    AuthorizedUserFragment.Callbacks,
    SignInDialogFragment.NoticeDialogListener {

    private lateinit var navController: NavController
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(applicationContext)
    }

    private val signInActivityResultCallback = ActivityResultCallback<ActivityResult> {
        when (it.resultCode) {
            RESULT_OK -> {
                onSuccessfulSignIn()
            }
        }
    }

    private val signInActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        signInActivityResultCallback,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavController()
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    private fun onSuccessfulSignIn() {
        navController.currentDestination?.let { currentDestination ->
            when (currentDestination.id) {
                R.id.user_list_fragment -> {
                    navigateToAuthorizedUserFromUserList()
                }
                R.id.user_details_fragment -> {
                    navigateToAuthorizedUserFromUserDetails()
                }
            }
        }
    }

    override fun onSignInButtonClick() {
        launchSignInActivity()
    }

    override fun onSignOutButtonClick() {
        navigateToUserListFromAuthorizedUser()
    }

    //--------------------status handlers-----------------------------------------------------------

    override fun onInvalidAccessToken() {
        viewModel
            .clearAuthorizedUserCache()
            .invokeOnCompletion {
                navController.currentDestination?.let { currentDestination ->
                    if (currentDestination.id == R.id.authorized_user_fragment) {
                        navigateToUserListFromAuthorizedUser()
                    }
                }
                showSignInDialog(R.string.dialog_message_access_token_is_invalid)
            }
    }

    override fun onDefaultRequestLimitExceeded(resetEpochSecond: Int) {
        showSignInDialog(
            message = getString(
                R.string.dialog_message_default_request_limit_exceeded,
                GitHubApi.DEFAULT_REQUEST_LIMIT,
                minutesUntilLimitReset(resetEpochSecond),
                GitHubApi.AUTHORIZED_USER_REQUEST_LIMIT,
            )
        )
    }

    override fun onAuthorizedUserRequestLimitExceeded(resetEpochSecond: Int) {
        showInfoDialog(
            message = getString(
                R.string.dialog_message_authorized_user_request_limit_exceeded,
                GitHubApi.AUTHORIZED_USER_REQUEST_LIMIT,
                minutesUntilLimitReset(resetEpochSecond)
            )
        )
    }

    override fun onUnavailableInternetConnection() {
        showShortMessage(R.string.no_internet_connection)
    }

    //----------------------------------------------------------------------------------------------

    override fun showUserDetails(login: String) {
        navigateToUserDetailsFromUserList(login)
    }

    override fun onMyProfileMenuItemClick() {
        navigateToAuthorizedUserFromUserList()
    }

    override fun onSignInMenuItemClick() {
        launchSignInActivity()
    }

    private fun initNavController() {
        val fragmentContainer =
            supportFragmentManager.findFragmentById(R.id.main_activity_fragment_container)
        navController = (fragmentContainer as NavHostFragment).navController
    }

    private fun navigateToUserDetailsFromUserList(login: String) {
        val directions = UserListFragmentDirections
        val toUserDetails = directions.actionUserListFragmentToUserDetailsFragment(login)
        navController.navigate(toUserDetails)
    }

    private fun navigateToAuthorizedUserFromUserList() {
        val directions = UserListFragmentDirections
        val toAuthorizedUserProfile = directions.actionUserListFragmentToAuthorizedUserFragment()
        navController.navigate(toAuthorizedUserProfile)
    }

    private fun navigateToAuthorizedUserFromUserDetails() {
        val directions = UserDetailsFragmentDirections
        val toAuthorizedUserProfile = directions.actionUserDetailsFragmentToAuthorizedUserFragment()
        navController.navigate(toAuthorizedUserProfile)
    }

    private fun navigateToUserListFromAuthorizedUser() {
        val directions = AuthorizedUserFragmentDirections
        val toUserList = directions.actionAuthorizedUserFragmentToUserListFragment()
        navController.navigate(toUserList)
    }

    private fun launchSignInActivity() {
        val signInActivityIntent = SignInActivity.newIntent(context = this)
        signInActivityLauncher.launch(signInActivityIntent)
    }
}