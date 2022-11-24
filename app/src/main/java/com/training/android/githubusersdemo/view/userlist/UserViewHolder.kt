package com.training.android.githubusersdemo.view.userlist

import androidx.recyclerview.widget.RecyclerView
import com.training.android.githubusersdemo.databinding.UserListItemBinding
import com.training.android.githubusersdemo.helper.Utils
import com.training.android.githubusersdemo.model.entity.UserListItem

class UserViewHolder(
    private val binding: UserListItemBinding,
    private val callbacks: UserListFragment.Callbacks,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: UserListItem?) {
        user?.let {
            binding.user = it
            binding.callbacks = callbacks
            Utils.loadImage(
                url = it.avatarUrl,
                into = binding.avatarImageView,
            )
        }
    }
}