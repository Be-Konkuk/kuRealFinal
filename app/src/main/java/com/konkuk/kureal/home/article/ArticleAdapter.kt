package com.konkuk.kureal.home.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.kureal.databinding.ItemArticleBinding

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.MyViewHolder>() {
    val articleList = mutableListOf<ArticleInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleAdapter.MyViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleAdapter.MyViewHolder(binding)
    }

    override fun getItemCount(): Int = articleList.size

    override fun onBindViewHolder(holder: ArticleAdapter.MyViewHolder, position: Int) {
        holder.onBind(articleList[position])
    }

    class MyViewHolder(
        private val binding : ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun onBind(articleInfo: ArticleInfo){
            binding.tvDate.text = articleInfo.date
            binding.tvTitle.text = articleInfo.article
            binding.tvNickname.text = articleInfo.nickname
        }
    }
}