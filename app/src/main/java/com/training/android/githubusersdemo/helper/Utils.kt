package com.training.android.githubusersdemo.helper

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.training.android.githubusersdemo.R

object Utils {

    class Network private constructor(private val context: Context) {

        val isInternetConnected: Boolean
            get() = context.isInternetConnected

        companion object {
            private var INSTANCE: Network? = null

            fun initialize(context: Context) {
                if (INSTANCE == null) {
                    INSTANCE = Network(context)
                }
            }

            fun getInstance(): Network {
                return INSTANCE ?: throw IllegalStateException("Network must be initialize first")
            }
        }
    }

    fun loadImage(url: String, into: ImageView) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(into)
    }

    private fun disableBackButton(view: View) {
        view.apply {
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener { v, keyCode, event -> keyCode == KeyEvent.KEYCODE_BACK }
        }
    }
}

