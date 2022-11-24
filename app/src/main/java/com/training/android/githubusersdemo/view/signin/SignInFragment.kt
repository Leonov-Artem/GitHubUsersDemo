package com.training.android.githubusersdemo.view.signin

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.training.android.githubusersdemo.R
import com.training.android.githubusersdemo.databinding.FragmentSignInBinding
import com.training.android.githubusersdemo.helper.addMenu
import com.training.android.githubusersdemo.helper.isInternetConnected
import com.training.android.githubusersdemo.helper.removeMenu
import com.training.android.githubusersdemo.helper.showInfoDialog
import com.training.android.githubusersdemo.model.data.source.remote.GitHubApi

private val CLASS_NAME = SignInFragment::class.java.simpleName
private val INTERFACE_NAME = SignInFragment.Callbacks::class.java.simpleName
private val TAG = CLASS_NAME

class SignInFragment : Fragment(), MenuProvider {

    interface Callbacks {
        fun onSignInButtonClick(login: String)
    }

    private lateinit var callbacks: Callbacks
    private lateinit var binding: FragmentSignInBinding

    private val isInternetConnected: Boolean
        get() = requireContext().isInternetConnected

    private val login: String
        get() = binding.loginEditText.text.toString()

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
        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpSignInButton()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_sign_in, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.info_menu_item -> {
                showInfoDialog(
                    message = getString(
                        R.string.dialog_message_about_authorization,
                        GitHubApi.AUTHORIZED_USER_REQUEST_LIMIT,
                        GitHubApi.DEFAULT_REQUEST_LIMIT
                    )
                )
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

    private fun setUpSignInButton() {
        binding.signInButton.setOnClickListener {
            if (!isInternetConnected) {
                showInfoDialog(R.string.no_internet_connection)
                return@setOnClickListener
            }

            callbacks.onSignInButtonClick(login)
        }
    }
}