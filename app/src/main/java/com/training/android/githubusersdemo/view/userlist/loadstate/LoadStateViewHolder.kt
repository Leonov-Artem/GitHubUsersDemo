package com.training.android.githubusersdemo.view.userlist.loadstate

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.training.android.githubusersdemo.databinding.LoadStateItemBinding

private val TAG = LoadStateViewHolder::class.java.simpleName

class LoadStateViewHolder(
    private val binding: LoadStateItemBinding,
    retry: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMessage.text =
                loadState.error.localizedMessage ?: "something went wrong..."
        }

        binding.apply {
            errorMessage.isVisible = loadState is LoadState.Error
            progressBar.isVisible = loadState is LoadState.Loading
            retryButton.isVisible = loadState is LoadState.Error
        }
    }
}