package com.adi121.statussaverupdate.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.adi121.statussaverupdate.R
import com.adi121.statussaverupdate.databinding.CategorySingleItemBinding
import com.adi121.statussaverupdate.utils.Category


class CategoriesAdapter(val context:Context, private val onCategoryClicked: OnCategoryClicked): RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder>() {

    var rowIndex=0

    private val differCallback= object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.url==newItem.url
        }
    }


    val differ= AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesHolder {
        val binding= CategorySingleItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoriesHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesHolder, position: Int) {
        holder.bind(differ.currentList[position])

        holder.binding.root.setOnClickListener {
            rowIndex=position
            notifyDataSetChanged()
            onCategoryClicked.onCatCLicked(differ.currentList[position])
        }

        if(rowIndex==0){
            onCategoryClicked.onCatCLicked(differ.currentList[0])
        }

        if(rowIndex==position){
            holder.binding.viewIndicator.setBackgroundColor(context.resources.getColor(R.color.colorText))
            //holder.binding.categoryName.setTextColor(ContextCompat.getColor(context,R.color.colorBlack))
        }else{

            holder.binding.viewIndicator.setBackgroundColor(context.resources.getColor(R.color.backGroundColor))
            //holder.binding.categoryName.setTextColor(ContextCompat.getColor(context,R.color.colorWhite))
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner  class CategoriesHolder( val binding: CategorySingleItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(category: Category){
            binding.apply {
                tvCatName.text=category.name
                Glide.with(ivCatImage)
                    .load(category.url)
                    .timeout(60000)
                    .into(ivCatImage)
            }
        }
    }

    interface OnCategoryClicked{
        fun onCatCLicked(category: Category)
    }

}