package com.konkuk.kureal.home.article

import android.content.Context
import android.view.LayoutInflater
import android.view.View
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
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it,position)
        }
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

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