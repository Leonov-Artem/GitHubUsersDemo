package com.training.android.githubusersdemo.view.userlist.loadstate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.training.android.githubusersdemo.databinding.LoadStateItemBinding

class LoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LoadStateItemBinding.inflate(layoutInflater, parent, false)
        return LoadStateViewHolder(binding, retry)
    }
}