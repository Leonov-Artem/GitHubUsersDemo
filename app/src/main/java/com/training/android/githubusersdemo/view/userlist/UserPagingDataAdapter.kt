package com.training.android.githubusersdemo.view.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.training.android.githubusersdemo.databinding.UserListItemBinding
import com.training.android.githubusersdemo.model.entity.UserListItem


class UserPagingDataAdapter(
    private val callbacks: UserListFragment.Callbacks,
) : PagingDataAdapter<UserListItem, UserViewHolder>(USER_COMPARATOR) {

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = UserListItemBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding, callbacks)
    }

    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<UserListItem>() {
            override fun areItemsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}