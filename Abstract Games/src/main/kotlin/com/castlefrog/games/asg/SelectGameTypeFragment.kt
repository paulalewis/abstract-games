package com.castlefrog.games.asg

import android.app.DialogFragment
import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.castlefrog.games.asg.model.Domain
import com.castlefrog.games.asg.model.Game
import java.util.*

/**
 * Fragment for selected the game type.
 */
class SelectGameTypeFragment : DialogFragment() {
    private var recyclerView: RecyclerView? = null
    private val contents: MutableList<Pair<String, View>>

    init {
        contents = ArrayList<Pair<String, View>>()
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        val hexIconView = HexGridView(getActivity())
        hexIconView.boardSize = 3
        hexIconView.boardBackgroundColor = Color.GRAY
        contents.add(Pair<String, View>(getString(R.string.hex), hexIconView))
        val havannahIconView = HexGridView(getActivity())
        havannahIconView.boardSize = 3
        havannahIconView.boardBackgroundColor = Color.GRAY
        contents.add(Pair<String, View>(getString(R.string.havannah), havannahIconView))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater.inflate(R.layout.fragment_select_game_type, container, false)
        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        recyclerView?.setLayoutManager(GridLayoutManager(getActivity(), 3))
        recyclerView?.setAdapter(SelectGameTypeAdapter(contents, object: SelectGameTypeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val domain = Domain(contents.get(position).first)
                val game = Game("default", domain, 0)
                GameActivity.navigate(getActivity(), game)
            }
        }))
        return view;
    }
}