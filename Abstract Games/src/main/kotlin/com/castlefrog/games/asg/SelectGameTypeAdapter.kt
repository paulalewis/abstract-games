package com.castlefrog.games.asg

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.TextView
import com.castlefrog.games.asg.model.Domain
import java.util.ArrayList

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

    override public fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_game_type_item, viewGroup, false)
        val holder = ViewHolder(view)
        view.setOnClickListener(
                {
                    onItemClickListener.onItemClick(holder.getAdapterPosition())
                }
        )
        return holder;
    }

    override public fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val domainName = viewHolder.itemView.getResources().getString(contents.get(position).first.type.nameRes)
        viewHolder.gameText.setText(domainName)
        viewHolder.gameIcon.addView(contents.get(position).second)
    }

    override public fun getItemCount(): Int {
        return contents.size()
    }
}