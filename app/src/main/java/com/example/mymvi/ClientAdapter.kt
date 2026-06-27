package com.example.mymvi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ClientAdapter (
    private val onItemClick: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : ListAdapter<Client, ClientAdapter.ClientViewHolder>(ClientDiffCallback()) {

    class ClientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.text_client_name)
        val emailText: TextView = view.findViewById(R.id.text_client_email)
        val deleteButton: Button = view.findViewById(R.id.button_delete)
        val root: View = view.findViewById(R.id.client_item_root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = getItem(position)
        holder.nameText.text = client.name
        holder.emailText.text = client.email

        holder.root.setOnClickListener {
            onItemClick(client.id)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(client.id)
        }
    }

    private class ClientDiffCallback : DiffUtil.ItemCallback<Client>() {
        override fun areItemsTheSame(oldItem: Client, newItem: Client): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Client, newItem: Client): Boolean = oldItem == newItem
    }
}
