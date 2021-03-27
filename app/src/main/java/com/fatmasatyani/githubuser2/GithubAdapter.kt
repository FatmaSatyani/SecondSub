package com.fatmasatyani.githubuser2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fatmasatyani.githubuser2.databinding.ListUserBinding

class GithubAdapter(private var list: ArrayList<Github>, private val clickListener: (Github) -> Unit) : RecyclerView.Adapter<GithubAdapter.GithubViewHolder>() {

    private val listGithub = ArrayList<Github> ()
    private var onItemClickCallback : OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData (filterList: ArrayList<Github>) {
        this.listGithub.clear()
        this.listGithub.addAll(filterList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_user,parent, false)
        return GithubViewHolder(view)
    }

    override fun onBindViewHolder(holder: GithubViewHolder, position: Int) {
        holder.bind(listGithub[position])
        }

    override fun getItemCount(): Int = listGithub.size

    inner class GithubViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ListUserBinding.bind(itemView)
        fun bind(githubItems: Github) {
            binding.textUserId.text = githubItems.username
            Glide.with(itemView.context)
                .load(githubItems.avatar)
                .apply(RequestOptions().override(460, 460))
                .into(binding.imgUserPhoto)

            binding.root.setOnClickListener {
                clickListener(githubItems)
            }

        }
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Github)
    }
}
