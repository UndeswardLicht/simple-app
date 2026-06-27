package com.example.mymvi

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private val editedCategories = mutableMapOf<Int, Category>()

    fun getEditedCategories(): List<Category> {
        val current = currentList.toMutableList()
        editedCategories.forEach { (id, category) ->
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                current[index] = category
            }
        }
        return current
    }

    fun clearEditMode() {
        editedCategories.clear()
        notifyDataSetChanged()
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentLayout: LinearLayout = view.findViewById(R.id.layout_category_content)
        val titleText: TextView = view.findViewById(R.id.text_category_title)
        val cashbackText: TextView = view.findViewById(R.id.text_category_cashback)
        val editTitle: EditText = view.findViewById(R.id.edit_category_title)
        val editCashback: EditText = view.findViewById(R.id.edit_category_cashback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        val displayCategory = editedCategories[category.id] ?: category

        holder.titleText.visibility = View.VISIBLE
        holder.cashbackText.visibility = View.VISIBLE
        holder.editTitle.visibility = View.GONE
        holder.editCashback.visibility = View.GONE

        // Remove old text watchers before setting new text
        holder.editTitle.removeTextChangedListener(holder.editTitle.tag as? TextWatcher)
        holder.editCashback.removeTextChangedListener(holder.editCashback.tag as? TextWatcher)

        holder.titleText.text = displayCategory.title
        holder.cashbackText.text = "Cashback: ${displayCategory.cashBack}"

        holder.editTitle.setText(displayCategory.title)
        holder.editCashback.setText(displayCategory.cashBack)

        // Set background colors based on category title (matching the image)
        val bgColor = when (category.title.uppercase()) {
            "REGULAR" -> R.color.white
            "PREMIUM" -> R.color.light_cyan // We might need to add this
            "VIP" -> R.color.light_yellow // We might need to add this
            else -> R.color.white
        }

        try {
            holder.contentLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, bgColor))
        } catch (e: Exception) {
            holder.contentLayout.setBackgroundColor(if (category.title.uppercase() == "REGULAR") 0xFFFFFFFF.toInt() else 0xFFE0F7FA.toInt())
        }

        holder.itemView.setOnClickListener {
            if (holder.editTitle.visibility == View.GONE) {
                holder.titleText.visibility = View.GONE
                holder.cashbackText.visibility = View.GONE
                holder.editTitle.visibility = View.VISIBLE
                holder.editCashback.visibility = View.VISIBLE
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val newCategory = category.copy(
                    title = holder.editTitle.text.toString(),
                    cashBack = holder.editCashback.text.toString()
                )
                editedCategories[category.id] = newCategory
            }
        }

        // Store reference to remove later
        holder.editTitle.tag = textWatcher
        holder.editCashback.tag = textWatcher

        holder.editTitle.addTextChangedListener(textWatcher)
        holder.editCashback.addTextChangedListener(textWatcher)
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem == newItem
    }
}
