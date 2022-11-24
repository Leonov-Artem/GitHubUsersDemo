package com.training.android.githubusersdemo.helper

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.training.android.githubusersdemo.App
import com.training.android.githubusersdemo.R
import com.training.android.githubusersdemo.view.dialog.InfoDialogFragment
import com.training.android.githubusersdemo.view.dialog.SignInDialogFragment
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Instant

val Context.isInternetConnected: Boolean
    get() = when (this) {
        is App -> isInternetConnected
        else -> applicationContext.isInternetConnected
    }

val Context.dataStore by preferencesDataStore(name = "data store of access token")

val Response<*>.requestsLimit: Int
    get() = headers()["X-RateLimit-Limit"]!!.toInt()

val Response<*>.remainingRequests: Int
    get() = headers()["X-RateLimit-Remaining"]!!.toInt()

val Response<*>.resetEpochSecond: Int
    get() = headers()["X-RateLimit-Reset"]!!.toInt()

val Response<*>.isRequestsLimitExceeded: Boolean
    get() = remainingRequests == 0

val Response<*>.isAccessTokenInvalid: Boolean
    get() = code() == 401

val Response<*>.isNotModified: Boolean
    get() = code() == 304

val Response<*>.lastUpdateDatetime: String
    get() = headers()["Last-Modified"]!!

fun View.hideKeyboard() {
    val imm = ContextCompat.getSystemService(
        context,
        InputMethodManager::class.java
    ) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun minutesUntilLimitReset(resetEpochSecond: Int): String {
    val minutesUntilReset = (resetEpochSecond - Instant.now().epochSecond) / 60.0
    val decimalFormat = DecimalFormat("#.#").apply { roundingMode = RoundingMode.HALF_EVEN }
    return decimalFormat.format(minutesUntilReset)
}

//--------------------showSignInDialog & showInfoDialog--------------------

private fun FragmentManager.findFragmentByTagSync(tag: String?): Fragment? {
    try {
        executePendingTransactions()
    } catch (_: IllegalStateException) {

    }
    return findFragmentByTag(tag)
}

fun AppCompatActivity.showSignInDialog(message: String) {
    val tag = SignInDialogFragment.TAG
    supportFragmentManager.apply {
        findFragmentByTagSync(tag)?.let { return }
        SignInDialogFragment.newInstance(message).show(this, tag)
    }
}

fun AppCompatActivity.showSignInDialog(messageResId: Int) {
    showSignInDialog(getString(messageResId))
}

fun AppCompatActivity.showInfoDialog(message: String) {
    val tag = InfoDialogFragment.TAG
    supportFragmentManager.apply {
        findFragmentByTagSync(tag)?.let { return }
        InfoDialogFragment.newInstance(message).show(this, tag)
    }
}

fun AppCompatActivity.showInfoDialog(messageResId: Int) {
    showInfoDialog(getString(messageResId))
}

fun Fragment.showInfoDialog(message: String) {
    val activity = requireActivity()
    if (activity is AppCompatActivity) {
        activity.showInfoDialog(message)
    }
}

fun Fragment.showInfoDialog(messageResId: Int) {
    val activity = requireActivity()
    if (activity is AppCompatActivity) {
        activity.showInfoDialog(messageResId)
    }
}

//--------------------addMenu & removeMenu--------------------

fun Activity.addMenu() {
    if (this is ComponentActivity && this is MenuProvider) {
        this.addMenuProvider(this)
    }
}

fun Activity.removeMenu() {
    if (this is ComponentActivity && this is MenuProvider) {
        this.removeMenuProvider(this)
    }
}

fun Fragment.addMenu() {
    context?.let {
        if (it is ComponentActivity && this is MenuProvider) {
            it.addMenuProvider(this)
        }
    }
}

fun Fragment.removeMenu() {
    context?.let {
        if (it is ComponentActivity && this is MenuProvider) {
            it.removeMenuProvider(this)
        }
    }
}

//--------------------showActionBarBackButton & hideActionBarBackButton--------------------

private fun Activity.showActionBarBackButton(showHomeAsUp: Boolean) {
    if (this is AppCompatActivity) {
        this.supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
    }
}

fun Activity.showActionBarBackButton() {
    showActionBarBackButton(true)
}

fun Activity.hideActionBarBackButton() {
    showActionBarBackButton(false)
}

fun Fragment.showActionBarBackButton() {
    requireActivity().showActionBarBackButton()
}

fun Fragment.hideActionBarBackButton() {
    requireActivity().hideActionBarBackButton()
}

//--------------------setActionBarTitle & resetActionBarTitle--------------------

fun Activity.setActionBarTitle(title: String?) {
    title?.let { this.title = it }
}

fun Activity.setActionBarTitle(titleResId: Int) {
    setActionBarTitle(getString(titleResId))
}

fun Activity.resetActionBarTitle() {
    setActionBarTitle(R.string.app_name)
}

fun Fragment.setActionBarTitle(title: String?) {
    requireActivity().setActionBarTitle(title)
}

fun Fragment.resetActionBarTitle() {
    requireActivity().resetActionBarTitle()
}

//--------------------showLongMessage & showShortMessage--------------------

fun Activity.showLongMessage(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.showShortMessage(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showShortMessage(messageResId: Int) {
    showShortMessage(getString(messageResId))
}

fun Fragment.showShortMessage(message: String?) {
    requireActivity().showShortMessage(message)
}

fun Fragment.showLongMessage(message: String?) {
    requireActivity().showLongMessage(message)
}