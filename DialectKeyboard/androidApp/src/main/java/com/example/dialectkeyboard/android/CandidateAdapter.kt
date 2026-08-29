package com.example.dialectkeyboard.android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dialectkeyboard.DialectEntry

class CandidateAdapter(
    private val onCandidateClick: (DialectEntry) -> Unit
) : RecyclerView.Adapter<CandidateAdapter.ViewHolder>() {

    private val candidates = mutableListOf<DialectEntry>()

    fun updateCandidates(newCandidates: List<DialectEntry>) {
        candidates.clear()
        candidates.addAll(newCandidates)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_candidate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = candidates[position]
        holder.tvWord.text = item.word
        holder.tvDescription.text = item.description
        holder.itemView.setOnClickListener {
            onCandidateClick(item)
        }
    }

    override fun getItemCount(): Int = candidates.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWord: TextView = view.findViewById(R.id.tv_candidate_word)
        val tvDescription: TextView = view.findViewById(R.id.tv_candidate_desc)
    }
}