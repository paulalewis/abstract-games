package com.castlefrog.games.asg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.castlefrog.games.asg.model.Domain

/**
 */
class SelectGameTypeAdapter(val contents: List<Pair<Domain, View>>, val onItemClickListener: SelectGameTypeAdapter.OnItemClickListener) : RecyclerView.Adapter<SelectGameTypeAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameIcon: FrameLayout
        val gameText: TextView

        init {
            gameIcon = view.findViewById(R.id.gameIcon) as FrameLayout
            gameText = view.findViewById(R.id.gameName) as TextView
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.view_game_type_item, viewGroup, false)
        val holder = ViewHolder(view)
        view.setOnClickListener({ onItemClickListener.onItemClick(holder.adapterPosition) })
        return holder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val domainName = viewHolder.itemView.resources.getString(contents[position].first.type.nameRes)
        viewHolder.gameText.text = domainName
        viewHolder.gameIcon.addView(contents[position].second)
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}