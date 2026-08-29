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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textCandidate: TextView = view.findViewById(android.R.id.text1)
        val textRegion: TextView? = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = candidates[position]
        holder.textCandidate.text = entry.word
        holder.textRegion?.text = if (entry.region.isNotEmpty()) entry.region else ""
        holder.itemView.setOnClickListener {
            onCandidateClick(entry)
        }
    }

    override fun getItemCount(): Int = candidates.size

    fun updateCandidates(newCandidates: List<DialectEntry>) {
        candidates.clear()
        candidates.addAll(newCandidates)
        notifyDataSetChanged()
    }
}