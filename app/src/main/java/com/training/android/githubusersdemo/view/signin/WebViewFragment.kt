package com.training.android.githubusersdemo.view.signin

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.training.android.githubusersdemo.R
import com.training.android.githubusersdemo.databinding.FragmentWebViewBinding
import com.training.android.githubusersdemo.helper.*
import com.training.android.githubusersdemo.model.data.source.remote.providers.OAuthApiProvider
import com.training.android.githubusersdemo.viewmodel.signin.WebViewFragmentViewModel
import com.training.android.githubusersdemo.viewmodel.signin.WebViewFragmentViewModelFactory
import kotlinx.coroutines.launch

private val CLASS_NAME = WebViewFragment::class.java.simpleName
private val INTERFACE_NAME = WebViewFragment.Callbacks::class.java.simpleName
private val TAG = CLASS_NAME

private const val CODE_QUERY_PARAM = "code"
private const val ERROR_QUERY_PARAM = "error"
private const val ACCESS_DENIED_PARAM_VALUE = "access_denied"

class WebViewFragment : Fragment(), MenuProvider {

    interface Callbacks {
        fun onSuccessfulSignIn()
        fun onWebViewClosing()
    }

    private lateinit var callbacks: Callbacks
    private lateinit var binding: FragmentWebViewBinding

    private val args: WebViewFragmentArgs by navArgs()
    private val viewModel: WebViewFragmentViewModel by viewModels {
        WebViewFragmentViewModelFactory(requireContext())
    }
    private val signInUrl: String
        get() = OAuthApiProvider.SignInUrl.create(args.login)

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
        addMenu()
        binding = FragmentWebViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpControls()
        loadSignInUrl()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_web_view, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.close_webview_menu_item -> {
                callbacks.onWebViewClosing()
                true
            }
            R.id.reload_webview_menu_item -> {
                reloadCurrentPage()
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

    private fun setUpControls() {
        setUpBackButton()
        setUpPageLoadingProgressBar()
        setUpWebView()
    }

    private fun setUpPageLoadingProgressBar() {
        binding.pageLoadingProgressBar.apply {
            max = 100
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        val overriddenWebViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                webResource: WebResourceRequest?
            ): Boolean {
                webResource?.let {
                    handleSignInResult(it.url)
                    // Returning false means that you are going to load this url in the webView itself
                    return false
                }
                return super.shouldOverrideUrlLoading(view, webResource)
            }
        }

        val overriddenWebChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.pageLoadingProgressBar.apply {
                    if (newProgress != 100) {
                        visibility = View.VISIBLE
                        progress = newProgress
                        return
                    }

                    visibility = View.GONE
                }
            }
        }

        binding.webView.apply {
            webViewClient = overriddenWebViewClient
            webChromeClient = overriddenWebChromeClient
            settings.javaScriptEnabled = true
        }
    }

    private fun handleSignInResult(url: Uri) {
        Log.d(TAG, "url = $url")

        url.getQueryParameter(CODE_QUERY_PARAM)?.let { temporaryCode ->
            exchangeTempCodeForAccessToken(temporaryCode)
            return
        }
        url.getQueryParameter(ERROR_QUERY_PARAM)?.let { error ->
            if (error == ACCESS_DENIED_PARAM_VALUE) {
                callbacks.onWebViewClosing()
                showInfoDialog(R.string.access_denied)
            }
            return
        }
    }

    private fun exchangeTempCodeForAccessToken(temporaryCode: String) {
        lifecycleScope.launch {
            viewModel.requestAccessToken(temporaryCode).collect { result ->
                when (result.status) {
                    Status.LOADING -> {
                        handleLoadingStatus()
                    }
                    Status.SUCCESS -> {
                        handleSuccessStatus(result)
                    }
                    Status.ERROR -> {
                        handleErrorStatus()
                    }
                    Status.NOT_LOADING -> {
                        handleNotLoadingStatus()
                    }
                    else -> {
                        Log.d(TAG, "как я вообще сюда попал?")
                    }
                }
            }
        }
    }

    //--------------------status handlers-----------------------------------------------------------

    private fun handleLoadingStatus() {
        Log.d(TAG, "LOADING")
        binding.apply {
            receivingTokenProgressBar.visibility = View.VISIBLE
            webViewLayout.visibility = View.GONE
        }
    }

    private fun handleSuccessStatus(result: Result<String>) {
        Log.d(TAG, "SUCCESS, accessToken = ${result.data}")
        viewModel
            .loadAuthorizedUserInfo(accessToken = result.data!!)
            .invokeOnCompletion {
                callbacks.onSuccessfulSignIn()
            }
    }

    private fun handleErrorStatus() {
        Log.d(TAG, "ERROR")
        binding.retryButton.apply {
            visibility = View.VISIBLE

            setOnClickListener {
                visibility = View.GONE
                binding.webViewLayout.visibility = View.VISIBLE
                loadSignInUrl()
            }
        }
    }

    private fun handleNotLoadingStatus() {
        Log.d(TAG, "NOT_LOADING")
        binding.receivingTokenProgressBar.visibility = View.GONE
    }

    //----------------------------------------------------------------------------------------------

    private fun setUpBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback {
            binding.webView.apply {
                if (canGoBack()) {
                    goBack()
                } else {
                    remove()
                    callbacks.onWebViewClosing()
                }
            }
        }
    }

    private fun loadSignInUrl() = binding.webView.loadUrl(signInUrl)

    private fun reloadCurrentPage() = binding.webView.reload()
}