package com.theshan.codeai.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theshan.codeai.databinding.ItemChatMessageBinding
import com.theshan.codeai.ui.ChatMessage
import com.theshan.codeai.ui.ResultActivity

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun updateLastMessage(message: ChatMessage) {
        if (messages.isNotEmpty()) {
            messages[messages.lastIndex] = message
            notifyItemChanged(messages.lastIndex)
        }
    }

    fun submitList(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            // User message
            binding.tvUserMessage.text = message.userMessage

            // AI response
            if (message.isLoading) {
                binding.aiMessageLayout.visibility = View.VISIBLE
                binding.tvAiResponse.text = "🤔 Generating..."
                binding.btnViewCode.visibility = View.GONE
            } else if (message.aiResponse != null) {
                binding.aiMessageLayout.visibility = View.VISIBLE
                binding.tvAiResponse.text = message.aiResponse

                if (message.hasCode && message.fullCode != null) {
                    binding.btnViewCode.visibility = View.VISIBLE
                    binding.btnViewCode.setOnClickListener {
                        val ctx = itemView.context
                        val intent = Intent(ctx, ResultActivity::class.java).apply {
                            putExtra("CODE", message.fullCode)
                        }
                        ctx.startActivity(intent)
                    }
                } else {
                    binding.btnViewCode.visibility = View.GONE
                }
            } else {
                binding.aiMessageLayout.visibility = View.GONE
            }
        }
    }
}
