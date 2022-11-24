package com.training.android.githubusersdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.training.android.githubusersdemo.helper.hideActionBarBackButton
import com.training.android.githubusersdemo.helper.resetActionBarTitle
import com.training.android.githubusersdemo.helper.setActionBarTitle
import com.training.android.githubusersdemo.helper.showActionBarBackButton
import com.training.android.githubusersdemo.view.signin.SignInFragment
import com.training.android.githubusersdemo.view.signin.SignInFragmentDirections
import com.training.android.githubusersdemo.view.signin.WebViewFragment
import com.training.android.githubusersdemo.view.signin.WebViewFragmentDirections

private val TAG = SignInActivity::class.java.simpleName

class SignInActivity :
    AppCompatActivity(R.layout.activity_sign_in),
    SignInFragment.Callbacks,
    WebViewFragment.Callbacks {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavController()
        setActionBarTitle(R.string.sign_in)
        showActionBarBackButton()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        navController.currentDestination?.let { currentDestination ->
            when (currentDestination.id) {
                R.id.sign_in_fragment -> {
                    finish()
                }
                R.id.web_view_fragment -> {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resetActionBarTitle()
        hideActionBarBackButton()
    }

    override fun onSignInButtonClick(login: String) {
        navigateToWebViewFromSignIn(login)
    }

    override fun onSuccessfulSignIn() {
        setResult(RESULT_OK)
        finish()
    }

    override fun onWebViewClosing() {
        navigateToSignInFromWebView()
    }

    private fun initNavController() {
        val fragmentContainer =
            supportFragmentManager.findFragmentById(R.id.sign_in_activity_fragment_container)
        navController = (fragmentContainer as NavHostFragment).navController
    }

    private fun navigateToSignInFromWebView() {
        val directions = WebViewFragmentDirections
        val toSignInFragment = directions.actionWebViewFragmentToSignInFragment()
        navController.navigate(toSignInFragment)
    }

    private fun navigateToWebViewFromSignIn(login: String) {
        val directions = SignInFragmentDirections
        val toWebViewFragment = directions.actionSignInFragmentToWebViewFragment(login)
        navController.navigate(toWebViewFragment)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SignInActivity::class.java)
    }
}